package com.thedevlair.user.model.business;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole name;

}
