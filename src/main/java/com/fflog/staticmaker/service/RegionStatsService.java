package com.fflog.staticmaker.service;

import com.fflog.staticmaker.model.RegionStats;
import com.fflog.staticmaker.model.RegionType;
import com.fflog.staticmaker.repository.RegionStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionStatsService {

    private final RegionStatsRepository regionStatsRepository;

    @Transactional(readOnly = true)
    public List<RegionStats> getAllRegionStats() {
        List<RegionStats> stats = regionStatsRepository.findAll();

        // Ensure all regions have stats entries
        if (stats.size() < RegionType.values().length) {
            for (RegionType region : RegionType.values()) {
                if (stats.stream().noneMatch(s -> s.getRegion() == region)) {
                    RegionStats newStats = new RegionStats(region);
                    stats.add(regionStatsRepository.save(newStats));
                }
            }
        }

        return stats;
    }

    @Transactional(readOnly = true)
    public RegionStats getStatsByRegion(RegionType region) {
        return regionStatsRepository.findByRegion(region)
                .orElseGet(() -> {
                    RegionStats newStats = new RegionStats(region);
                    return regionStatsRepository.save(newStats);
                });
    }
}
