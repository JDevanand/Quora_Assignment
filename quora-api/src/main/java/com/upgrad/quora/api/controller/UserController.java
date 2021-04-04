package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    //Signup
    @RequestMapping(path = "/user/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> createUser(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = userBusinessService.createUser(userEntity);

        SignupUserResponse signupUserResponse = new SignupUserResponse();
        signupUserResponse.setId(createdUserEntity.getUuid());
        signupUserResponse.setStatus("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(signupUserResponse,HttpStatus.CREATED);
    }

    //USER SIGN IN
    @RequestMapping(path = "/user/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignIn(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.userSignIn(authorization);

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthTokenEntity.getUser().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

        //Access token to be sent in header which will be used in request from client
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token",userAuthTokenEntity.getAccessToken());

        return new ResponseEntity<SigninResponse>(signinResponse,headers,HttpStatus.OK);
    }


    //User sign out
    @RequestMapping(path = "/user/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignOut(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {

        UserAuthTokenEntity loggedUsersAuthToken = userBusinessService.userSignOut(accessToken);

        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.setId(loggedUsersAuthToken.getUser().getUuid());
        signoutResponse.setMessage("Signed out successfully");

        return  new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.OK);
    }

}
