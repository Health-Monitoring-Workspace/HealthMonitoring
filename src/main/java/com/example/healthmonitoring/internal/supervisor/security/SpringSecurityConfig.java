package com.example.healthmonitoring.internal.supervisor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;


@EnableWebFluxSecurity
@RequiredArgsConstructor
@Component
public class SpringSecurityConfig {

    private final ReactiveUserDetailsService service;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {

        final RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/login"));

        final RedirectServerAuthenticationSuccessHandler successHandler = new RedirectServerAuthenticationSuccessHandler();
        successHandler.setLocation(URI.create("/dashboard"));

        return http
                .csrf().disable()
                .formLogin()
                .loginPage("/login")
                .authenticationSuccessHandler(successHandler)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new ServerLogoutSuccessHandler() {
                    @Override
                    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
                        ServerHttpResponse response = exchange.getExchange().getResponse();
                        response.setStatusCode(HttpStatus.FOUND);
                        response.getHeaders().setLocation(URI.create("/login"));
                        return exchange.getExchange().getSession()
                                .flatMap(WebSession::invalidate);
                    }
                })
                .and()
                .authorizeExchange()
                .pathMatchers("/resources/**", "/css/**", "/images/**", "/js/**").permitAll()
                .pathMatchers("/login", "/about").permitAll()
                .pathMatchers("/**").authenticated()
                .anyExchange()
                .permitAll()// Except for protected paths above, the rest will be open
                .and()
                .authenticationManager(authenticationManager())
                .exceptionHandling(
                        exceptions -> exceptions
                                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login"))
                )
                // .accessDeniedPage("/403.html");
//                .and()
                .build();

    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(service);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}