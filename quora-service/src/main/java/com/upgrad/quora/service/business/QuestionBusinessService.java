package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    public List<QuestionEntity> getAllQuestions(String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();
    }

    public List<QuestionEntity> getQuestionbyUserId(final String accessToken, String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }

        UserEntity searchUserEntity = userDao.getUser(userUuid);
        if(searchUserEntity==null){
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getQuestionByUserId(userUuid);
    }

    public QuestionEntity getQuestionById(final String questionUuid) throws InvalidQuestionException {
        try {
            return questionDao.getQuestionByQuestionId(questionUuid);
        }catch (NoResultException nre){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }
    }

    //Create question
    public QuestionEntity createQuestion(String accessToken, String questionContent) throws AuthorizationFailedException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionContent);
        questionEntity.setUser(loggedUserAuthTokenEntity.getUser());
        questionEntity.setQuestionCreatedDate(new Date());

        return questionDao.createQuestion(questionEntity);
    }

    //Delete question by its UUid
    public QuestionEntity deleteQuestion(String accessToken, String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question");
        }

        QuestionEntity questionEntityToBeDeleted = questionDao.getQuestionByQuestionId(questionUuid);
        if(questionEntityToBeDeleted==null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        boolean isAdmin = loggedUserAuthTokenEntity.getUser().getRole().equals("admin");
        boolean isOwner = questionEntityToBeDeleted.getUser().getUuid().equals(loggedUserAuthTokenEntity.getUser().getUuid());

        if(!isAdmin) {
            if (!isOwner) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        return questionDao.deleteQuestion(questionEntityToBeDeleted);
    }

    //Edit question
    public QuestionEntity editQuestion(String accessToken, String questionUuid, String questionContent) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit the question");
        }

        QuestionEntity questionEntityToBeUpdated = questionDao.getQuestionByQuestionId(questionUuid);
        if(questionEntityToBeUpdated==null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        questionEntityToBeUpdated.setContent(questionContent);
        boolean isOwner = questionEntityToBeUpdated.getUser().getUuid().equals(loggedUserAuthTokenEntity.getUser().getUuid());

        if (!isOwner) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        return questionDao.updateQuestion(questionEntityToBeUpdated);

    }

}
