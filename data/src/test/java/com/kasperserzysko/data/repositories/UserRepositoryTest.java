package com.kasperserzysko.data.repositories;

import com.kasperserzysko.data.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindUserByEmail() {
        User user = new User();
        user.setEmail("test@test.com");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findUserByEmail("test@test.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@test.com", foundUser.get().getEmail());
    }

    @Test
    void testFindUserByEmailReturnsEmptyOptionalWhenNotFound() {
        Optional<User> foundUser = userRepository.findUserByEmail("nonexistent@test.com");

        assertFalse(foundUser.isPresent());
    }
}