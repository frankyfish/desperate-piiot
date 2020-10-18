package pi.tracker.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.service.SensorDataCollector;
import pi.tracker.telegram.keyboard.KeyboardButtons;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class TelegramDeliveryService {

    private final SensorDataCollector sensorDataCollector;
    private final ReplyKeyboardMarkup sensorKeyboard;
    private TelegramBot bot;
    //todo: telegram token field

    @Inject
    public TelegramDeliveryService(SensorDataCollector sensorDataCollector) {
        this.sensorDataCollector = sensorDataCollector;
        this.sensorKeyboard = new ReplyKeyboardMarkup(
                new String[]{KeyboardButtons.LIGHT, KeyboardButtons.TEMPERATURE},
                new String[]{KeyboardButtons.HUMIDITY, KeyboardButtons.AIR_PRESSURE},
                new String[]{KeyboardButtons.HUMAN});
        this.bot = new TelegramBot("");
    }

    /**
     * Starts a telegram bot used as a point of access to sensor data
     * Example: t.me/handy36bot  @handy36bot
     * @param serviceStartedEvent event that triggers the listening of requests from Telegram.
     */
    @EventListener
    public void startTelegramBot(final ServiceStartedEvent serviceStartedEvent) {
        log.info("Starting Telegram bot");
        bot.setUpdatesListener(updateListener -> {
            log.debug("Git Update event, starting to process...");
            updateListener.forEach(update -> {
                Long chatId = update.message().chat().id();
                String messageText = update.message().text();
                log.debug("Handling message: {}", update.message());
                if(messageText!=null && !messageText.isEmpty()) {
                    handleUserRequest(chatId, messageText);
                }
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUserRequest(Long chatId, String text) {
        switch (text) {
            case "/start":
                bot.execute(buildResponseWithSensorKeys(chatId, "Please select a sensor to check."));
                break;
            case KeyboardButtons.LIGHT:
                bot.execute(buildResponseWithSensorKeys(chatId,
                        "Light: " + sensorDataCollector.collectMetric().getLight() + " lux"));
                break;
            case KeyboardButtons.TEMPERATURE: // todo change to external temperature sensor!!! show board temperature now
                bot.execute(buildResponseWithSensorKeys(chatId,
                        "Temperature near device is: " + sensorDataCollector.collectMetric().getAirPressureTemperature()
                                + " °C"));
                break;
            case KeyboardButtons.HUMIDITY:
                bot.execute(buildResponseWithSensorKeys(chatId,
                        "Humidity is: " + sensorDataCollector.collectMetric().getOnBoardHumidity() + "%"));
                break;
            case KeyboardButtons.HUMAN:
                int human = sensorDataCollector.collectMetric().getHuman();
                bot.execute(buildResponseWithSensorKeys(chatId,
                        "There " + (human == 1 ? "are" : "are NO") + " moving live forms near sensor!"));
                break;
            case KeyboardButtons.AIR_PRESSURE:
                bot.execute(buildResponseWithSensorKeys(chatId,
                        "Air pressure is: " + sensorDataCollector.collectMetric().getAirPressureSensor()));
                break;
            default:
                SendMessage sendMessage = new SendMessage(chatId, "Be specific, please.");
                sendMessage.replyMarkup(sensorKeyboard);
                bot.execute(sendMessage);
        }
    }

    private SendMessage buildResponseWithSensorKeys(Long chatId, String body) {
        SendMessage sendMessage = new SendMessage(chatId, body);
        sendMessage.replyMarkup(sensorKeyboard);
        return sendMessage;
    }

}
