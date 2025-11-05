package com.fflog.staticmaker.controller;

import com.fflog.staticmaker.dto.CreateRaidGroupRequest;
import com.fflog.staticmaker.model.RaidGroup;
import com.fflog.staticmaker.model.RegionType;
import com.fflog.staticmaker.service.RaidGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raid-groups")
@RequiredArgsConstructor
public class RaidGroupController {

    private final RaidGroupService raidGroupService;

    @PostMapping
    public ResponseEntity<RaidGroup> createRaidGroup(@Valid @RequestBody CreateRaidGroupRequest request) {
        RaidGroup raidGroup = raidGroupService.createRaidGroup(request.getGroupName(), request.getRegion());
        return ResponseEntity.status(HttpStatus.CREATED).body(raidGroup);
    }

    @GetMapping
    public ResponseEntity<List<RaidGroup>> getAllRaidGroups() {
        List<RaidGroup> raidGroups = raidGroupService.getAllRaidGroups();
        return ResponseEntity.ok(raidGroups);
    }

    @GetMapping("/{groupName}")
    public ResponseEntity<RaidGroup> getRaidGroupByName(@PathVariable String groupName) {
        RaidGroup raidGroup = raidGroupService.getRaidGroupByName(groupName);
        return ResponseEntity.ok(raidGroup);
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<RaidGroup>> getRaidGroupsByRegion(@PathVariable RegionType region) {
        List<RaidGroup> raidGroups = raidGroupService.getRaidGroupsByRegion(region);
        return ResponseEntity.ok(raidGroups);
    }

    @DeleteMapping("/{groupName}")
    public ResponseEntity<Void> deleteRaidGroup(@PathVariable String groupName) {
        raidGroupService.deleteRaidGroup(groupName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{groupName}/export", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportRaidGroup(@PathVariable String groupName) {
        String export = raidGroupService.exportRaidGroup(groupName);
        return ResponseEntity.ok(export);
    }
}
