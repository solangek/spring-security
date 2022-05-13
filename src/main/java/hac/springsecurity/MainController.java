package hac.springsecurity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unbescape.html.HtmlEscape;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Locale;

@Controller
public class MainController {

    /** Home page. */
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /** User zone index. */
    @RequestMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    /** Administration zone index.
     * Note that we can access current logged user just by adding the Principal
     * parameter
     */
    @RequestMapping("/admin")
    public String adminIndex(Principal principal) {
        System.out.println("Current logged user details: " + principal.getName());
        return "admin/index";
    }

    /** Shared zone index. */
    @RequestMapping("/shared")
    public String sharedIndex() {
        // print current logged user details
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Current logged user details: "
                + ((UserDetails) principal).getUsername()
                + " (" + ((UserDetails) principal).getAuthorities() + ")");


        return "shared/index";
    }

    /** Simulation of an exception. */
    @RequestMapping("/simulateError")
    public void simulateError() {
        throw new RuntimeException("This is a simulated error message");
    }

    /** Error page. */
    @RequestMapping("/error.html")
    public String error(HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", "Error " + request.getAttribute("javax.servlet.error.status_code"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<ul>");
        while (throwable != null) {
            errorMessage.append("<li>").append(HtmlEscape.escapeHtml5(throwable.getMessage())).append("</li>");
            throwable = throwable.getCause();
        }
        errorMessage.append("</ul>");
        model.addAttribute("errorMessage", errorMessage.toString());
        return "error";
    }

    /** Error page. */
    @RequestMapping("/403")
    public String forbidden() {
        return "403";
    }

    /***** UNCOMMENT THE NEXT 2 METHODS TO USE A CUSTOM LOGIN PAGE *****/
    /***** ALSO uncomment the corresponding code in the ApplicationConfig class *****/
    /** Login form with error. */
//    @RequestMapping("/login-error")
//    public String loginError(Model model) {
//        model.addAttribute("loginError", true);
//        return "login";
//    }
//    /** Login form. */
//    @RequestMapping("/login")
//    public String login() {
//        return "login";
//    }
    /************************** END OF CUSTOM LOGIN **********************/

}