package com.fflog.staticmaker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 10)
    private RegionType region;

    @Column(name = "statics", nullable = false)
    private Integer staticsCount = 0;

    @Column(name = "players", nullable = false)
    private Integer playersCount = 0;

    public RegionStats(RegionType region) {
        this.region = region;
        this.staticsCount = 0;
        this.playersCount = 0;
    }

    public void incrementStatics() {
        this.staticsCount++;
    }

    public void decrementStatics() {
        if (this.staticsCount > 0) {
            this.staticsCount--;
        }
    }

    public void incrementPlayers() {
        this.playersCount++;
    }

    public void decrementPlayers() {
        if (this.playersCount > 0) {
            this.playersCount--;
        }
    }
}
