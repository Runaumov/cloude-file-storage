package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.entity.UserEntity;
import com.runaumov.spring.cloudfilestorage.repository.UserRepository;
import com.runaumov.spring.cloudfilestorage.security.UserEntityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserEntityDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserEntityDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return new UserEntityDetails(userEntity);
    }
}
