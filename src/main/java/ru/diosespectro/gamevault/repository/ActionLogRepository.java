package ru.diosespectro.gamevault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.diosespectro.gamevault.entity.ActionLog;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
}