package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Get access token entity using access token string provided by user
    public UserAuthTokenEntity getUserAuthToken(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",
                    UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    //Create access token when signing in
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    //User sign out update
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity userSignOut(final UserAuthTokenEntity userAuthTokenEntity){
        entityManager.merge(userAuthTokenEntity);
        return  userAuthTokenEntity;
    }

}
