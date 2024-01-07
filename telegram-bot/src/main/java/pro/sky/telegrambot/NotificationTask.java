package pro.sky.telegrambot;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "chat_id")
    private Long chatID;

    @Column (name = "notification")
    private String notification;

    @Column (name = "alarm_date")
    private LocalDateTime  alarmDate;

//    @Column (name = "added_at")
//    private LocalDateTime addedAt;


    public NotificationTask(Long chatID,
                            String notification,
                            LocalDateTime alarmDate) {
        this.chatID = chatID;
        this.notification = notification;
        this.alarmDate = alarmDate;
    }

    public UUID getId() {
        return id;
    }

    public Long getChatID() {
        return chatID;
    }

    public String getNotification() {
        return notification;
    }

    public LocalDateTime getAlarmDate() {
        return alarmDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(chatID, that.chatID) && Objects.equals(notification, that.notification) && Objects.equals(alarmDate, that.alarmDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatID, notification, alarmDate);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatID=" + chatID +
                ", notification='" + notification + '\'' +
                ", alarmDate=" + alarmDate +
                '}';
    }
}
