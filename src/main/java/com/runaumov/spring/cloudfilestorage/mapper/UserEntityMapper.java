package com.runaumov.spring.cloudfilestorage.mapper;

import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityRequestDto;
import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.mapper.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    UserEntity toUserEntity(UserEntityRequestDto dto);

    default UserEntityResponseDto toUserEntityResponseDto(UserEntity user) {
        return new UserEntityResponseDto(user.getUsername());
    }
}
