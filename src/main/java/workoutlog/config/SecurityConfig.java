package workoutlog.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import workoutlog.repository.UserRepository;
import workoutlog.security.JwtAuthorizationFilter;
import workoutlog.security.JwtHandler;

@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @AllArgsConstructor
  private enum FreeEndpoint {
    USER_REGISTER(HttpMethod.POST, "/user"),
    LOGIN(HttpMethod.POST, "/login"),
    ;

    private HttpMethod http;
    private String url;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity httpSecurity, JwtHandler jwtHandler) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(
            auth -> {
              for (FreeEndpoint freeEP : FreeEndpoint.values()) {
                log.info("securityFilterChain http:{} url:{}", freeEP.http, freeEP.url);
                auth.requestMatchers(freeEP.http, freeEP.url).permitAll();
              }
              auth.anyRequest().authenticated();
            })
        // .addFilterBefore(jwtAuthorizationFilter(jwtHandler), LogoutFilter.class)
        .addFilterAfter(jwtAuthorizationFilter(jwtHandler), ExceptionTranslationFilter.class)
        .csrf(custom -> custom.disable())
        .cors(cors -> cors.disable())
        .httpBasic(Customizer.withDefaults())
        .requestCache(cache -> cache.disable())
        .sessionManagement(session -> session.disable())
        .rememberMe(rm -> rm.disable())
        .formLogin(fl -> fl.disable())
        .build();
  }

  private JwtAuthorizationFilter jwtAuthorizationFilter(JwtHandler jwtHandler) {
    List<RequestMatcher> requestMatchers = new ArrayList<>();
    Stream.of(FreeEndpoint.values())
        .map(fe -> AntPathRequestMatcher.antMatcher(fe.http, fe.url))
        .forEach(requestMatchers::add);

    RequestMatcher matcher = new RequestMatcher() {
      @Override
      public boolean matches(HttpServletRequest request) {
        return !(new OrRequestMatcher(requestMatchers).matches(request));
      }
    };
    return new JwtAuthorizationFilter(matcher, jwtHandler);
  }

  @Bean
  public UserDetailsService userDetailsServiceBean(final PasswordEncoder passwordEncoder,
      final UserRepository userRepository) {
    return username -> {
      return userRepository.findByEmail(username)
          .map(user -> new User(user.email(), user.password(), AuthorityUtils.NO_AUTHORITIES))
          .orElseThrow(() -> new UsernameNotFoundException(username));
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService detailsService, PasswordEncoder encoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(encoder);
    provider.setUserDetailsService(detailsService);
    return new ProviderManager(provider);
  }

}
