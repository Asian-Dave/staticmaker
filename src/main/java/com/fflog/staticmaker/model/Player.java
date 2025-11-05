package com.fflog.staticmaker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players",
       uniqueConstraints = @UniqueConstraint(columnNames = {"firstname", "surname", "static_name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 15, message = "First name must not exceed 15 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters")
    @Column(nullable = false, length = 15)
    private String firstname;

    @NotBlank(message = "Surname is required")
    @Size(max = 15, message = "Surname must not exceed 15 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Surname must contain only letters")
    @Column(nullable = false, length = 15)
    private String surname;

    @NotBlank(message = "Role is required")
    @Column(name = "f_role", nullable = false, length = 10)
    private String role; // Tank, Healer, DPS

    @NotBlank(message = "Datacenter is required")
    @Column(name = "f_datacenter", nullable = false, length = 20)
    private String datacenter;

    @NotBlank(message = "Server is required")
    @Column(name = "f_server", nullable = false, length = 30)
    private String server;

    @NotBlank(message = "Static name is required")
    @Column(name = "static_name", nullable = false, length = 50)
    private String staticName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raid_group_id")
    private RaidGroup raidGroup;

    public String getFullName() {
        return firstname + " " + surname;
    }
}
