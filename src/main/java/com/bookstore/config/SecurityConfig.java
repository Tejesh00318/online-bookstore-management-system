package com.bookstore.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
    MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

    http
      .csrf(csrf -> csrf
        .ignoringRequestMatchers(PathRequest.toH2Console()) // allow H2 console posts
      )
      .headers(headers -> headers
        .frameOptions(frame -> frame.sameOrigin()) // H2 console frames
      )
      .authorizeHttpRequests(auth -> auth
        // H2 console servlet
        .requestMatchers(PathRequest.toH2Console()).permitAll()

        // Public MVC endpoints
        .requestMatchers(
          mvc.pattern("/"),
          mvc.pattern("/home"),
          mvc.pattern("/books/**"),
          mvc.pattern("/category/**"),
          mvc.pattern("/login"),
          mvc.pattern("/register"),
          mvc.pattern("/about"),
          mvc.pattern("/contact"),
          mvc.pattern("/css/**"),
          mvc.pattern("/js/**"),
          mvc.pattern("/images/**")
        ).permitAll()

        // Admin area
        .requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")

        // Authenticated user areas
        .requestMatchers(
          mvc.pattern("/user/**"),
          mvc.pattern("/cart/**"),
          mvc.pattern("/orders/**"),
          mvc.pattern("/profile/**")
        ).hasAnyRole("USER","ADMIN")

        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/dashboard", true)
        .failureUrl("/login?error=true")
        .usernameParameter("username")
        .passwordParameter("password")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/login?logout=true")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .permitAll()
      );

    return http.build();
  }
}
