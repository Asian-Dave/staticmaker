package com.fflog.staticmaker.service;

import com.fflog.staticmaker.exception.DuplicateResourceException;
import com.fflog.staticmaker.exception.ResourceNotFoundException;
import com.fflog.staticmaker.exception.ValidationException;
import com.fflog.staticmaker.model.Player;
import com.fflog.staticmaker.model.RaidGroup;
import com.fflog.staticmaker.model.RegionStats;
import com.fflog.staticmaker.model.RegionType;
import com.fflog.staticmaker.repository.RaidGroupRepository;
import com.fflog.staticmaker.repository.RegionStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RaidGroupService {

    private final RaidGroupRepository raidGroupRepository;
    private final RegionStatsRepository regionStatsRepository;

    @Transactional
    public RaidGroup createRaidGroup(String groupName, RegionType region) {
        // Validate group name
        validateGroupName(groupName);

        // Check if group already exists
        if (raidGroupRepository.existsByGroupName(groupName)) {
            throw new DuplicateResourceException("Raid group '" + groupName + "' already exists");
        }

        // Create raid group
        RaidGroup raidGroup = new RaidGroup();
        raidGroup.setGroupName(groupName);
        raidGroup.setRegion(region);
        raidGroup.setPlayerCount(0);

        RaidGroup savedGroup = raidGroupRepository.save(raidGroup);

        // Update region stats
        updateRegionStats(region, 1, 0);

        log.info("Created raid group: {} in region: {}", groupName, region);

        return savedGroup;
    }

    @Transactional(readOnly = true)
    public List<RaidGroup> getAllRaidGroups() {
        return raidGroupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RaidGroup getRaidGroupByName(String groupName) {
        return raidGroupRepository.findByGroupNameWithMembers(groupName)
                .orElseThrow(() -> new ResourceNotFoundException("Raid group '" + groupName + "' not found"));
    }

    @Transactional(readOnly = true)
    public List<RaidGroup> getRaidGroupsByRegion(RegionType region) {
        return raidGroupRepository.findByRegion(region);
    }

    @Transactional
    public void deleteRaidGroup(String groupName) {
        RaidGroup raidGroup = raidGroupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new ResourceNotFoundException("Raid group '" + groupName + "' not found"));

        RegionType region = raidGroup.getRegion();
        int playerCount = raidGroup.getPlayerCount();

        raidGroupRepository.delete(raidGroup);

        // Update region stats
        updateRegionStats(region, -1, -playerCount);

        log.info("Deleted raid group: {} from region: {}", groupName, region);
    }

    @Transactional(readOnly = true)
    public String exportRaidGroup(String groupName) {
        RaidGroup raidGroup = getRaidGroupByName(groupName);

        StringBuilder export = new StringBuilder();
        export.append("Raid Group: ").append(raidGroup.getGroupName()).append("\n");
        export.append("Region: ").append(raidGroup.getRegion().getDisplayName()).append("\n");
        export.append("Player Count: ").append(raidGroup.getPlayerCount()).append("\n\n");
        export.append("Members:\n");
        export.append("----------------------------------------\n");

        for (Player player : raidGroup.getMembers()) {
            export.append(String.format("%s %s | Role: %s | Server: %s (%s)\n",
                    player.getFirstname(),
                    player.getSurname(),
                    player.getRole(),
                    player.getServer(),
                    player.getDatacenter()));
        }

        return export.toString();
    }

    private void validateGroupName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Group name cannot be empty");
        }
        if (name.length() > 50) {
            throw new ValidationException("Group name must not exceed 50 characters");
        }
        if (name.contains(" ")) {
            throw new ValidationException("Group name cannot contain spaces");
        }
        if (!name.matches("^[a-zA-Z0-9]+$")) {
            throw new ValidationException("Group name must contain only letters and numbers");
        }
    }

    private void updateRegionStats(RegionType region, int staticsDelta, int playersDelta) {
        RegionStats stats = regionStatsRepository.findByRegion(region)
                .orElseGet(() -> {
                    RegionStats newStats = new RegionStats(region);
                    return regionStatsRepository.save(newStats);
                });

        if (staticsDelta > 0) {
            stats.setStaticsCount(stats.getStaticsCount() + staticsDelta);
        } else if (staticsDelta < 0) {
            stats.setStaticsCount(Math.max(0, stats.getStaticsCount() + staticsDelta));
        }

        if (playersDelta > 0) {
            stats.setPlayersCount(stats.getPlayersCount() + playersDelta);
        } else if (playersDelta < 0) {
            stats.setPlayersCount(Math.max(0, stats.getPlayersCount() + playersDelta));
        }

        regionStatsRepository.save(stats);
    }
}
