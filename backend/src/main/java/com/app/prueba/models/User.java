package com.app.prueba.models;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    // Attributes id, name, lastName, email, password, phone

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    @Length(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Column(name = "last_name")
    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    @Length(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;

    @Column(name = "email", unique = true)
    @NotNull(message = "Email cannot be null")
    @Email(regexp = "^[a-zA-Z0-9._-]+@(gmail|hotmail|yahoo|outlook)\\.com$", message = "Email must be from gmail, hotmail, yahoo or outlook")
    private String email;

    @Column(name = "username", unique = true)
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(name = "password")
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Length(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Column(name = "phone", unique = true)
    @NotNull(message = "Phone cannot be null")
    private String phone;

    // 1 user 1 role
    @OneToOne // 1 user 1 role
    private Role role;

    @ManyToOne // 1 user many cards
    // optinal true 0..* (default)
    // optinal false 1..*
    private Cards cards;

}