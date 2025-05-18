package com.sanjot.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, 
                          UserDetailsService userDetailsService) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String newPassword = "Password123"; // change this
//        String hashedPassword = encoder.encode(newPassword);
//        System.out.println("New Hashed Password: " + hashedPassword);

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/SignUp","/ContactUs","/AboutUs", "/signup", "/LoginPage", "/css/**", "/js/**", "/image/**").permitAll()
                .requestMatchers("/dashboard").hasAnyRole("ADMIN", "STAFF") // Both can access dashboard
                 .requestMatchers("/inventory").hasRole("ADMIN")
                .requestMatchers("/departments").hasRole("ADMIN") // Only Admin
// Only Admin

                .requestMatchers("/requests").hasAnyRole("STAFF","ADMIN") // Only Staff
                .requestMatchers( "/issued_history", "/settings").hasAnyRole("ADMIN", "STAFF") // Both can access
                .requestMatchers("/categories",  "/stock_reports").hasRole("ADMIN")
                .requestMatchers("/inventory/request-form").hasRole("STAFF") // Adjust role as needed
                .requestMatchers("/transactions/request").hasRole("ADMIN") // Only Staff can request
                .requestMatchers("/stock-report").hasRole("ADMIN") // Only Admin
                .requestMatchers("/user-list").hasRole("ADMIN") 
                .requestMatchers("/return_request").hasRole("STAFF")
// Only Admin
//                .requestMatchers("/user_form").hasRole("STAFF") // Only Admin


                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/LoginPage")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/LoginPage?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/logout") // Disable CSRF for logout
            );

        return http.build();
    }
    }
