package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.UserEntityRequestDto;
import com.runaumov.spring.cloudfilestorage.dto.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.entity.UserEntity;
import com.runaumov.spring.cloudfilestorage.mapper.UserEntityMapper;
import com.runaumov.spring.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

    @Autowired
    public AuthService(UserRepository userRepository, UserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Transactional
    public UserEntityResponseDto registerUser(UserEntityRequestDto dto) {
        UserEntity savedUser = userRepository.save(userEntityMapper.toUserEntity(dto));
        return userEntityMapper.toUserEntityResponseDto(savedUser);
    }
}
