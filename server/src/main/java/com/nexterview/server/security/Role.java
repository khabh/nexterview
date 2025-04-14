package com.nexterview.server.security;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    GUEST("ROLE_GUEST");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public boolean matches(String roleName) {
        return this.roleName.equals(roleName);
    }

    public static Role from(String roleName) {
        if (USER.matches(roleName)) {
            return USER;
        }
        return GUEST;
    }
}
