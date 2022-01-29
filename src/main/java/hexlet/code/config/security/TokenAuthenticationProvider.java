package hexlet.code.config.security;

import hexlet.code.model.User;
import hexlet.code.service.UserAuthenticationService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

// провайдер получает пользователя из БД по его токену, иначе 401
@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private static final String ROLE = "USER";

    @Autowired
    private UserAuthenticationService authService;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
    }

    // провайдер получает пользователя из БД по его токену. authService вычленяет из токена email и по факту
    // поиск по email. Наш пользователь преобразуется в спрингового пользователя, который и возвращается.
    // По спринговому пользователю Spring Security понимает, что он действующий и ему доступны определённые функции
    @Override
    protected UserDetails retrieveUser(
            String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        try {
            return this.authService.findByToken(token)
                    .map(this::buildUserDetails) // билдим по нашему User`у спрингового User`а
                    .orElse(null);
        } catch (JwtException e) {
            // все AuthenticationException перехватываются кастомным обработчиком SecurityConfig.handleError
            // который вернёт 401 - UNAUTHORIZED
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private UserDetails buildUserDetails(final User user) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                // роли не используются в приложении, поэтому предполагаем, что у всех пользователей одна роль "USER"
                // т.к. список пустым быть не может по ограничения Spring
                return List.of(new SimpleGrantedAuthority(ROLE));
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getEmail();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true; // т.к. не используется
            }

            @Override
            public boolean isAccountNonLocked() {
                return true; // т.к. не используется
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true; // т.к. не используется
            }

            @Override
            public boolean isEnabled() {
                return true; // т.к. не используется
            }
        };
    }
}
