package com.thedevlair.user.service.impl;


import com.thedevlair.user.dao.RefreshTokenRepository;
import com.thedevlair.user.dao.UserRepository;
import com.thedevlair.user.exception.type.ForbiddenException;
import com.thedevlair.user.exception.type.InternalServerErrorException;
import com.thedevlair.user.exception.type.NotFoundException;
import com.thedevlair.user.mapper.RefreshTokenMapper;
import com.thedevlair.user.model.business.RefreshToken;
import com.thedevlair.user.model.business.rs.RefreshTokenRs;
import com.thedevlair.user.model.thirdparty.RefreshTokenDTO;
import com.thedevlair.user.model.thirdparty.UserDTO;
import com.thedevlair.user.security.jwt.JwtService;
import com.thedevlair.user.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;
    private final UserRepository userRepository;
    private final JwtService jwtUtils;

    @Value("${theDevLair.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, RefreshTokenMapper refreshTokenMapper, UserRepository userRepository, JwtService jwtUtils) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenMapper = refreshTokenMapper;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenMapper.dtoToRefreshToken(refreshTokenRepository.findByToken(token));
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();

        Optional<UserDTO> userDTO = userRepository.findById(userId);

        if (userDTO.isEmpty()) {
            throw new NotFoundException("Not found user");
        }

        refreshTokenDTO.setUser(userDTO.get());
        refreshTokenDTO.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenDTO.setToken(UUID.randomUUID().toString());

        try {
            return refreshTokenMapper.dtoToRefreshToken(refreshTokenRepository.save(refreshTokenDTO));
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error processing request");
        }

    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.deleteByToken(refreshToken.getToken());
            throw new ForbiddenException(refreshToken.getToken(),
                    "Refresh token was expired. Please make a new signIn request");
        }

        return refreshToken;
    }

    @Transactional
    public void deleteByUserId(Long userId) {

        try {
            Optional<UserDTO> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                throw new NotFoundException("Not found user.");
            }

            refreshTokenRepository.deleteByUser(optionalUser.get());
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error processing request.");
        }

    }

    @Override
    public ResponseEntity<RefreshTokenRs> refreshToken(RefreshToken refreshToken) {
        String rfToken = refreshToken.getRefreshToken();

        return Optional.of(refreshTokenRepository.findByToken(rfToken))
                .map(this::validateExpiration)
                .map(RefreshTokenDTO::getUser)
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getUsername());
                    return ResponseEntity.ok(new RefreshTokenRs(token, rfToken));
                })
                .orElseThrow(() -> new ForbiddenException(rfToken,
                        "Refresh token is not in database!"));

    }

    private RefreshTokenDTO validateExpiration(RefreshTokenDTO refreshTokenDTO) {
        if (refreshTokenDTO.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshTokenDTO);
            throw new ForbiddenException(refreshTokenDTO.getToken(),
                    "Refresh token was expired. Please make a new signIn request");
        }
        return refreshTokenDTO;
    }
}
