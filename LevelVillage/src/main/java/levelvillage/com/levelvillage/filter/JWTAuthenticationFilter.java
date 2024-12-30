package levelvillage.com.levelvillage.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import levelvillage.com.levelvillage.service.UserService;
import levelvillage.com.levelvillage.util.JWTTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTTokenUtil tokenUtil;
    private final UserService userService;

    @Autowired
    public JWTAuthenticationFilter(JWTTokenUtil tokenUtil, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // Log and continue the filter chain for requests that don't have a valid Authorization header
            System.out.println("Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token and username
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String username = tokenUtil.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;

            try {
                userDetails = userService.loadUserByUsername(username); // Replaceable with your UserDetailsService implementation
            } catch (UsernameNotFoundException e) {
                System.out.println("User not found: " + username);
                filterChain.doFilter(request, response); // Continue without setting authentication
                return;
            }

            // Validate the token
            if (tokenUtil.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the Authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Invalid token for username: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }

}
