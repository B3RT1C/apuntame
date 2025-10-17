package com.apuntame.backend.constant;

public class JwtConstants {

    // Header key
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Token prefix
    public static final String BEARER_PREFIX = "Bearer ";

    // Api endpoints
    public static final String AUTH_BASE_ENDPOINT = "/api/auth";
    public static final String LOGIN_ENDPOINT = AUTH_BASE_ENDPOINT + "/login";

    private JwtConstants() {}
}
