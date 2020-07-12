package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* Service annotation - It is used to mark the class as a service provider, is a specialization of @Component annotation */
@Service
public class QuestionBusinessService {
    /* Autowired QuestionDao class that performs DB related queries of questions required by this service class */
    @Autowired
    private QuestionDao questionDao;

    /* Autowired UserDao class that performs DB related queries of users required by this service class */
    @Autowired
    private UserDao userDao;

    /* Transactional Annotation with Propagation - same transaction will propagate from a transactional caller
     * createQuestion function returns QuestionEntity that has been created
     * Takes in QuestionEntity object , authorization
     * Authorization (when user is signed out or if user has not signed in at all is performed here
     * If no exception is thrown, question is created and user whose authorization is done is set to the question
     *
     *  */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUser(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    /*
     * getAllQuestions function returns List of QuestionEntity objects from DB
     * Takes in authorization (access token)
     * Authorization (when user is signed out or if user has not signed in at all is performed here
     * If no exception is thrown, all questions are retrieved from DB and returned
     *
     *  */
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        List<QuestionEntity> listOfQuestions  = questionDao.getAllQuestions();
        return listOfQuestions;
    }
}
