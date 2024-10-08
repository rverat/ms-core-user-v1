package com.thedevlair.user.service.impl;

import com.thedevlair.user.dao.RoleRepository;
import com.thedevlair.user.dao.UserRepository;
import com.thedevlair.user.dao.UserRoleRepository;
import com.thedevlair.user.exception.type.ConflictDataException;
import com.thedevlair.user.exception.type.InternalServerErrorException;
import com.thedevlair.user.exception.type.NotFoundException;
import com.thedevlair.user.mapper.UserMapper;
import com.thedevlair.user.model.business.ERole;
import com.thedevlair.user.model.business.RefreshToken;
import com.thedevlair.user.model.business.User;
import com.thedevlair.user.model.business.rq.LoginRequest;
import com.thedevlair.user.model.business.rq.UpdateUserPasswordRq;
import com.thedevlair.user.model.business.rs.JwtRs;
import com.thedevlair.user.model.business.rs.MessageRs;
import com.thedevlair.user.model.business.rs.UserRs;
import com.thedevlair.user.model.thirdparty.RoleDTO;
import com.thedevlair.user.model.thirdparty.UserDTO;
import com.thedevlair.user.security.jwt.JwtService;
import com.thedevlair.user.security.service.UserDetailsImpl;
import com.thedevlair.user.service.RefreshTokenService;
import com.thedevlair.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final UserMapper userMapper;

    private final AuthenticationManager authenticationManager;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;
    private final JwtService jwtUtils;

    private final RefreshTokenService refreshTokenService;

    @Override
    public ResponseEntity<JwtRs> authenticate(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtRs(userDetails.getId(),
                userDetails.getUsername(),
                userDetails.isEnabled(),
                roles,
                jwt,
                "Bearer",
                refreshToken.getToken()));
    }

    @Override
    public ResponseEntity<MessageRs> sessionValidate(String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.substring("Bearer ".length());

            // Validate the token using the JwtUtils class
            boolean isValidToken = jwtUtils.validateToken(token);

            if (isValidToken) {
                return ResponseEntity.ok(new MessageRs("Token is valid."));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageRs("Invalid token."));
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("Error processing request.");
        }
    }

    @Override
    public ResponseEntity<UserRs> getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<UserDTO> optionalUser = userRepository.findById(userDetails.getId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found.");
        }
        return ResponseEntity.ok(userMapper.dtoToUserRs(optionalUser.get()));
    }

    @Override
    public ResponseEntity<UserRs> getUserById(Long id) {

        Optional<UserDTO> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User with ID %d not found.", id);
        }
        return ResponseEntity.ok(userMapper.dtoToUserRs(optionalUser.get()));
    }

    @Override
    public ResponseEntity<MessageRs> create(User user) {

        if (Boolean.TRUE.equals(userRepository.existsByUsername(user.getUsername()))) {
            throw new ConflictDataException("Error: Username is already taken!");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(user.getEmail()))) {
            throw new ConflictDataException("Error: Email is already in use!");
        }

        UserDTO userDTO = userMapper.userToDto(user);

        userDTO.setRoles(getUserRoles(userDTO));
        userDTO.setCreatedAt(new Date());
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        userRepository.save(userDTO);

        return ResponseEntity.ok(new MessageRs("Registered user successfully!"));

    }

    @Override
    public ResponseEntity<MessageRs> update(User user) {

        Optional<UserDTO> userFromDB = userRepository.findById(user.getId());

        if (userFromDB.isEmpty()) {
            throw new NotFoundException("Not found user");
        }

        UserDTO userDTOFromDB = userFromDB.get();

        if (!userDTOFromDB.getUsername().equals(user.getUsername()) &&
                (Boolean.TRUE.equals(userRepository.existsByUsername(user.getUsername())))) {
            throw new ConflictDataException("Error: Username is already taken!");

        }

        if (!userDTOFromDB.getEmail().equals(user.getEmail()) &&
                (Boolean.TRUE.equals(userRepository.existsByEmail(user.getEmail())))) {
            throw new ConflictDataException("Error: Email is already in use!");

        }

        UserDTO userDTO = userMapper.userToDto(user);

        userDTO.setRoles(getUserRoles(userDTO));
        userDTO.setCreatedAt(userDTOFromDB.getCreatedAt()); //The creation date should not change
        userDTO.setPassword(userDTOFromDB.getPassword()); //Password is kept
        userDTO.setUpdatedAt(new Date());
        userRepository.save(userDTO);

        return ResponseEntity.ok(new MessageRs("Updated user details successfully!"));


    }

    private Set<RoleDTO> getUserRoles(UserDTO userDTO) {

        Set<RoleDTO> roles = userDTO.getRoles();

        if (roles.isEmpty()) {
            // Add the default role ROLE_USER if the list is empty
            return Set.of(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
        }
        // Map the Set of Roles to a List of RoleDTOs
        return roles.stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found.")))
                .collect(Collectors.toSet());
    }

    @Override
    public ResponseEntity<MessageRs> updatePassword(String token, UpdateUserPasswordRq passwordRq) {

        // Retrieve the currently authenticated user's details
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), passwordRq.oldPassword()));

        if (!authentication.isAuthenticated()) {
            throw new NotFoundException("Not found user.");
        }

        // Check if the user exists
        Optional<UserDTO> optionalUser = userRepository.findById(userDetails.getId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Not found user.");
        }

        UserDTO user = optionalUser.get();

        user.setPassword(encoder.encode(passwordRq.newPassword()));
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        this.logout(token);

        return ResponseEntity.ok(new MessageRs("Updated user password successfully!"));
    }

    @Override
    public ResponseEntity<MessageRs> logout(String token) {

        String jwtToken = token.substring("Bearer ".length());

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = userDetails.getId();

        refreshTokenService.deleteByUserId(userId);
        JwtService.invalidateToken(jwtToken);
        return ResponseEntity.ok(new MessageRs("Log out successful!"));
    }

    @Override
    public ResponseEntity<MessageRs> delete(Long id) {
        try {
            userRoleRepository.deleteByUser_Id(id); //before delete user, we need delete roles assigned for this user
            refreshTokenService.deleteByUserId(id);
            userRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error processing request.");
        }
        return ResponseEntity.ok(new MessageRs("Delete user successful!"));
    }
}
