package hexlet.code.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String BEARER = "Bearer";
    private static final String BAD_CREDENTIALS_MSG = "No Token Provided";

    public TokenAuthenticationFilter(RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) {
        var authenticationToken = this.getToken(request.getHeader(HttpHeaders.AUTHORIZATION))
                .orElseThrow(() -> new BadCredentialsException(BAD_CREDENTIALS_MSG));
        return getAuthenticationManager()
                .authenticate(authenticationToken);
    }

    @Override
    public void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws ServletException, IOException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    private Optional<UsernamePasswordAuthenticationToken> getToken(String bearer) {
        return Optional.ofNullable(bearer)
                .map(auth -> auth.replaceFirst("^" + BEARER, ""))
                .map(String::trim)
                .map(token -> new UsernamePasswordAuthenticationToken(token, token));
    }
}
