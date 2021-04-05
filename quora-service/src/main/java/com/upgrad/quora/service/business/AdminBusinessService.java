package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.ZonedDateTime.now;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    //Delete user only if the admin's access token is provided
    //Else it throws exceptions for Authorization failures and if the User is not found
    public UserEntity deleteUser(final String userUuid, final String accessToken) throws UserNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userAuthDao.getUserAuthToken(accessToken);

        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(!(loggedUserAuthTokenEntity.getUser().getRole().equals("admin"))){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        if(loggedUserAuthTokenEntity.getLogoutAt()!=null || (loggedUserAuthTokenEntity.getExpiresAt().compareTo(now())<0)){
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }

        UserEntity userEntityToBeDeleted =  userDao.getUser(userUuid);
        if(userEntityToBeDeleted==null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }
        return userDao.deleteUser(userEntityToBeDeleted);
    }

}
