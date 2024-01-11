package pro.sky.telegrambot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, UUID> {
    List<NotificationTask> findByAlarmDate (LocalDateTime alarmDate);

}
