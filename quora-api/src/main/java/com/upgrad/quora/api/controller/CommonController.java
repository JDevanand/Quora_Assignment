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

    @RequestMapping(path="/userprofile/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> getUserProfile (@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonBusinessService.getUser(userUuid,accessToken);

        return new ResponseEntity<UserEntity>(userEntity, HttpStatus.FOUND);
    }

}
