package org.example.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String email = decodedToken.getEmail();
                String uid = decodedToken.getUid();

                // Extract custom claims for role. Default to USER.
                String role = "USER";
                Object roleClaim = decodedToken.getClaims().get("role");
                if (roleClaim != null) {
                    role = roleClaim.toString().toUpperCase();
                }

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

                // Create authentication token
                FirebaseAuthenticationToken authentication = new FirebaseAuthenticationToken(email, uid, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (FirebaseAuthException e) {
                // Token is invalid or expired
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                // Firebase might not be initialized yet etc.
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
