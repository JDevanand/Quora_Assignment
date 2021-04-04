package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public UserAuthTokenEntity authenticate(final String authorization) throws AuthenticationFailedException {

        //Basic dXNlcjFAZW1haWwuY29tOnVzZXIxIQ==
        //above is a sample encoded text where the username is "username" and
        // password is "password" seperated by a ":"
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        String userName= decodedArray[0];
        String password = decodedArray[1];

        UserEntity userEntity = userDao.getUserByUsername(userName);

        if(userEntity==null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }
        //encrypt the user provided password
        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        //check the user provided pwd with user-entity pwd from db
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);
            userAuthToken.setUuid((UUID.randomUUID().toString()));

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthToken);

            return userAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
    }

}

