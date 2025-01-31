package workoutlog.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends GenericFilterBean {

  private JwtHandler jwtHandler;

  public JwtAuthorizationFilter(RequestMatcher requestMatcher, JwtHandler jwtHandler) {
    this.jwtHandler = jwtHandler;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String jwtToken = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
    log.info("attemptAuthentication token:{}", jwtToken);
    if (jwtToken != null) {
      try {
        Authentication authentication = jwtHandler.getAuthentication(jwtToken.substring(7));
        log.info(
            "authentication name:{}", authentication.getName());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (ExpiredJwtException e) {
        throw new CredentialsExpiredException(e.getMessage());
      } catch (Exception exp) {
        log.error("Erro trying to validate jwt token", exp);
      }
    }
    chain.doFilter(request, response);
  }

}
