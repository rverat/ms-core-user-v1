package com.thedevlair.user.model.business;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RefreshToken {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Instant expiryDate;

    public RefreshToken(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
