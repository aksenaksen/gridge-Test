package com.example.instagram.common.security;

public enum UserRole {
    USER("USER")
    ,ADMIN("ADMIN");

    private static final String ROLE_PREFIX = "ROLE_";

    private final String roleName;

    UserRole(String role){
        this.roleName = role;
    }

    public static UserRole from(String role) {
        for (UserRole memberRole : values()) {
            if (memberRole.roleName.equals(role)) {
                return memberRole;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }

    public String getRoleName(){
        return roleName;
    }

    public String getRoleWithPrefix() {
        return ROLE_PREFIX+ getRoleName();
    }
}