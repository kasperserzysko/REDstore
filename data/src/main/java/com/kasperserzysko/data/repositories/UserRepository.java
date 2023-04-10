package com.kasperserzysko.data.repositories;

import com.kasperserzysko.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByActivationLink(String activationLink);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.ratings WHERE u.id = :userId")
    Optional<User> findUserWithRatings(@Param("userId") Long userId);
}
