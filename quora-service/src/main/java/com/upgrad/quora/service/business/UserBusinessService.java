package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.common.UnexpectedException;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        if (userDao.isUsernameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if (userDao.isEmailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        UserEntity signUpUser = userDao.createUser(userEntity);

        return signUpUser;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity  signin(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(username);
        try {
            if (userEntity == null) {
                throw new AuthenticationFailedException("ATH-001", "This username does not exist");
            }

            final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
            if (encryptedPassword.equals(userEntity.getPassword())) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                UserAuthTokenEntity userAuthEntity = new UserAuthTokenEntity();

                final ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);

                userAuthEntity.setUuid(UUID.randomUUID().toString());
                userAuthEntity.setUser(userEntity);
                userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
                userAuthEntity.setExpiresAt(expiresAt);
                userAuthEntity.setLoginAt(now);

                userDao.createAuthToken(userAuthEntity);

                return userAuthEntity;
            } else {
                throw new AuthenticationFailedException("ATH-002", "Password failed");
            }

        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            GenericErrorCode genericErrorCode = GenericErrorCode.GEN_001;
            throw new UnexpectedException(genericErrorCode, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signOut(final String bearerAcccessToken) throws SignOutRestrictedException, NullPointerException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(bearerAcccessToken);
        //if the access token doesnt exist in the database it will throw an error with below message
        //else if the access token exists in the database the logout time will be updated and persisted in the database
        if (userAuthToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthToken.setLogoutAt(now);
            userDao.updateUserLogoutAt(userAuthToken);
            return userAuthToken;
        }
    }

    public UserAuthTokenEntity getUserByToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthByToken = userDao.getUserAuthToken(accessToken);
        if (userAuthByToken == null) {
            throw new AuthorizationFailedException("ATH-001", "User has not signed in");
        } else if (userAuthByToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATH-002", "User is signed out.Sign in first to get user details");
        }
        return userAuthByToken;
    }

    public UserEntity getUserById(final String userUuid) throws UserNotFoundException {
        UserEntity userEntity = userDao.getUserById(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }
}



