package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* Service annotation - It is used to mark the class as a service provider, is a specialization of @Component annotation */
@Service
public class UserProfileBusinessService {
    /* Autowired UserDao class that performs DB related queries required by this service class */
    @Autowired
    private UserDao userDao;

    /* Authorization (when user is signed out or if user has not signed in at all is performed here,
    *   When user is not found by the userId, UserNotFoundException is thrown
    *   If no exception is thrown, the UserEntity object obtained by querying through userId is returned
    *  */
    public UserEntity getUser(final String userId, final String authorization)
            throws UserNotFoundException, AuthorizationFailedException {
        /* Authorization - getting UserAuthToken Entity through authorization header */
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        UserEntity userEntity = userDao.getUserById(userId);
        if(userEntity==null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }
}
