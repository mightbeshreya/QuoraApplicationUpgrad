package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/* RestController Annotation - Controller + RequestBody annotations */
@RestController
/* Request Mapping is done */
@RequestMapping("/")
/* AdminController is added and autowired with adminBusinessService service */
public class AdminController {
    @Autowired
    private AdminBusinessService adminBusinessService;

    /* method - DELETE for userDelete (Deletes the user)
        Path mapped to - /admin/user/{userId}
        produces JSON
        Takes in Path variable 'userId' with annotation PathVariable & Request header variable 'authorization'
        with annotation RequestHeader
        throws AuthorizationFailedException - when user is not signed in or when user is signed out (Authorization fails)
        throws UserNotFoundException - when user is not found with the userId
    *  */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userId,
                                                         @RequestHeader("authorization") final String authorization)
    throws AuthorizationFailedException, UserNotFoundException {
        /* getting deleted user uuid from deleteUser method in  AdminBusinessService */
        String  deletedUserUuid = adminBusinessService.deleteUser(userId, authorization);
        /* If no error occurs, user id deleted successfully - setting Id and status */
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deletedUserUuid)
                .status("USER SUCCESSFULLY DELETED");
        /* Response Entity UserDeleteResponse is returned with deleted user's uuid and HttpStatus.OK status  */
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
