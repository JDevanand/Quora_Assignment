package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    //Get all answers to a question
    public List<AnswerEntity> getAnswerByQuestionId(final String accessToken, String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");
        }

        QuestionEntity questionEntity= questionDao.getQuestionByQuestionId(questionUuid);
        if(questionEntity==null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        return answerDao.getAnswerByQuestionId(questionUuid);
    }

    public AnswerEntity getAnswerById(final String answerUuid){

        return answerDao.getAnswerById(answerUuid);

    }
    //Create answer
    public AnswerEntity createAnswer(String accessToken,String questionUuid, String answerContent) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
        }

        QuestionEntity questionEntityToBeAnswered = questionDao.getQuestionByQuestionId(questionUuid);
        if(questionEntityToBeAnswered==null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setQuestion(questionEntityToBeAnswered);
        answerEntity.setAnswer(answerContent);
        answerEntity.setAnswerCreatedDate(new Date());
        answerEntity.setUser(loggedUserAuthTokenEntity.getUser());

        return answerDao.createAnswer(answerEntity);
    }

    //edit answer
    public AnswerEntity editAnswer(String accessToken,String answerUuid, String updatedAnswerContent) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answerEntityToBeUpdated = answerDao.getAnswerById(answerUuid);
        if(answerEntityToBeUpdated ==null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        answerEntityToBeUpdated.setAnswer(updatedAnswerContent);
        boolean isOwner = answerEntityToBeUpdated.getUser().getUuid().equals(loggedUserAuthTokenEntity.getUser().getUuid());

        if (!isOwner) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        return answerDao.updateAnswer(answerEntityToBeUpdated);
    }

    //delete answer
    public AnswerEntity deleteAnswer(String accessToken, String answerUuid) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
        }

        AnswerEntity answerEntityToBeDeleted = answerDao.getAnswerById(answerUuid);
        if(answerEntityToBeDeleted==null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        boolean isAdmin = loggedUserAuthTokenEntity.getUser().getRole().equals("admin");
        boolean isOwner = answerEntityToBeDeleted.getUser().getUuid().equals(loggedUserAuthTokenEntity.getUser().getUuid());

        if(!isAdmin) {
            if (!isOwner) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        return answerDao.deleteAnswer(answerEntityToBeDeleted);
    }
}
