package hac.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ApplicationConfig  {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder bCryptPasswordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password(bCryptPasswordEncoder.encode("password"))
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("admin")
                .password(bCryptPasswordEncoder.encode("password"))
                .roles("ADMIN")
                .build());
        manager.createUser(User.withUsername("useradmin")
                .password(bCryptPasswordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                // if you have a REST api, you may want to uncomment the following lines
                // otherwise you will get a 403 error when trying to access the api
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**") // assuming you have an API at /api/**
//                )
                .csrf(withDefaults())

                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/css/**", "/", "/403", "/errorpage", "/simulateError").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN") // the ** represents any subpath
                                .requestMatchers("/user/**").hasRole("USER")
                                .requestMatchers("/shared/**").hasAnyRole("USER", "ADMIN")
                )
                .formLogin((form) -> form
                                .loginPage("/login")
//                                .loginProcessingUrl("/login")   // if you want to implement the login controller instead of using the default one
                               .defaultSuccessUrl("/", true)   // if you want to redirect to a default controller after login (for example the controller may check ROLES and redirect accordingly)
//                                .failureUrl("/")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())

                // comment out these lines if you want to use the default error page:
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling
                                .accessDeniedPage("/403")
                )

        ;

        return http.build();

    }


// instead of defining open path in the method above you can do it here:
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/favicon.ico");
    }

}
