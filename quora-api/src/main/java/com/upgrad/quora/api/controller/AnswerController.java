package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    //Create answer endpoint with bearer authentication
    @RequestMapping(path ="/question/{questionId}/answer/create",method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestBody final AnswerRequest answerRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String accessToken) throws InvalidQuestionException, AuthenticationFailedException, AuthorizationFailedException {


        AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(accessToken,questionUuid,answerRequest.getAnswer());

        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setId(createdAnswerEntity.getUuid());
        answerResponse.setStatus("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //Edit answer endpoint with bearer authentication
    @RequestMapping(path="/answer/edit/{answerId}",method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@PathVariable("answerId") String answerUuid, @RequestHeader("authorization")final String accessToken, @RequestBody AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity updatedAnswerEntity = answerBusinessService.editAnswer(accessToken,answerUuid,answerEditRequest.getContent());

        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        answerEditResponse.setId(updatedAnswerEntity.getUuid());
        answerEditResponse.setStatus("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //Delete answer endpoint with bearer authentication
    @RequestMapping(path="/answer/delete/{answerId}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerUuid, @RequestHeader("authorization")final String accessToken) throws AuthenticationFailedException, AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity deletedAnswerEntity = answerBusinessService.deleteAnswer(accessToken,answerUuid);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse();
        answerDeleteResponse.setId(deletedAnswerEntity.getUuid());
        answerDeleteResponse.setStatus("ANSWER DELETED");

        return  new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }

    //Get All the answer to a question endpoint with bearer authentication
    @RequestMapping(path="/answer/all/{questionId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswerToQuestion(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> answerEntityList = answerBusinessService.getAnswerByQuestionId(accessToken, questionUuid);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();

        for(AnswerEntity answerEntity : answerEntityList){
            AnswerDetailsResponse adr = new AnswerDetailsResponse();
            adr.setId(answerEntity.getUuid());
            adr.setQuestionContent(answerEntity.getQuestion().getContent());
            adr.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponses.add(adr);
        }

        return  new ResponseEntity<>(answerDetailsResponses, HttpStatus.OK);
    }

}
