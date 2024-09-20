package com.thedevlair.user.model.business.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtRs {
    private Long id;
    private String username;
    private boolean isEnabled;
    private List<String> roles;
    private String token;
    private String type = "Bearer";
    private String refreshToken;
}
