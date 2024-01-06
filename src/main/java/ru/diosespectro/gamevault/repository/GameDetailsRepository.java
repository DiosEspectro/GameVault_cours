package ru.diosespectro.gamevault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.diosespectro.gamevault.entity.GameDetails;
import ru.diosespectro.gamevault.entity.Platform;
import ru.diosespectro.gamevault.entity.Store;

import java.util.List;

@Repository
public interface GameDetailsRepository extends JpaRepository<GameDetails, Long> {
    List<GameDetails> findAllByPlatform(Platform platform);
    List<GameDetails> findAllByStore(Store store);
}
