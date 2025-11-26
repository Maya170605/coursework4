package com.example.curs4.service;

import com.example.curs4.dto.UserDTO;
import com.example.curs4.entity.Role;
import com.example.curs4.entity.Unp;
import com.example.curs4.entity.User;
import com.example.curs4.exception.CustomException;
import com.example.curs4.mapper.UserMapper;
import com.example.curs4.repository.UnpRepository;
import com.example.curs4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UnpRepository unpRepository;
    private final VerificationService verificationService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // CREATE
    public UserDTO register(UserDTO dto) {
        log.info("Регистрация пользователя: {}", dto.getUsername());

        if (dto.getRole() == Role.ADMIN) {
            throw new CustomException("Роль администратора недоступна для регистрации");
        }

        validateUser(dto);

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setVerified(true);

        if (dto.getRole() == Role.CLIENT) {
            boolean isVerified = verificationService.verifyUNP(dto.getUnp());
            user.setVerified(isVerified);

            Unp unp = unpRepository.findByUnp(dto.getUnp())
                    .orElseThrow(() -> new CustomException("УНП не найден в справочнике"));
            user.setUnp(unp);
        } else if (dto.getRole() == Role.DRIVER) {
            user.setName(null);
            user.setUnp(null);
            user.setActivityType(null);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("Поиск пользователя с ID: {}", id);

        // Используем стандартный метод findById из JpaRepository
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new CustomException("Пользователь с ID " + id + " не найден");
                });

        log.info("Найден пользователь: {}", user.getUsername());
        UserDTO userDTO = userMapper.toDto(user);
        log.info("DTO создан: {}", userDTO);

        return userDTO;
    }
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // Метод с пагинацией (если нужен)
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Пользователь не найден"));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // UPDATE
    public UserDTO updateUser(Long id, UserDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("Пользователь с ID " + id + " не найден"));

        // Обновляем только разрешенные поля
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getName() != null) {
            existingUser.setName(dto.getName());
        }
        if (dto.getActivityType() != null) {
            existingUser.setActivityType(dto.getActivityType());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    // DELETE
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("Пользователь с ID " + id + " не найден"));
        userRepository.delete(user);
    }

    // Валидация
    private void validateUser(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException("Пользователь с таким логином уже существует");
        }

        if (dto.getRole() == Role.CLIENT) {
            String unpValue = dto.getUnp();

            if (unpValue == null || unpValue.trim().isEmpty()) {
                throw new CustomException("УНП обязателен для клиента");
            }

            if (!verificationService.verifyUNP(unpValue)) {
                throw new CustomException("УНП не прошёл валидацию (должно быть 9 цифр)");
            }

            if (unpRepository.findByUnp(unpValue).isEmpty()) {
                throw new CustomException("УНП не найден в справочнике");
            }

            if (userRepository.existsByUnp_Unp(unpValue)) {
                throw new CustomException("Пользователь с таким УНП уже существует");
            }

            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                throw new CustomException("Название компании обязательно для клиента");
            }
        }

        if (dto.getRole() == Role.DRIVER && dto.getName() != null && !dto.getName().trim().isEmpty()) {
            throw new CustomException("Название компании не должно указываться для водителя");
        }
    }

    // Дополнительные методы
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}