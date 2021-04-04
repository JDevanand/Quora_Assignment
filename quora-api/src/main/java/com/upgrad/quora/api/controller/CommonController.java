package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;
    //User profile
    /*This endpoint is used to get the details of any user in the Quora Application.
    This endpoint can be accessed by any user in the application.

    It should be a GET request
    This endpoint must request the path variable 'userId' as a string for the corresponding user profile to be retrieved and access token of the signed in user as a string in authorization Request Header.
    If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException' with the message code - 'ATHR-001' and message - 'User has not signed in'.
    If the user has signed out, throw "AuthorizationFailedException" with the message code -'ATHR-002' and message -'User is signed out.Sign in first to get user details' .
    If the user with uuid whose profile is to be retrieved does not exist in the database, throw 'UserNotFoundException' with the message code -'USR-001' and message -'User with entered uuid does not exist'.
    Else, return all the details of the user from the database in the JSON response with the corresponding HTTP status.
    */

    @RequestMapping(path="/userprofile/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> getUserProfile (@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonBusinessService.getUser(userUuid,accessToken);

        return new ResponseEntity<UserEntity>(userEntity, HttpStatus.FOUND);
    }

}
