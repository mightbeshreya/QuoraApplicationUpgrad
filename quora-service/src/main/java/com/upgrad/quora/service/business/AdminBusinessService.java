package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* Service annotation - It is used to mark the class as a service provider, is a specialization of @Component annotation */
@Service
public class AdminBusinessService {
    /* Autowired UserDao class that performs DB related queries required by this service class */
    @Autowired
    private UserDao userDao;

    /* Transactional Annotation with Propagation - same transaction will propagate from a transactional caller
    * deleteUser function returns string
    * Takes in userId , authorization
    * Authorization (when user is signed out or if user has not signed in at all is performed here or user
    * role is nonadmin,
    * When user is not found by the userId, UserNotFoundException is thrown
    * If no exception is thrown, user is deleted from DB
    * returns deleted user's uuid
    *  */
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userId, final String authorization)
            throws UserNotFoundException, AuthorizationFailedException {
        /* Authorize */
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        if(!userAuthTokenEntity.getUser().getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
        String deletedUserUuid = userDao.deleteUserByUserId(userId);
        if(deletedUserUuid==null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        return deletedUserUuid;
    }
}
