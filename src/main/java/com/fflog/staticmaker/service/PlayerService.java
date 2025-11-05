package com.fflog.staticmaker.service;

import com.fflog.staticmaker.exception.DuplicateResourceException;
import com.fflog.staticmaker.exception.ResourceNotFoundException;
import com.fflog.staticmaker.exception.ValidationException;
import com.fflog.staticmaker.model.Player;
import com.fflog.staticmaker.model.RaidGroup;
import com.fflog.staticmaker.model.RegionStats;
import com.fflog.staticmaker.model.RegionType;
import com.fflog.staticmaker.repository.PlayerRepository;
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
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final RaidGroupRepository raidGroupRepository;
    private final RegionStatsRepository regionStatsRepository;
    private final FFLogsApiClient ffLogsApiClient;

    @Transactional
    public Player createPlayer(String firstname, String surname, String role, String datacenter,
                               String server, String groupName) {
        // Validate input
        validatePlayerName(firstname, "First name");
        validatePlayerName(surname, "Surname");

        // Determine region from datacenter
        RegionType region = RegionType.fromDatacenter(datacenter);

        // Check if player already exists in the static
        if (playerRepository.existsByFirstnameAndStaticName(firstname, groupName)) {
            throw new DuplicateResourceException("Player with first name '" + firstname +
                    "' already exists in static '" + groupName + "'");
        }

        if (playerRepository.existsBySurnameAndStaticName(surname, groupName)) {
            throw new DuplicateResourceException("Player with surname '" + surname +
                    "' already exists in static '" + groupName + "'");
        }

        // Validate character exists on FFLogs using proper API
        if (ffLogsApiClient.characterDoesNotExist(firstname, surname, server, region)) {
            throw new ValidationException("Character '" + firstname + " " + surname +
                    "' does not exist on FFLogs");
        }

        // Get raid group
        RaidGroup raidGroup = raidGroupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new ResourceNotFoundException("Raid group '" + groupName + "' not found"));

        // Create player
        Player player = new Player();
        player.setFirstname(firstname);
        player.setSurname(surname);
        player.setRole(role);
        player.setDatacenter(datacenter);
        player.setServer(server);
        player.setStaticName(groupName);
        player.setRegion(region);
        player.setRaidGroup(raidGroup);

        Player savedPlayer = playerRepository.save(player);

        // Update raid group player count
        raidGroup.setPlayerCount(raidGroup.getPlayerCount() + 1);
        raidGroupRepository.save(raidGroup);

        // Update region stats
        updateRegionStats(region, 0, 1);

        log.info("Created player: {} {} in static: {}", firstname, surname, groupName);

        return savedPlayer;
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Player> getPlayersByStaticName(String staticName) {
        return playerRepository.findByStaticName(staticName);
    }

    @Transactional(readOnly = true)
    public List<Player> getPlayersByRegion(RegionType region) {
        return playerRepository.findByRegion(region);
    }

    @Transactional
    public void deletePlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));

        String staticName = player.getStaticName();
        RegionType region = player.getRegion();

        playerRepository.delete(player);

        // Update raid group player count
        raidGroupRepository.findByGroupName(staticName).ifPresent(raidGroup -> {
            raidGroup.setPlayerCount(Math.max(0, raidGroup.getPlayerCount() - 1));
            raidGroupRepository.save(raidGroup);
        });

        // Update region stats
        updateRegionStats(region, 0, -1);

        log.info("Deleted player: {} {} from static: {}", player.getFirstname(), player.getSurname(), staticName);
    }

    @Transactional
    public Player updatePlayerRole(Long playerId, String newRole) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));

        player.setRole(newRole);
        Player updatedPlayer = playerRepository.save(player);

        log.info("Updated player {} {} role to: {}", player.getFirstname(), player.getSurname(), newRole);

        return updatedPlayer;
    }

    private void validatePlayerName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
        if (name.length() > 15) {
            throw new ValidationException(fieldName + " must not exceed 15 characters");
        }
        if (name.contains(" ")) {
            throw new ValidationException(fieldName + " cannot contain spaces");
        }
        if (!name.matches("^[a-zA-Z]+$")) {
            throw new ValidationException(fieldName + " must contain only letters");
        }
    }


    private void updateRegionStats(RegionType region, int staticsDelta, int playersDelta) {
        RegionStats stats = regionStatsRepository.findByRegion(region)
                .orElseGet(() -> {
                    RegionStats newStats = new RegionStats(region);
                    return regionStatsRepository.save(newStats);
                });

        if (staticsDelta > 0) {
            stats.incrementStatics();
        } else if (staticsDelta < 0) {
            stats.decrementStatics();
        }

        if (playersDelta > 0) {
            stats.incrementPlayers();
        } else if (playersDelta < 0) {
            stats.decrementPlayers();
        }

        regionStatsRepository.save(stats);
    }
}
