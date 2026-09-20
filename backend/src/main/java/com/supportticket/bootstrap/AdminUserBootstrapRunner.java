package com.supportticket.bootstrap;

import com.supportticket.config.AppProperties;
import com.supportticket.domain.Role;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminUserBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public AdminUserBootstrapRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.countByRole(Role.ADMIN) == 0) {
            createAdminIfConfigured();
        }
        if (userRepository.countByRole(Role.USER) == 0) {
            createUserAssigneeIfConfigured();
        }
    }

    private void createAdminIfConfigured() {
        String username = appProperties.getBootstrap().getAdmin().getUsername();
        String password = appProperties.getBootstrap().getAdmin().getPassword();
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("app.bootstrap.admin.username is required when no ADMIN user exists");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException(
                    "APP_BOOTSTRAP_ADMIN_PASSWORD is required when no ADMIN user exists in the database");
        }
        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Bootstrap created ADMIN user '{}'", username);
    }

    private void createUserAssigneeIfConfigured() {
        String username = appProperties.getBootstrap().getUser().getUsername();
        if (!StringUtils.hasText(username)) {
            log.warn("No USER accounts exist and app.bootstrap.user.username is not set; assignee creation skipped");
            return;
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        String rawPassword = appProperties.getBootstrap().getUser().getPassword();
        if (!StringUtils.hasText(rawPassword)) {
            rawPassword = username + "-bootstrap-change-me";
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        userRepository.save(user);
        log.info("Bootstrap created USER assignee '{}'", username);
    }
}
