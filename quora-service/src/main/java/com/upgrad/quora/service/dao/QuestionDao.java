package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    //Autowired - is application managed entity manager in MVC framework
    //Persistence context is container managed entity manager and is different from mvc
    @PersistenceContext
    private EntityManager entityManager;

    //create
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    //get all questions
    public List<QuestionEntity> getAllQuestions(){

        return entityManager.createNamedQuery("getAllQuestions",QuestionEntity.class).getResultList();
    }

    //get question by question id
    public QuestionEntity getQuestionByQuestionId(final String questionId){
        try {
            return entityManager.createNamedQuery("questionByQuestionId", QuestionEntity.class).setParameter("id", questionId)
                    .getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    //get  questions by UserId
    public List<QuestionEntity> getQuestionByUserId(String userUuid) {
        try {
            List<QuestionEntity> questionEntities = entityManager.createNamedQuery("questionByUserId", QuestionEntity.class).setParameter("useruuid", userUuid)
                    .getResultList();
            return questionEntities;
        } catch (NoResultException nre){
            return null;
        }
    }

    //delete
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(QuestionEntity questionEntity) {
        try {
            entityManager.remove(questionEntity);
            return questionEntity;
        } catch (NoResultException nre){
            return null;
        }
    }

    //update
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(QuestionEntity questionEntity){
        try{
            entityManager.merge(questionEntity);
            return questionEntity;
        } catch (NoResultException nre){
            return null;
        }
    }


}
