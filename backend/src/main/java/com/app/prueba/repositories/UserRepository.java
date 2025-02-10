package com.app.prueba.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.prueba.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    public User findUserByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
    public User findUserByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE username = :username OR email = :email", nativeQuery = true)
    public User findUserByUsernameOrEmail(String username, String email);

    @Query(value = "SELECT * FROM users WHERE email = :email AND password = :password", nativeQuery = true)
    public User findUserByEmailAndPassword(String email, String password);
}
