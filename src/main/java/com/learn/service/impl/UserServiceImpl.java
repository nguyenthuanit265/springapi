package com.learn.service.impl;

import com.learn.model.dto.SpringSecurityUserDetailsDto;
import com.learn.model.dto.UserDto;
import com.learn.model.entity.User;
import com.learn.model.request.SignUpRequest;
import com.learn.repository.UserRepository;
import com.learn.service.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        if (ObjectUtils.isEmpty(email)) {
            return Optional.empty();
        }
//        if ("admin@gmail.com".equals(email)) {
//            return Optional.ofNullable(SpringSecurityUserDetailsDto.builder()
//                    .id(1L)
//                    .name("admin")
//                    .email(email)
//                    .password("123456")
//                    .build());
//        }
        // TODO Query DB
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return Optional.ofNullable(
                    UserDto.builder()
                            .id(user.get().getId())
                            .email(user.get().getEmail())
                            .name(user.get().getName())
                            .build()
            );
        }
        return Optional.empty();
    }

    @Override
    public void signUp(SignUpRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        String hashPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        user.setPassword(hashPassword);
        userRepository.save(user);
    }

    @Override
    public Optional<SpringSecurityUserDetailsDto> findByUsername(String username) {
        if (ObjectUtils.isEmpty(username)) {
            return Optional.empty();
        }
//        if ("admin@gmail.com".equals(email)) {
//            return Optional.ofNullable(SpringSecurityUserDetailsDto.builder()
//                    .id(1L)
//                    .name("admin")
//                    .email(email)
//                    .password("123456")
//                    .build());
//        }
        // TODO Query DB
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return Optional.ofNullable(
                    SpringSecurityUserDetailsDto.builder()
                            .id(user.get().getId())
                            .email(user.get().getEmail())
                            .name(user.get().getName())
                            .build()
            );
        }
        return Optional.empty();
    }
}
