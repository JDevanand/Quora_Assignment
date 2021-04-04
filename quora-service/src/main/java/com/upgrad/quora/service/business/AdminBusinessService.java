package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity deleteUser(final String userUuid, final String accessToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);


        if(loggedUserAuthTokenEntity ==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(loggedUserAuthTokenEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }
        if(!(loggedUserAuthTokenEntity.getUser().getRole().equals("admin"))){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userEntityToBeDeleted =  userDao.getUser(userUuid);
        if(userEntityToBeDeleted==null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }
        return userDao.deleteUser(userEntityToBeDeleted);
    }

}
