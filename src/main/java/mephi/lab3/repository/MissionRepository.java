package mephi.lab3.repository;//норм

import java.util.Optional;
import mephi.lab3.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<MissionEntity, Long>{
    Optional<MissionEntity> findByMissionId(String missionId);
}
