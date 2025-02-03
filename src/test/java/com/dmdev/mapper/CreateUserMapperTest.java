package com.dmdev.mapper;

import com.dmdev.dto.CreateUserDto;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateUserMapperTest {

    private final CreateUserMapper mapper = CreateUserMapper.getInstance();

    @Test
    void map() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .birthday("2020-11-04")
                .password("123")
                .gender(Gender.MALE.name())
                .role(Role.USER.name())
                .build();

        User actualResult = mapper.map(dto);

        User expectedResult = User.builder()
                .name("Ivan")
                .email("test@gmail.com")
                .birthday(LocalDate.of(2020, 11, 4))
                .password("123")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}