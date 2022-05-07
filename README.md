# a simple Spring security application

The application has 3 areas for the user to access:
- /admin for ADMIN roles
- /shared for ADMIN and USER roles
- /user for USER roles

User credentials are defined the Configuration class.
ADMIN role: jim, password: demo
USER role: bob, password demo
ADMIN and USER role: ted, password demo

The example includes:
- a custom error page (defined in Configuration + controller).
- a (disabled) custom login page (uses login.html in the templates).
To enable the custom login page, uncomment the corresponding
code in the Configuration class and in the controller.

