package com.supportticket.service;

import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;
import com.supportticket.persistence.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserSummaryResponse> listUsers(Role role) {
        List<com.supportticket.persistence.entity.UserEntity> users =
                role == null ? userRepository.findAll() : userRepository.findByRole(role);
        return users.stream()
                .map(TicketMapper::toUserSummary)
                .toList();
    }
}
