package com.epitech.epitrello.Security.Controller;

import com.epitech.epitrello.Security.Logout.Logout;
import com.epitech.epitrello.Security.Logout.LogoutRepository;
import com.epitech.epitrello.Security.Service.JwtUserDetailsService;
import com.epitech.epitrello.Security.Config.JwtTokenUtil;
import com.epitech.epitrello.Security.UserDTO;
import com.epitech.epitrello.Security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping(path="/epitrello")
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogoutRepository logoutRepository;

    @PostMapping(value = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestParam String username, @RequestParam String password) throws Exception {
        authenticate(username, password);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> loginResponse = new HashMap<>();
        loginResponse.put("token", token);
        return ResponseEntity.ok(loginResponse);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@RequestParam String username, @RequestParam String password) {
        if (userRepository.existsByUsername(username))
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        UserDTO user = new UserDTO();
        user.setUsername(username);
        user.setPassword(password);
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        Logout logout = new Logout();
        logout.setToken(requestTokenHeader.substring(7));
        logoutRepository.save(logout);
        return ResponseEntity.ok("User disconnected");
    }
}
