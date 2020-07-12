package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
