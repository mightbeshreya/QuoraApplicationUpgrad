package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserProfileBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/* RestController annotation - It's a convenience annotation that combines @Controller and @ResponseBody  */
@RestController
/* RequestMapping annotation is added */
@RequestMapping("/")
/*Added the CommonController class */
public class CommonController {
    /* Autowiring the UserProfileBusinessService service class that authorizes the user
       and gets the user details
     */
    @Autowired
    private UserProfileBusinessService userProfileBusinessService;

    /* GET method - userProfile, path is mapped to /userprofile/{userId} , produces JSON reply
    *   Need Path variable 'userId' annotated with PathVariable and Request header 'authorization'
    *   annotated with RequestHeader as arguments
    *   throws AuthorizationFailedException - when user is not signed in or when user is signed out (Authorization fails)
    *   throws UserNotFoundException - when user is not found with the userId
    * */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userId,
                   @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {
        /* Get userEntity which contains details of user if no errors */
        UserEntity userEntity = userProfileBusinessService.getUser(userId, authorization);
        /* Attach the user details to UserDetailsResponse object  */
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).userName(userEntity.getUserName())
                .emailAddress(userEntity.getEmail()).country(userEntity.getCountry())
                .aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob())
                .contactNumber(userEntity.getContactNumber());
        /* return the response entity userDetailsResponse */
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}
