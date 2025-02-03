package com.dmdev.service;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.dto.UserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateUserMapper;
import com.dmdev.mapper.UserMapper;
import com.dmdev.validator.CreateUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceIT extends IntegrationTestBase {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void init() {
        userDao = UserDao.getInstance();
        userService = new UserService(
                CreateUserValidator.getInstance(),
                userDao,
                CreateUserMapper.getInstance(),
                UserMapper.getInstance()
        );
    }

    @Test
    void login() {
        User user = userDao.save(getUser("test1@gmail.com"));

        Optional<UserDto> actualResult = userService.login(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void create() {
        CreateUserDto createUserDto = getCreateUserDto();

        UserDto actualResult = userService.create(createUserDto);

        assertNotNull(actualResult.getId());

    }

    private User getUser(String email) {
        return User.builder()
//                .id(99)       без id, так как оно устанавливается при сохранении
                .name("Ivan")
                .email(email)
                .birthday(LocalDate.of(2020, 11, 4))
                .password("123")
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