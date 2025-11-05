package com.fflog.staticmaker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raid_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaidGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Group name is required")
    @Size(max = 50, message = "Group name must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Group name must contain only letters and numbers")
    @Column(name = "static_name", nullable = false, unique = true, length = 50)
    private String groupName;

    @Column(name = "players", nullable = false)
    private Integer playerCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @OneToMany(mappedBy = "raidGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> members = new ArrayList<>();

    public void addMember(Player player) {
        members.add(player);
        player.setRaidGroup(this);
        this.playerCount = members.size();
    }

    public void removeMember(Player player) {
        members.remove(player);
        player.setRaidGroup(null);
        this.playerCount = members.size();
    }
}
