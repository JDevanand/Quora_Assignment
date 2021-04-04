package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    //Create question
    @RequestMapping(path="question/create",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String accessToken, final QuestionRequest questionRequest) throws AuthorizationFailedException {


        QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(accessToken, questionRequest.getContent());

        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(createdQuestionEntity.getUuid());
        questionResponse.setStatus("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    //Get all questions
    @RequestMapping(path="question/all",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(@RequestHeader("authorization") final String accessToken){

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestions();
        QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
        for (QuestionEntity questionEntity:questionEntities) {
            questionDetailsResponse.setId(questionEntity.getUuid());
            questionDetailsResponse.setContent(questionEntity.getContent());
        }

        return new ResponseEntity<QuestionDetailsResponse>(questionDetailsResponse,HttpStatus.OK);
    }

    //edit question
   @RequestMapping(path="/question/edit/{questionId}",method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionUuid, final QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity editedQuestionEntity = questionBusinessService.editQuestion(accessToken,questionUuid,questionEditRequest.getContent());

        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        questionEditResponse.setId(editedQuestionEntity.getUuid());
        questionEditResponse.setStatus("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
   }

   //delete question
   @RequestMapping(path="/question/delete/{questionId}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity deletedQuestionEntity = questionBusinessService.deleteQuestion(accessToken,questionUuid);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(deletedQuestionEntity.getUuid());
        questionDeleteResponse.setStatus("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse,HttpStatus.OK);
   }

   //Get question by userid
    @RequestMapping(path="question/all/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getQuestionByUserid(@RequestHeader("authorization") String accessToken, @PathVariable("userId") String uuid) throws AuthenticationFailedException {

            UserAuthTokenEntity userAuthToken = authenticationService.authenticate(accessToken);
            UserEntity loggedUser = userAuthToken.getUser();

            if(uuid.equals(loggedUser.getUuid())) {
                List<QuestionEntity> questionEntities = questionBusinessService.getQuestionbyUserId(uuid);


                QuestionResponse questionResponse = new QuestionResponse();
                //questionResponse.setId();
                //questionResponse.setStatus();

                //return the question data to response entity
                return new ResponseEntity<>(questionResponse, HttpStatus.OK);
            } else {
                return null;
            }
    }

}
