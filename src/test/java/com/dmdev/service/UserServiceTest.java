package com.dmdev.service;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateUserMapper;
import com.dmdev.mapper.UserMapper;
import com.dmdev.validator.CreateUserValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CreateUserValidator createUserValidator;
    @Mock
    private UserDao userDao;
    @Mock
    private CreateUserMapper createUserMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void loginSuccess() {
        User user = getUser();
        UserDto userDto = getUserDto();
        doReturn(Optional.of(user)).when(userDao).findByEmailAndPassword(user.getEmail(), user.getPassword());
        doReturn(userDto).when(userMapper).map(user);

        Optional<UserDto> actualResult = userService.login(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent().contains(userDto);
    }

    @Test
    void loginFail() {
        doReturn(Optional.empty()).when(userDao).findByEmailAndPassword(any(), any());

        Optional<UserDto> actualResult = userService.login("dummy", "123");

        assertThat(actualResult).isEmpty();
        verifyNoInteractions(userMapper);
    }

    @Test
    void create() {
        CreateUserDto createUserdto = getCreateUserDto();
        User user = getUser();
        UserDto userDto = getUserDto();
        doReturn(new ValidationResult()).when(createUserValidator).validate(createUserdto);
        doReturn(user).when(createUserMapper).map(createUserdto);
        doReturn(userDto).when(userMapper).map(user);

        UserDto actualResult = userService.create(createUserdto);

        assertThat(actualResult).isEqualTo(userDto);
        verify(userDao).save(user);
    }

    @Test
    void shouldThrowExceptionIfUserDtoInvalid() {
        CreateUserDto createUserdto = getCreateUserDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.role", "message"));
        doReturn(validationResult).when(createUserValidator).validate(createUserdto);

        assertThrows(ValidationException.class, () -> userService.create(createUserdto));
        verifyNoInteractions(createUserMapper, userMapper, userDao);
    }

    private User getUser() {
        return User.builder()
                .id(99)
                .name("Ivan")
                .email("test@gmail.com")
                .birthday(LocalDate.of(2020, 11, 4))
                .password("123")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(99)
                .name("Ivan")
                .email("test@gmail.com")
                .birthday(LocalDate.of(2020, 11, 4))
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
    }

    private CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .birthday("2020-11-04")
                .password("123")
                .gender(Gender.MALE.name())
                .role(Role.USER.name())
                .build();
    }
}