package workoutlog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtHandler {

  JwtProperties properties;

  SecretKey secretKey;

  private static final String AUTHORITIES_ROLE = "role";

  public JwtHandler(JwtProperties properties) {
    this.properties = properties;
  }

  @PostConstruct
  public void init() {
    String encoded = Base64.getEncoder().encodeToString(properties.getKey().getBytes());
    secretKey = Keys.hmacShaKeyFor(encoded.getBytes(StandardCharsets.UTF_8));
  }

  public String generate(Authentication authentication) {
    ClaimsBuilder builder = Jwts.claims().subject(authentication.getName());
    String authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    if (authorities != null && !authorities.isEmpty()) {
      builder.add(AUTHORITIES_ROLE, authorities);
    }
    return Jwts.builder()
        .claims(builder.build())
        .issuedAt(new Date())
        .signWith(secretKey, Jwts.SIG.HS256)
        .expiration(new Date(new Date().getTime() + (properties.getExpiration() * 1000)))
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims payload =
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    log.debug("getAuthentication expiration:{}", payload.getExpiration());
    Object roleClaim = payload.get(AUTHORITIES_ROLE);
    Collection<? extends GrantedAuthority> authorities =
        roleClaim != null
            ? AuthorityUtils.commaSeparatedStringToAuthorityList(roleClaim.toString())
            : AuthorityUtils.NO_AUTHORITIES;

    return new UsernamePasswordAuthenticationToken(
        new User(payload.getSubject(), "", authorities), token, authorities);
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims =
          Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token);
      // parseClaimsJws will check expiration date. No need do here.
      log.debug("expiration date: {}", claims.getPayload().getExpiration());
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.info("Invalid JWT token: {}", e.getMessage());
      log.trace("Invalid JWT token trace.", e);
    }
    return false;
  }
}
