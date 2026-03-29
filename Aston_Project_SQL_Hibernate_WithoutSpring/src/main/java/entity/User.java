package entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public User( String name,  String email,  Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        createdAt = LocalDateTime.now();
    }
}