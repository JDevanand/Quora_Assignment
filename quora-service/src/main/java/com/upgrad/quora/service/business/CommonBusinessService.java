package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    //Fetch the details of a user based on the user uuid after bearer authentication
    public UserEntity getUser(String userUuid, String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userAuthDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        UserEntity userEntity= userDao.getUser(userUuid);

        if(userEntity == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }

        return userEntity;
    }
}
