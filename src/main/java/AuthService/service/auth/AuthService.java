package AuthService.service.auth;

import AuthService.constants.RoleType;
import AuthService.dto.auth.LoginRequest;
import AuthService.dto.auth.RegisterRequest;
import AuthService.dto.person.PersonDTO;
import AuthService.exceptions.AuthException;
import AuthService.security.PersonDetails;
import AuthService.security.service.JwtService;
import AuthService.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final PersonService personService;

    private void assertBlockCondition(PersonDetails personDetails) {
        if (personDetails.isBlocked()) {
            throw new AuthException("Error! User is blocked!");
        }
    }

    private void assertPasswordEqual(String password, String confirmPassword) {
        if (!Optional.ofNullable(password).equals(Optional.ofNullable(confirmPassword))) {
            throw new AuthException("Error! Passwords is not equals");
        }
    }

    private String buildJwtToken(PersonDetails personDetails) {
        assertBlockCondition(personDetails);
        return jwtService.generateJwtToken(personDetails);
    }

    private PersonDTO buildCustomer(RegisterRequest request) {
        return PersonDTO.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .roles(Collections.singleton(RoleType.ROLE_USER))
                .build();
    }

    public String login(LoginRequest request){
        Authentication authentication = authenticationProvider
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return buildJwtToken(personDetails);
    }

    public String createCustomer(RegisterRequest request) {
        assertPasswordEqual(request.getPassword(), request.getConfirmPassword());
        return buildJwtToken(PersonDetails.build(personService.createPerson(buildCustomer(request))));
    }

}
