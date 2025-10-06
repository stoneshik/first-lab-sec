package lab.security.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lab.security.bd.entities.User;
import lab.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User loadUserById(Long userId) throws UsernameNotFoundException {
        return userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UsernameNotFoundException("Id пользователя не найден: " + userId)
            );
    }
}
