package com.DADN.homeyolo.service;

import com.DADN.homeyolo.entity.ActivityHistory;
import com.DADN.homeyolo.exception.AppException;
import com.DADN.homeyolo.exception.ErrorCode;
import com.DADN.homeyolo.repository.ActivityHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AdafruitService {

    private final Map<String, String> messagesHistory;
    private final ActivityHistoryRepository activityHistoryRepository;

    @Value("${app.adafruit.username}")
    private String username;

    @Value("${app.adafruit.active_key}")
    private String active_key;

    private final WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;

    public AdafruitService(WebClient.Builder webClientBuilder, SimpMessagingTemplate messagingTemplate, ActivityHistoryRepository activityHistoryRepository) {
        this.webClient = webClientBuilder.build();
        this.messagingTemplate = messagingTemplate;

        messagesHistory = new HashMap<>();
        messagesHistory.put("fan", "Fan speed adjusted to ");
        messagesHistory.put("minlight", "minLight adjusted to ");
        messagesHistory.put("light", "Light turned ");
        this.activityHistoryRepository = activityHistoryRepository;
    }

    public void sendControlCommand(String feedKey, String value, String username) {
        try {
            String message = "";
            switch (feedKey) {
                case "light":
                    if (!value.equals("0") && !value.equals("1")) {
                        throw new AppException(ErrorCode.INVALID_VALUE_LIGHT);
                    }
                    message = messagesHistory.get(feedKey) + (value.equals("1")?"on": "off");
                    break;
                case "minlight":
                case "fan":
                    int intValue = Integer.parseInt(value);
                    if (intValue < 0 || intValue > 100) {
                        throw new AppException(ErrorCode.INVALID_VALUE_FAN_OR_MINLIGHT);
                    }
                    message = messagesHistory.get(feedKey) + value + "%.";
                    break;
                default:
                    throw new AppException(ErrorCode.INVALID_KEY);
            }

            String url = String.format("https://io.adafruit.com/api/v2/%s/feeds/%s/data", this.username, feedKey);
            webClient.post()
                    .uri(url)
                    .header("X-AIO-Key", active_key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("value", value))
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe();

            // lưu vào lịch sử
            activityHistoryRepository.save(ActivityHistory.builder()
                            .message(message)
                            .time(LocalDateTime.now())
                            .username(username)
                    .build());

        }
        catch (AppException e) {
            throw new RuntimeException(e);
        }

    }

    @Scheduled(fixedRate = 10000) // 5 giây/lần
    public void fetchSensorData() {
        Map<String, String> feeds = Map.of(
                "humidity", "humidity",
                "light-sensor", "light-sensor",
                "temp", "temp"
        );

        log.info("🔁 Bắt đầu lấy dữ liệu từ Adafruit...");

        feeds.forEach((sensorType, feedKey) -> {
            String url = String.format("https://io.adafruit.com/api/v2/%s/feeds/%s/data/last", username, feedKey);
            log.info("📡 Đang fetch sensor [{}] từ URL: {}", sensorType, url);

            webClient.get().uri(url)
                    .header("X-AIO-Key", active_key)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(data -> {
                        log.info("✅ Nhận được dữ liệu cho sensor [{}]: {}", sensorType, data);

                        try {
                            // Gửi lên WebSocket
                            messagingTemplate.convertAndSend("/topic/sensor-data",
                                    Map.of("sensor", sensorType, "value", data));
                            log.info("📤 Đã gửi dữ liệu [{}] lên topic /topic/sensor-data", sensorType);
                        } catch (Exception e) {
                            log.error("❌ Gửi WebSocket lỗi với sensor [{}]: {}", sensorType, e.getMessage(), e);
                        }
                    })
                    .doOnError(error -> {
                        log.error("🚫 Lỗi khi fetch từ Adafruit [{}]: {}", sensorType, error.getMessage(), error);
                    })
                    .subscribe();
        });

        log.info("✅ Hoàn tất vòng fetch dữ liệu.");
    }



}
