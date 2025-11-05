package com.fflog.staticmaker.controller;

import com.fflog.staticmaker.dto.CreatePlayerRequest;
import com.fflog.staticmaker.model.Player;
import com.fflog.staticmaker.model.RegionType;
import com.fflog.staticmaker.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player player = playerService.createPlayer(
                request.getFirstname(),
                request.getSurname(),
                request.getRole(),
                request.getDatacenter(),
                request.getServer(),
                request.getStaticName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/static/{staticName}")
    public ResponseEntity<List<Player>> getPlayersByStatic(@PathVariable String staticName) {
        List<Player> players = playerService.getPlayersByStaticName(staticName);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<Player>> getPlayersByRegion(@PathVariable RegionType region) {
        List<Player> players = playerService.getPlayersByRegion(region);
        return ResponseEntity.ok(players);
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long playerId) {
        playerService.deletePlayer(playerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{playerId}/role")
    public ResponseEntity<Player> updatePlayerRole(
            @PathVariable Long playerId,
            @RequestParam String role) {
        Player player = playerService.updatePlayerRole(playerId, role);
        return ResponseEntity.ok(player);
    }
}
