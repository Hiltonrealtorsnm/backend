package com.realestate.realestate.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public JwtAuthenticationToken(Object principal, Object credentials,
                                  java.util.Collection authorities) {
        super(principal, credentials, authorities);
    }
}
