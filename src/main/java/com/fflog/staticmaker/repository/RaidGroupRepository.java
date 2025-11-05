package com.fflog.staticmaker.repository;

import com.fflog.staticmaker.model.RaidGroup;
import com.fflog.staticmaker.model.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaidGroupRepository extends JpaRepository<RaidGroup, Long> {

    Optional<RaidGroup> findByGroupName(String groupName);

    List<RaidGroup> findByRegion(RegionType region);

    boolean existsByGroupName(String groupName);

    @Query("SELECT rg FROM RaidGroup rg LEFT JOIN FETCH rg.members WHERE rg.groupName = :groupName")
    Optional<RaidGroup> findByGroupNameWithMembers(String groupName);

    long countByRegion(RegionType region);
}
