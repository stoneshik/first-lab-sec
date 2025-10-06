package lab.blps.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lab.blps.dto.MessageResponseDto;
import lab.blps.exceptions.ResourceIsAlreadyExistsException;
import lab.blps.exceptions.ResourceNotFoundException;
import lab.blps.exceptions.TokenRefreshException;
import lab.blps.security.bd.entities.RefreshToken;
import lab.blps.security.bd.entities.Role;
import lab.blps.security.bd.entities.RoleEnum;
import lab.blps.security.bd.entities.User;
import lab.blps.security.dto.request.LogoutRequestDto;
import lab.blps.security.dto.request.SignInRequestDto;
import lab.blps.security.dto.request.SignUpRequestDto;
import lab.blps.security.dto.request.TokenRefreshRequestDto;
import lab.blps.security.dto.request.TokenRefreshResponseDto;
import lab.blps.security.dto.response.JwtResponseDto;
import lab.blps.security.jwt.JwtUtils;
import lab.blps.security.model.UserDetailsImpl;
import lab.blps.security.repositories.RoleRepository;
import lab.blps.security.repositories.UserRepository;
import lab.blps.security.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${cors.urls}")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthorizationController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDto.getLogin(),
                loginRequestDto.getPassword()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        List<String> roles = userDetails
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return ResponseEntity.ok(
            new JwtResponseDto(
                jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                roles
            )
        );

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService
            .findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String token = jwtUtils.generateTokenFromUsername(user.getLogin());
                return ResponseEntity.ok(new TokenRefreshResponseDto(token, requestRefreshToken));
            })
            .orElseThrow(() ->
                new TokenRefreshException(requestRefreshToken, "Токен обновления не в базе данных!")
            );
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.getLogin())) {
            throw new ResourceIsAlreadyExistsException("Ошибка: Логин уже занят");
        }
        User user = new User(
            signUpRequest.getLogin(),
            encoder.encode(signUpRequest.getPassword())
        );
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository
                .findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Ошибка: Роль не найдена"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository
                        .findByName(RoleEnum.ROLE_ADMIN)
                        .orElseThrow(() -> new ResourceNotFoundException("Ошибка: Роль не найдена"));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository
                        .findByName(RoleEnum.ROLE_USER)
                        .orElseThrow(() -> new ResourceNotFoundException("Ошибка: Роль не найдена"));
                    roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponseDto("Пользователь успешно зарегистрирован!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequestDto logOutRequest) {
        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        return ResponseEntity.ok(new MessageResponseDto("Пользователь успешно вышел!"));
    }
}
