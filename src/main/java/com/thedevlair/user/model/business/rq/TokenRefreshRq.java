package com.thedevlair.user.model.business.rq;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRq(@NotBlank String refreshToken) {

}