package com.thedevlair.expose.web;

import com.thedevlair.user.model.business.RefreshToken;
import com.thedevlair.user.model.business.User;
import com.thedevlair.user.model.business.rq.LoginRequest;
import com.thedevlair.user.model.business.rq.UpdateUserPasswordRq;
import com.thedevlair.user.model.business.rs.JwtRs;
import com.thedevlair.user.model.business.rs.MessageRs;
import com.thedevlair.user.model.business.rs.RefreshTokenRs;
import com.thedevlair.user.model.business.rs.UserRs;
import com.thedevlair.user.service.RefreshTokenService;
import com.thedevlair.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/ms-core-users/v1")
@Tag(name
        = "User Controller", description = "Operations related to users")
public class UserController {

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signIn")
    @Operation(summary = "Authenticate user with username and password")
    public ResponseEntity<JwtRs> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        return userService.authenticate(loginRequest);

    }

    @GetMapping("/getUser")
    @Operation(summary = "Get user info logged in")
    public ResponseEntity<UserRs> getUser() {

        return userService.getUser();
    }

    @Operation(summary = "Get a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRs.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<UserRs> getUserById(@PathVariable("userId") Long userId) {

        return userService.getUserById(userId);

    }

    @PostMapping("/sessionValidate")
    @Operation(summary = "Validate user session")
    public ResponseEntity<MessageRs> sessionValidate(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                         String authorizationHeader) {

        return userService.sessionValidate(authorizationHeader);
    }

    @PostMapping("/signUp")
    @Operation(summary = "Create user")
    public ResponseEntity<MessageRs> registerUser(@Valid @RequestBody User user) {

        return userService.create(user);
    }

    @PatchMapping()
    @Operation(summary = "Update user")
    public ResponseEntity<MessageRs> updateUser(@Valid @RequestBody User user) {

        return userService.update(user);

    }

    @PatchMapping("/updatePassword")
    @Operation(summary = "Update user password")
    public ResponseEntity<MessageRs> updateUserPass(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @Valid @RequestBody UpdateUserPasswordRq passwordRq) {

        return userService.updatePassword(authorizationHeader, passwordRq);

    }

    @PostMapping("/refreshToken")
    @Operation(summary = "Create access token from refresh token")
    public ResponseEntity<RefreshTokenRs> refreshToken(@Valid @RequestBody RefreshToken refreshToken) {

        return refreshTokenService.refreshToken(refreshToken);

    }

    @PostMapping("/signOut")
    @Operation(summary = " Logout user session")
    public ResponseEntity<MessageRs> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        return userService.logout(authorizationHeader);

    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by userId")
    public ResponseEntity<MessageRs> deleteUser(@PathVariable("userId") Long userId) {
        return userService.delete(userId);
    }
}
