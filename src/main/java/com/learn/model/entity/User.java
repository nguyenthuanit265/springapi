package com.learn.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity(name = "User")
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String name;
    /**
     * Authorization information.
     */
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(name = "UserRole",
            joinColumns = @JoinColumn(name = "userId",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId",
                    referencedColumnName = "id"))
    private Set<Role> roles;
}