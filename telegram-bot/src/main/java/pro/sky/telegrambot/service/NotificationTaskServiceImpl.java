package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.NotificationTask;
import pro.sky.telegrambot.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;
    private final Logger logger = (Logger) LoggerFactory.getLogger(NotificationTaskServiceImpl.class);
    private static final Pattern MESSAGE_PATTERN =
            Pattern.compile("([0-9\\.:\\s]){16}(\\s)(.+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public NotificationTaskServiceImpl(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    @Override
    public void process(Update update) {
        // если отправлено пустое сообщение.
        if (update.message() == null) {
            logger.info("Отправлено пустое сообщение.");
            return;
        }
        long chatId = update.message().chat().id();
        String clientMessage = update.message().text();

        if (clientMessage == null) {
            telegramBot.execute(new SendMessage(chatId,
                    "Для начала работы с ботом отправь /start"));
            return;
        }
        // если отправлено стартовое сообщение
        if (clientMessage.equals("/start")) {
            sendWelcomeMessage(chatId);
            return;
        }
        //если отправлено сообщение, сравниваем с шаблоном и проверяем дату
        Matcher matcher = MESSAGE_PATTERN.matcher(clientMessage);
        if (matcher.find()) {
            dateFormatValidator(chatId, matcher);
        } else {
            telegramBot.execute(new SendMessage(chatId,
                    "Напоминание можно добавить в формате 'dd.MM.yyyy HH:mm' и текст."));
            return;
        }

        LocalDateTime alarmDate = LocalDateTime
                .parse(matcher.group(1), DATE_TIME_FORMATTER);
        String notification = matcher.group(3);
//        LocalDateTime now = LocalDateTime.now()
//                .truncatedTo(ChronoUnit.MINUTES);
        saveNotification(chatId, notification, alarmDate);

    }

    // Отправка приветственного сообщения с шаблоном напоминания.
    private void sendWelcomeMessage(long chatId) {
        telegramBot.execute(new SendMessage(chatId,
                "Добро пожаловать в бот-напоминалку!"
                        + "Добавь напоминание в формате 'dd.MM.yyyy HH:mm текст напоминания.'"
                        + "И в указанное время я пришлю тебе уведомление."));
    }

    // Проверка корректности даты
    private void dateFormatValidator(long chatId,
                                     Matcher matcher) {
        String dateString = matcher.group(1);
        try {
            LocalDateTime alarmDate = LocalDateTime.parse(dateString,
                    DATE_TIME_FORMATTER);

            if (!alarmDate.isAfter(LocalDateTime.now())) {
                logger.warning("Некорректный формат даты.");
                telegramBot.execute(new SendMessage(chatId,
                        "Напоминание должно быть позднее текущего момента."));
                return;
            }
        } catch (DateTimeParseException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Напоминание должно быть в формате 'dd.MM.yyyy HH:mm' и текст."));
        }
    }

    // Сохранение напоминания.
    private void saveNotification(Long chatId,
                                  String notification,
                                  LocalDateTime alarmDate) {
        NotificationTask notificationTask = new NotificationTask(chatId, notification, alarmDate);
        repository.save(notificationTask);
        logger.info("Напоминание сохранено: " + notificationTask);
        telegramBot.execute(new SendMessage(chatId,
                "Твоё напоминание сохранено: " + notificationTask));

    }

    @Override
    @Scheduled(cron = "0 0/1 * * * *")
    public void fetchDatabaseRecords() {
        List<NotificationTask> records = repository
                .findByAlarmDate(LocalDateTime.now().
                truncatedTo(ChronoUnit.MINUTES));
        // Для каждого напоминания выводим сообщение пользователю
        records.forEach(record -> {
            logger.info("Напоминание отправлено.");
            telegramBot.execute(new SendMessage(record.getChatID(),
                    String.format("Привет! Не забудь: \n%s" +
                                    ", в %s", record.getNotification(),
                            record.getAlarmDate())));
        });
    }
}
