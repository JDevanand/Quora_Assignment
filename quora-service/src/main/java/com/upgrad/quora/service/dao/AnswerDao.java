package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Get all answer of a specific question
    public List<AnswerEntity> getAnswerByQuestionId(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("answerByQuestionId", AnswerEntity.class).setParameter("quuid", questionUuid)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Get answer of a specific answer uuid
    public AnswerEntity getAnswerById(final String answerUuid) {
        try {
            return entityManager.createNamedQuery("answerById", AnswerEntity.class).setParameter("uuid", answerUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Create answer in database
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    //update answer in database
    @Transactional(propagation = Propagation.REQUIRED)
    public  AnswerEntity updateAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    //delete answer in database
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
        return answerEntity;
    }
}


