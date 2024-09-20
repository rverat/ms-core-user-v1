package com.thedevlair.user.mapper;

import com.thedevlair.user.model.business.User;
import com.thedevlair.user.model.business.rs.UserRs;
import com.thedevlair.user.model.thirdparty.UserDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToUser(UserDTO userDTO);

    UserRs dtoToUserRs(UserDTO userDTO);

    UserDTO userToDto(User user);

}
