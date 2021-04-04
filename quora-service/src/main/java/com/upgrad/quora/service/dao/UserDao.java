package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

 @Repository
 public class UserDao {

        //Autowired - is application managed entity manager in MVC framework
        //Persistence context is container managed entity manager and is different from mvc
        @PersistenceContext
        private EntityManager entityManager;

        //Create User
        @Transactional(propagation = Propagation.REQUIRED)
        public UserEntity createUser(UserEntity userEntity){
            entityManager.persist(userEntity);
            return userEntity;
        }

        //Delete User
        @Transactional(propagation = Propagation.REQUIRED)
        public UserEntity deleteUser(UserEntity userEntity){
            entityManager.remove(userEntity);
            return userEntity;
        }

        //Get user by uuid
        public UserEntity getUser(final String userUuid){
            try {
                return entityManager.createNamedQuery("userByUuid",UserEntity.class)
                        .setParameter("uuid",userUuid)
                        .getSingleResult();
            } catch (NoResultException nre){
                return null;
            }
        }

        //Get user by email id
        public UserEntity getUserByEmail(final String email) {
            try {
                return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email)
                        .getSingleResult();
            } catch (NoResultException nre) {
                return null;
            }
        }

        //Get user by username
        public UserEntity getUserByUsername(final String username) {
            try {
                return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username)
                        .getSingleResult();
            } catch (NoResultException nre) {
                return null;
            }
        }

        //Update user entity
        @Transactional(propagation = Propagation.REQUIRED)
        public void updateUser(final UserEntity updatedUserEntity){
            entityManager.merge(updatedUserEntity);
        }

        //Get access token using access token
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

