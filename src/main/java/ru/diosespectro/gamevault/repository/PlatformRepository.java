package ru.diosespectro.gamevault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.diosespectro.gamevault.entity.Platform;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    Platform findByName(String name);
}