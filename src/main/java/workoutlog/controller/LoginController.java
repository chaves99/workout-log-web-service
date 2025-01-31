package workoutlog.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import workoutlog.payload.request.LoginRequest;
import workoutlog.payload.response.LoginResponse;
import workoutlog.security.JwtHandler;

@RestController
public record LoginController(AuthenticationManager autenticationManager, JwtHandler jwtHandler) {

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    Authentication authentication = autenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    return new LoginResponse(jwtHandler.generate(authentication));
  }
}
