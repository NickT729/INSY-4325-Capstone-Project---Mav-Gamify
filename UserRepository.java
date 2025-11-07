package com.mavpal.repository;

import com.mavpal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUtaId(String utaId);
    Optional<User> findByEmail(String email);
    boolean existsByUtaId(String utaId);
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) + 1 FROM User u WHERE u.xp > (SELECT u2.xp FROM User u2 WHERE u2.id = ?1)")
    Long getUserRank(Integer userId);
}
