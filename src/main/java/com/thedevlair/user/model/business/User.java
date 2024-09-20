package com.thedevlair.user.model.business;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class User {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores")
    private String username;

    @Size(min = 8, max = 120)
    private String password;

    @Size(max = 80)
    @Email
    private String email;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 125)
    private String profilePicture;

    private Date createdAt;

    private Date updatedAt;

    private boolean isEnabled;

    private Set<Role> roles = new HashSet<>();

}
