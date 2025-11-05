package com.fflog.staticmaker.controller;

import com.fflog.staticmaker.model.RegionStats;
import com.fflog.staticmaker.model.RegionType;
import com.fflog.staticmaker.service.RegionStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/region-stats")
@RequiredArgsConstructor
public class RegionStatsController {

    private final RegionStatsService regionStatsService;

    @GetMapping
    public ResponseEntity<List<RegionStats>> getAllRegionStats() {
        List<RegionStats> stats = regionStatsService.getAllRegionStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{region}")
    public ResponseEntity<RegionStats> getStatsByRegion(@PathVariable RegionType region) {
        RegionStats stats = regionStatsService.getStatsByRegion(region);
        return ResponseEntity.ok(stats);
    }
}
