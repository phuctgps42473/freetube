package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 50)
    private String role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<Account> accounts;

    public static Role createUserRole() {
        Role role = new Role();
        role.setId(2);
        role.setRole("user");
        return role;
    }

    public Role() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
