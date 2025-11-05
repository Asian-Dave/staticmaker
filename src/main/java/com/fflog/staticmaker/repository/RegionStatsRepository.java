package com.fflog.staticmaker.repository;

import com.fflog.staticmaker.model.RegionStats;
import com.fflog.staticmaker.model.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionStatsRepository extends JpaRepository<RegionStats, Long> {

    Optional<RegionStats> findByRegion(RegionType region);

    boolean existsByRegion(RegionType region);
}
