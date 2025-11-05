package com.fflog.staticmaker.repository;

import com.fflog.staticmaker.model.Player;
import com.fflog.staticmaker.model.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByStaticName(String staticName);

    List<Player> findByRegion(RegionType region);

    Optional<Player> findByFirstnameAndSurnameAndStaticName(String firstname, String surname, String staticName);

    boolean existsByFirstnameAndStaticName(String firstname, String staticName);

    boolean existsBySurnameAndStaticName(String surname, String staticName);

    long countByStaticName(String staticName);

    long countByRegion(RegionType region);
}
