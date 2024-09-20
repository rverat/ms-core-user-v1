package com.thedevlair.user.model.business.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record LoginRequest(@NotBlank
                           @Size(min = 3, max = 20)
                           @Pattern(regexp = "^\\w+$", message = "Username can only contain alphanumeric characters and underscores")
                           String username,
                           @NotBlank
                           @Size(min = 8, max = 16)
                           String password) {

}
