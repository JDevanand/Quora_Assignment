package com.upgrad.quora.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    //Create question endpoint
    @RequestMapping(path="question/create",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String accessToken, final QuestionRequest questionRequest) throws AuthorizationFailedException {


        QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(accessToken, questionRequest.getContent());

        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(createdQuestionEntity.getUuid());
        questionResponse.setStatus("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    //Get all questions endpoint
    @RequestMapping(path="question/all",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken) throws JsonProcessingException, AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestions(accessToken);
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<>();

        for (QuestionEntity questionEntity : questionEntities) {
            QuestionDetailsResponse qdr = new QuestionDetailsResponse();
            qdr.setId(questionEntity.getUuid());
            qdr.setContent(questionEntity.getContent());
            questionDetailsResponse.add(qdr);
        }

        return new ResponseEntity<>(questionDetailsResponse, HttpStatus.OK);
    }

    //Edit question endpoint
   @RequestMapping(path="/question/edit/{questionId}",method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionUuid, final QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity editedQuestionEntity = questionBusinessService.editQuestion(accessToken,questionUuid,questionEditRequest.getContent());

        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        questionEditResponse.setId(editedQuestionEntity.getUuid());
        questionEditResponse.setStatus("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
   }

   //Delete question endpoint
   @RequestMapping(path="/question/delete/{questionId}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity deletedQuestionEntity = questionBusinessService.deleteQuestion(accessToken,questionUuid);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(deletedQuestionEntity.getUuid());
        questionDeleteResponse.setStatus("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse,HttpStatus.OK);
   }

   //Get question by userid endpoint
    @RequestMapping(path="question/all/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserid(@RequestHeader("authorization") String accessToken, @PathVariable("userId") String userUuid) throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> questionEntities = questionBusinessService.getQuestionbyUserId(accessToken, userUuid);
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity questionEntity:questionEntities) {
            QuestionDetailsResponse qdr = new QuestionDetailsResponse();
            qdr.setId(questionEntity.getUuid());
            qdr.setContent(questionEntity.getContent());
            questionDetailsResponse.add(qdr);
        }

        return new ResponseEntity<>(questionDetailsResponse, HttpStatus.OK);
    }
}
