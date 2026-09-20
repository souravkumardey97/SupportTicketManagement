package com.supportticket.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminBootstrapIT extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createsSingleAdminOnEmptyDatabase() {
        Integer adminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'",
                Integer.class);
        assertEquals(1, adminCount);
    }

    @Test
    void doesNotCreateDuplicateAdminsOnRestart() {
        Integer adminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'",
                Integer.class);
        assertEquals(1, adminCount);
    }
}
