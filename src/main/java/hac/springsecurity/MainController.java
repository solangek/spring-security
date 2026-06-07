package hac.springsecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.security.Principal;

/**
 * MVC controller that serves the demo's pages.
 *
 * Because it is annotated with {@code @Controller} (not {@code @RestController}), every handler
 * method returns a *view name* as a String. Spring's view resolver maps that name to a Thymeleaf
 * template under src/main/resources/templates/ — e.g. returning "user/index" renders
 * templates/user/index.html.
 *
 * Notice there is no access-control code in here: which role is required for each URL is declared
 * centrally in {@link ApplicationConfig}. This controller's only job is to choose which page to show.
 */
@Controller
public class MainController {

    // SLF4J logging facade; Spring Boot routes it to Logback (the default) under the hood.
    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    /** Home page. */
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /** Login page — rendered by our custom template referenced from ApplicationConfig.formLogin(). */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** User zone index. */
    @RequestMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    /** Administration zone index.
     * Note that we can access current logged user just by adding the Principal
     * parameter
     *
     * How it works: Spring Security resolves a {@link Principal} (or Authentication) method
     * parameter automatically from the security context of the current request — so simply
     * declaring the parameter is enough to obtain the logged-in user; no manual lookup needed.
     */
    @RequestMapping("/admin")
    public String adminIndex(Principal principal) {
        System.out.println("Current logged user details: " + principal );
        return "admin/index";
    }

    /** Shared zone index.
     *
     * Alternative to the Principal parameter used in adminIndex(): read the authenticated user
     * straight from the thread-bound {@link SecurityContextHolder}. This approach works anywhere,
     * even in service/helper classes that never receive the controller method parameters.
     * getPrincipal() returns the {@link UserDetails} object our InMemoryUserDetailsManager
     * built for this account at login time.
     */
    @RequestMapping("/shared")
    public String sharedIndex() {
        // print current logged user details - withouth using the Principal parameter
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Current logged user details: "
                + ((UserDetails) principal).getUsername()
                + " (" + ((UserDetails) principal).getAuthorities() + ")");


        return "shared/index";
    }

    /** Simulation of an exception. Throwing here lets us demonstrate the exception handling below. */
    @RequestMapping("/simulateError")
    public void simulateError() {
        throw new RuntimeException("This is a simulated exception thrown by a controller");
    }

    /** Error page that displays all exceptions.
     *  The thrown Exception is received as a parameter and exposed to the view through the
     *  {@link Model} (Spring's bag of attributes handed to the template).
     */
    @RequestMapping("/errorpage")
    public String error(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    /** simple Error page. ApplicationConfig's accessDeniedPage("/403") forwards here on a 403. */
    @RequestMapping("/403")
    public String forbidden() {
        return "403";
    }

    /**
     * Catches any Exception thrown by a handler method in THIS controller and turns it into a
     * rendered error page. {@code @ExceptionHandler} declares what is caught; {@code @ResponseStatus}
     * sets the HTTP status code (500 Internal Server Error) returned to the browser.
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {

        //logger.error("Exception during execution of SpringSecurity application", ex);
        String errorMessage = (ex != null ? ex.getMessage() : "Unknown error");

        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }


}
