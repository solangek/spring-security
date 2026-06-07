package hac.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Central Spring Security configuration.
 *
 * {@code @Configuration} marks this class as a source of {@code @Bean} definitions. At startup
 * Spring Security looks for the three beans we declare here and wires them together:
 *   1. UserDetailsService  -> where the user accounts come from
 *   2. PasswordEncoder     -> how passwords are hashed (and later verified)
 *   3. SecurityFilterChain -> the rules: who may reach which URL, plus login / logout behaviour
 */
@Configuration
public class ApplicationConfig  {

    /**
     * Declares the application's user accounts.
     *
     * {@link InMemoryUserDetailsManager} keeps the users in memory, which is ideal for a teaching
     * demo. A real application would instead load users from a database (a JDBC- or JPA-backed
     * UserDetailsService). Note the passwords are never stored in clear text: each is run through
     * the {@link PasswordEncoder} (injected automatically by Spring) before being saved. The roles
     * assigned here are exactly what the authorization rules in filterChain() check against.
     */
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
                .roles("USER", "ADMIN")   // this account holds BOTH roles at once
                .build());
        return manager;
    }

    /**
     * The password-hashing strategy. BCrypt is a deliberately slow, salted hash and is the
     * recommended default for storing passwords: even if the user store leaks, the original
     * passwords cannot be trivially recovered. The same bean both encodes the passwords above
     * and verifies the password a visitor types into the login form.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * The heart of the configuration: builds the chain of servlet filters that EVERY HTTP request
     * passes through. This is where authentication (who are you?) and authorization (what are you
     * allowed to reach?) are configured.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS (cross-origin requests) using Spring's sensible defaults.
                .cors(withDefaults())
                // if you have a REST api, you may want to uncomment the following lines
                // otherwise you will get a 403 error when trying to access the api
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**") // assuming you have an API at /api/**
//                )
                // CSRF protection is ON by default. Every state-changing form (POST/PUT/DELETE)
                // must submit a CSRF token; the Thymeleaf templates include it as a hidden field.
                .csrf(withDefaults())

                // Authorization rules. They are evaluated TOP TO BOTTOM and the FIRST matching
                // pattern wins, so order the entries from most specific to most general.
                .authorizeHttpRequests((requests) -> requests
                                // Public paths — anyone may reach these without logging in:
                                .requestMatchers("/css/**", "/favicon.ico", "/", "/403", "/errorpage", "/simulateError").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN") // the ** represents any subpath
                                .requestMatchers("/user/**").hasRole("USER")
                                .requestMatchers("/shared/**").hasAnyRole("USER", "ADMIN")   // either role is enough
                                // Tip: a common hardening step is to finish the list with
                                // .anyRequest().authenticated() as a catch-all, so any URL not
                                // listed above still requires the visitor to be logged in.
                )
                // Use our OWN login page (served by MainController's @GetMapping("/login"))
                // instead of the page Spring Security would otherwise generate automatically.
                .formLogin((form) -> form
                                .loginPage("/login")
//                                .loginProcessingUrl("/login")   // if you want to implement the login controller instead of using the default one
                               .defaultSuccessUrl("/", true)   // if you want to redirect to a default controller after login (for example the controller may check ROLES and redirect accordingly)
//                                .failureUrl("/")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())   // expose the logout endpoint to everyone

                // comment out these lines if you want to use the default error page:
                // When a logged-in user lacks the required role Spring returns HTTP 403; this
                // redirects that case to our friendly /403 page instead of the default white page.
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling
                                .accessDeniedPage("/403")
                )

        ;

        return http.build();

    }


    // Static resources such as /favicon.ico are opened via permitAll() above.
    // Spring Security recommends permitAll() over WebSecurity.ignoring(): ignoring()
    // bypasses the whole security filter chain (no CSRF, no security headers), while
    // permitAll() still runs the chain and simply authorizes everyone for that path.

}
