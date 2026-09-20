package com.supportticket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Bootstrap bootstrap = new Bootstrap();
    private List<String> corsAllowedOrigins = new ArrayList<>(List.of("http://localhost:5173"));

    public Jwt getJwt() {
        return jwt;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    public static class Jwt {
        private String secret;
        private long expirationMs = 86400000L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }

    public static class Bootstrap {
        private final BootstrapUser admin = new BootstrapUser();
        private final BootstrapUser user = new BootstrapUser();

        public BootstrapUser getAdmin() {
            return admin;
        }

        public BootstrapUser getUser() {
            return user;
        }
    }

    public static class BootstrapUser {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
