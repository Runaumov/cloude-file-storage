package com.runaumov.spring.cloudfilestorage.service.auth;

import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityRequestDto;
import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.mapper.entity.UserEntity;
import com.runaumov.spring.cloudfilestorage.exception.UsernameAlreadyExistException;
import com.runaumov.spring.cloudfilestorage.mapper.UserEntityMapper;
import com.runaumov.spring.cloudfilestorage.repository.UserRepository;
import com.runaumov.spring.cloudfilestorage.service.storage.MinioStorageService;
import com.runaumov.spring.cloudfilestorage.service.user.UserPathService;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final UserPathService userPathService;
    private final MinioStorageService minioStorageService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntityResponseDto registerUser(UserEntityRequestDto dto) {
        try {
            UserEntity user = userEntityMapper.toUserEntity(dto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            UserEntity savedUser = userRepository.save(user);
            createUserRootFolder(savedUser.getId());
            return userEntityMapper.toUserEntityResponseDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            if (isUniqueUsernameViolation(e)) {
                throw new UsernameAlreadyExistException("Username already exist", e);
            }
            throw e;
        }
    }

    private boolean isUniqueUsernameViolation(DataIntegrityViolationException e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                return "users_username_key".equals(cve.getConstraintName());
            }
            cause = cause.getCause();
        }
        return false;
    }

    private void createUserRootFolder(Long userId) {
        String userPrefix = userPathService.getUserPrefix(userId);

        MinioUtils.handleMinioException(() -> {
            minioStorageService.putEmptyItem(userPrefix); return null;
            }, "Failed to create user root folder");
    }
}
