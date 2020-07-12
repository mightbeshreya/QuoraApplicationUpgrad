package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/* RestController Annotation - Controller + RequestBody annotations */
@RestController
/* Request Mapping is done */
@RequestMapping("/")
/* QuestionController is added and autowired with QuestionBusinessService service */
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;

    /* method - POST for createQuestion (Creates a new question)
        Path mapped to - /question/create
        consumes JSON, produces JSON
        Takes in QuestionRequest Entity (through consumes) & Request header variable 'authorization'
        with annotation RequestHeader
        throws AuthorizationFailedException - when user is not signed in or when user is signed out (Authorization fails)
    *  */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        /* Sets all the required fields from QuestionRequest JSON */
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        /* Creating the question */
        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity,
                authorization);

        /* Creating the response - setting ID = question uuid and status with a message */
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid())
                .status("QUESTION CREATED");
        /* Return QuestionResponse with HttpStatus.CREATED status */
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }


    /* method - GET for getAllQuestions (gets all questions from DB)
        Path mapped to - /question/all
        produces JSON
        Takes in Request header variable 'authorization'
        with annotation RequestHeader
        throws AuthorizationFailedException - when user is not signed in or when user is signed out (Authorization fails)
    *  */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions
    (@RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException {
        /* Getting the list of all questions from the Service and in turn DB */
        List<QuestionEntity> listOfQuestions = questionBusinessService.getAllQuestions(authorization);
        /* Initializing QuestionDetailsResponse as linkedlist and adding each QuestionDetailsResponse entity to it */
        final List<QuestionDetailsResponse> questionDetailsResponses = new LinkedList<>() ;

        /* Run a for loop to add each question retrieved from DB */
        for(QuestionEntity q: listOfQuestions) {
            /* Setting question details (UUID and content ) to questionDetailsResponse and
            Adding in List of questionDetailsResponses  */
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(q.getUuid());
            questionDetailsResponse.setContent(q.getContent());
            questionDetailsResponses.add(questionDetailsResponse);
        }
        /* Returning list of  QuestionDetailsResponse */
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }
}
