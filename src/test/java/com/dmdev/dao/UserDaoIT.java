package com.dmdev.dao;

import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoIT extends IntegrationTestBase {

    private final UserDao userDao = UserDao.getInstance();

    @Test
    void findAll() {
        User user1 = userDao.save(getUser("test1@gmail.com"));
        User user2 = userDao.save(getUser("test2@gmail.com"));
        User user3 = userDao.save(getUser("test3@gmail.com"));

        List<User> actualResult = userDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> userIds = actualResult.stream().map(User::getId).toList();
        assertThat(userIds).contains(user1.getId(), user2.getId(), user3.getId());
    }

    @Test
    void findById() {
        User user = userDao.save(getUser("test1@gmail.com"));

        Optional<User> actualResult = userDao.findById(user.getId());

        assertThat(actualResult).isPresent().contains(user);
    }

    @Test
    void save() {
        User user = getUser("test1@gmail.com");

        User actualResult = userDao.save(user);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByEmailAndPassword() {
        User user = userDao.save(getUser("test1@gmail.com"));

        Optional<User> actualResult = userDao.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent().contains(user);
    }

    @Test
    void shouldNotFindByEmailAndPasswordIfUserDoesNotExists() {
        userDao.save(getUser("test1@gmail.com"));

        Optional<User> actualResult = userDao.findByEmailAndPassword("dummy", "123");

        assertThat(actualResult).isEmpty();
    }

    @Test
    void deleteExistingEntity() {
        User user = userDao.save(getUser("test1@gmail.com"));

        boolean actualResult = userDao.delete(user.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingEntity() {
        userDao.save(getUser("test1@gmail.com"));

        boolean actualResult = userDao.delete(10023);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        User user = getUser("test1@gmail.com");
        userDao.save(user);
        user.setName("Ivan-updated");
        user.setPassword("new-password");

        userDao.update(user);

        Optional<User> updatedUser = userDao.findById(user.getId());
        assertThat(updatedUser).isPresent().contains(user);
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
}