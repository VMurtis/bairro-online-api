package br.com.fiap.api.infra.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import org.springframework.http.HttpMethod;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {

    @Bean
    public CorsConfigurationSource configuracaoCors() {
        CorsConfiguration configuracao = new CorsConfiguration();

        configuracao.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuracao.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuracao.setAllowedHeaders(
                List.of("Content-Type", "Authorization", "X-XSRF-TOKEN")
        );

        configuracao.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource origem =
                new UrlBasedCorsConfigurationSource();

        origem.registerCorsConfiguration("/**", configuracao);

        return origem;
    }


    @Bean
    public SecurityFilterChain filtrosSeguranca(HttpSecurity http) throws Exception{        return http


                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new CsrfTokenRequestAttributeHandler()
                        )
                        .ignoringRequestMatchers(
                                request -> request.getServletPath().equals("/usuarios")
                                        && request.getMethod().equals("POST")
                        )
                )

                .cors(cors -> cors.configurationSource(configuracaoCors()))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    req.requestMatchers(
                            "/login",
                            "/csrf",
                            "/css/**",
                            "/js/**",
                            "/assets/**"
                    ).permitAll();

                    req.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();

                    req.requestMatchers("/admin/**").hasRole("ADMIN");
                    req.anyRequest().authenticated();
                })

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    "{\"mensagem\":\"Login realizado com sucesso\"}"
                            );
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    "{\"mensagem\":\"E-mail ou senha inválidos\"}"
                            );
                        })
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    "{\"mensagem\":\"Logout realizado com sucesso\"}"
                            );
                        })
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe.key("lembrarDeMim")
                        .alwaysRemember(true)
                )

                .build();
    }
}
