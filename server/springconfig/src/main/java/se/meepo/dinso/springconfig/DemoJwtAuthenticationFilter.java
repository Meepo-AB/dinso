package se.meepo.dinso.springconfig;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se.meepo.dinso.database.DemoSessionService;

@Component
public class DemoJwtAuthenticationFilter extends OncePerRequestFilter {
  private final DemoSessionService sessions;
  public DemoJwtAuthenticationFilter(DemoSessionService sessions) { this.sessions = sessions; }
  @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    var authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) { chain.doFilter(request, response); return; }
    try {
      var profile = sessions.requireActive(authorization.substring(7));
      var authentication = UsernamePasswordAuthenticationToken.authenticated(profile, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_" + profile.role().name())));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      chain.doFilter(request, response);
    } catch (SecurityException exception) {
      SecurityContextHolder.clearContext(); response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); response.setContentType(MediaType.APPLICATION_JSON_VALUE); response.getWriter().write("{\"message\":\"Invalid or expired demo session\"}");
    }
  }
}
