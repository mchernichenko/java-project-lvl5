package hexlet.code.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RequestMatcher protectedUrls;
    private final RequestMatcher publicUrls;

    @Autowired
    @Lazy
    private TokenAuthenticationProvider authenticationProvider;

    /**
     * Публичные URL:
     * GET /api/users - получение списка пользователей
     * POST /api/users - создание пользователя
     * POST /api/login - получение токена для зарегистрированного пользователя
     * GET /api/statuses - получение списка статусов
     * GET /api/statuses/{id} - получение статуса по идентификатору
     * Все прочие URL не начинающиеся на /api, т.е. не относящиеся непосредственно к приложению,
     * а это h2 консоль, swagger, frontend.
     */

    public SecurityConfig(@Value("${base-url}") final String baseUrl,
                          @Lazy final TokenAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.publicUrls = new OrRequestMatcher(
                new AntPathRequestMatcher(baseUrl + "/users", HttpMethod.POST.toString()),
                new AntPathRequestMatcher(baseUrl + "/users", HttpMethod.GET.toString()),
                new AntPathRequestMatcher(baseUrl + "/statuses", HttpMethod.GET.toString()),
                new AntPathRequestMatcher(baseUrl + "/statuses/{id}", HttpMethod.GET.toString()),
                new AntPathRequestMatcher(baseUrl + "/login", HttpMethod.POST.toString()),
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );
        this.protectedUrls = new NegatedRequestMatcher(this.publicUrls);
    }

    // переопределённые правила Spring Security
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().frameOptions().sameOrigin()// решает проблему отображение H2-Console в браузере
                .and()
                .authorizeRequests() // требование авторизации для всех запросов
                .requestMatchers(this.publicUrls).permitAll() // запросы на публичные url без авторизации
                .anyRequest().authenticated() // любой запрос требует аутентификации
                .and()
                // определяем кастомный обработчик на уровне Spring Security
                .exceptionHandling().authenticationEntryPoint(this::handleError)
                .and()
                // предоставление аутентификации - т.е. получение спрингового пользователя, в нашим случае, по токену
                // если пользователь не находится, то проваливаемся в handleError
                .authenticationProvider(this.authenticationProvider)
                // когда приходит запрос, который нужно авторизовать, вызвывается фильтр, в котором будет создан
                // объект UsernamePasswordAuthenticationToken и который будет передан в провайдер
                .addFilterBefore(this.restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .sessionManagement().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }

    private void handleError(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final AuthenticationException exception) throws IOException {
        response.sendError(UNAUTHORIZED.value(), exception.getMessage()); // 401 http code
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(this.authenticationProvider);
    }

    @Bean
    public TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
        var authenticationFilter = new TokenAuthenticationFilter(this.protectedUrls);
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(this.successHandler());
        return authenticationFilter;
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler() {
        var successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy((request, response, url) -> {
        });
        return successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
