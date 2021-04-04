package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private AuthenticationService authenticationService;

    //Logic to create user
    public UserEntity createUser(UserEntity userEntity) throws SignUpRestrictedException{
        String password = userEntity.getPassword();
        if(password == null){
            userEntity.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        userEntity.setRole("nonadmin");

        //If the username provided already exists in the current database,
        // throw ‘SignUpRestrictedException’ with the
        // message code -'SGR-001' and message - 'Try any other Username, this Username has already been taken'.
        if(userDao.getUserByUsername(userEntity.getUserName())!=null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

         //If the email Id provided by the user already exists in the current database,
        // throw ‘SignUpRestrictedException’ with the
        // message code -'SGR-002' and message -'This user has already been registered, try with any other emailId'.
        if(userDao.getUserByEmail(userEntity.getEmail())!=null){
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        return userDao.createUser(userEntity);
    }

    //Logic to check and sign-in user
    public UserAuthTokenEntity userSignIn(final String authorization) throws AuthenticationFailedException {

        return authenticationService.authenticate(authorization);

    }

    //User sign out
    public UserAuthTokenEntity userSignOut(final String accessToken) throws SignOutRestrictedException {

        UserAuthTokenEntity loggedUserAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(loggedUserAuthTokenEntity ==null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in"); // found issue.. yet to be completed
        }
        loggedUserAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        return userDao.userSignOut(loggedUserAuthTokenEntity);
    }
}
