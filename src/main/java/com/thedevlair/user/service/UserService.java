package com.thedevlair.user.service;

import com.thedevlair.user.model.business.User;
import com.thedevlair.user.model.business.rq.LoginRequest;
import com.thedevlair.user.model.business.rq.UpdateUserPasswordRq;
import com.thedevlair.user.model.business.rs.JwtRs;
import com.thedevlair.user.model.business.rs.MessageRs;
import com.thedevlair.user.model.business.rs.UserRs;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<JwtRs> authenticate(LoginRequest loginRequest);

    ResponseEntity<MessageRs> sessionValidate(String authorizationHeader);

    ResponseEntity<UserRs> getUser();

    ResponseEntity<UserRs> getUserById(Long id);

    ResponseEntity<MessageRs> create(User user);

    ResponseEntity<MessageRs> update(User user);

    ResponseEntity<MessageRs> updatePassword(String token, UpdateUserPasswordRq passwordRq);

    ResponseEntity<MessageRs> logout(String token);

    ResponseEntity<MessageRs> delete(Long id);

}
