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

    @PersistenceContext
    private EntityManager entityManager;

    //create a question entity in database
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    //get all questions from database
    public List<QuestionEntity> getAllQuestions(){

        return entityManager.createNamedQuery("getAllQuestions",QuestionEntity.class).getResultList();
    }

    //get a specific question by question id
    public QuestionEntity getQuestionByQuestionId(final String questionId){
        try {
            return entityManager.createNamedQuery("questionByQuestionId", QuestionEntity.class).setParameter("id", questionId)
                    .getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    //get all questions posted by a user based on UserId
    public List<QuestionEntity> getQuestionByUserId(String userUuid) {
        try {
            List<QuestionEntity> questionEntities = entityManager.createNamedQuery("questionByUserId", QuestionEntity.class).setParameter("useruuid", userUuid)
                    .getResultList();
            return questionEntities;
        } catch (NoResultException nre){
            return null;
        }
    }

    //delete a question
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
