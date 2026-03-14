package org.example.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final String email;
    private final String uid;

    public FirebaseAuthenticationToken(String email, String uid, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.email = email;
        this.uid = uid;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email; // Use email as the principal text usually
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}
