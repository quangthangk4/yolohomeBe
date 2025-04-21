package com.DADN.homeyolo.service;

import com.DADN.homeyolo.dto.response.DataAdafruitResponse;
import com.DADN.homeyolo.dto.response.TempChartResponse;
import com.DADN.homeyolo.entity.ActivityHistory;
import com.DADN.homeyolo.exception.AppException;
import com.DADN.homeyolo.exception.ErrorCode;
import com.DADN.homeyolo.repository.ActivityHistoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AdafruitService {

    private final Map<String, String> messagesHistory;
    private final ActivityHistoryRepository activityHistoryRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.adafruit.username}")
    private String username;

    @Value("${app.adafruit.active_key}")
    private String active_key;

    private final WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final Duration blockTimeout = Duration.ofSeconds(10);

    public AdafruitService(WebClient.Builder webClientBuilder, SimpMessagingTemplate messagingTemplate, ActivityHistoryRepository activityHistoryRepository, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.messagingTemplate = messagingTemplate;

        messagesHistory = new HashMap<>();
        messagesHistory.put("fan", "Fan speed adjusted to ");
        messagesHistory.put("temp-danger", "dangerous temperature adjusted to ");
        messagesHistory.put("humidity-danger", "dangerous humidity adjusted to ");
        messagesHistory.put("minlight", "minLight adjusted to ");
        messagesHistory.put("light", "Light turned ");
        this.activityHistoryRepository = activityHistoryRepository;
        this.objectMapper = objectMapper;
    }

    public void sendControlCommand(String feedKey, String value, String username) {
        try {
            String message;
            switch (feedKey) {
                case "light":
                    if (!value.equals("0") && !value.equals("1")) {
                        throw new AppException(ErrorCode.INVALID_VALUE_LIGHT);
                    }
                    message = messagesHistory.get(feedKey) + (value.equals("1")?"on": "off");
                    break;
                case "minlight":
                case "fan":
                case "temp-danger":
                case "humidity-danger":
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

    public DataAdafruitResponse getLatestLightValue() {
        DataAdafruitResponse response = new DataAdafruitResponse();

        Map<String, String> listUrl = Map.of(
                "isOn", "light",
                "minLight", "minlight",
                "brightness", "light-sensor",
                "fan", "fan",
                "humidity-danger", "humidity-danger",
                "temp-danger", "temp-danger"
        );

        listUrl.forEach((sensorType, feedKey) -> {
            String url = String.format("https://io.adafruit.com/api/v2/%s/feeds/%s/data?limit=1", username, feedKey);
            webClient.get().uri(url)
                    .header("X-AIO-Key", active_key)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(data -> {
                        String value = extractValueFromJsonListMap(data);
                        if (value != null) {
                        switch (sensorType) {
                            case "isOn":
                                response.setIsOn("1".equals(value));
                                break;
                            case "minLight":
                                response.setMinLight(value);
                                break;
                            case "brightness":
                                response.setBrightness(value);
                                break;
                            case "fan":
                                response.setFanSpeed(value);
                                break;
                            case "temp-danger":
                                response.setTempDanger(value);
                                break;
                            case "humidity-danger":
                                response.setHumidityDanger(value);
                                break;
                        }

                        }
                    })
                    .doOnError(error -> {
                        throw new AppException(ErrorCode.ERROR_WHEN_CALL_ADAFRUIT_API);
                    })
                    .block(blockTimeout);
        });


        return response;
    }

    public String extractValueFromJsonListMap(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            // Parse chuỗi JSON thành một List các Map
            List<Map<String, Object>> dataList = objectMapper.readValue(jsonString, new TypeReference<>() {
            });

            // Kiểm tra xem list có rỗng không và phần tử đầu tiên có tồn tại không
            if (!dataList.isEmpty()) {
                Map<String, Object> firstObjectMap = dataList.getFirst();
                // Kiểm tra xem map có chứa key "value" không
                if (firstObjectMap != null && firstObjectMap.containsKey("value")) {
                    Object valueObject = firstObjectMap.get("value");
                    // Trả về giá trị dưới dạng String (nếu nó không null)
                    return (valueObject != null) ? valueObject.toString() : null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            return null; // Trả về null nếu có lỗi parse
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

            webClient.get().uri(url)
                    .header("X-AIO-Key", active_key)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(data -> {

                        try {
                            // Gửi lên WebSocket
                            messagingTemplate.convertAndSend("/topic/sensor-data",
                                    Map.of("sensor", sensorType, "value", data));
                        } catch (Exception ignored) {
                        }
                    })
                    .doOnError(error -> {
                        throw new AppException(ErrorCode.ERROR_WHEN_CALL_ADAFRUIT_API);
                    })
                    .subscribe();
        });
        log.info("✅ Hoàn tất fetch dữ liệu.");
    }

    public List<TempChartResponse> tempChart(String hours, String key_feed) {
        String url = String.format("https://io.adafruit.com/api/v2/%s/feeds/%s/data/chart?hours=%s", username,key_feed, hours);

        String jsonData = webClient.get()
                .uri(url)
                .header("X-AIO-Key", active_key)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (jsonData == null) {
            System.err.println("Không nhận được dữ liệu từ Adafruit API.");
            throw new AppException(ErrorCode.ERROR_WHEN_CALL_ADAFRUIT_API);
        }


        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            // Lấy ra node "data"
            JsonNode dataNode = rootNode.path("data"); // dùng path để tránh NullPointerException
            if (dataNode.isMissingNode() || !dataNode.isArray()) {
                System.err.println("Trường 'data' không tồn tại hoặc không phải là mảng trong JSON response.");
                throw new AppException(ErrorCode.ERROR_WHEN_CALL_ADAFRUIT_API);
            }

            List<TempChartResponse> results = new ArrayList<>();
            // Duyệt qua từng phần tử trong mảng "data"
            for (JsonNode pointNode : dataNode) {
                if (pointNode.isArray() && pointNode.size() >= 2) {
                    String dateStr = pointNode.get(0).asText(null); // Lấy phần tử đầu tiên
                    String valueStr = pointNode.get(1).asText(null); // Lấy phần tử thứ hai

                    if (dateStr != null && valueStr != null) {
                        results.add(new TempChartResponse(dateStr, valueStr));
                    } else {
                        System.err.println("Bỏ qua điểm dữ liệu không hợp lệ (null date hoặc value): " + pointNode.toString());
                    }
                } else {
                    System.err.println("Bỏ qua điểm dữ liệu không hợp lệ (không phải mảng hoặc size < 2): " + pointNode.toString());
                }
            }
            return results;

        } catch (IOException e) {
            System.err.println("Lỗi khi parse JSON từ Adafruit API: " + e.getMessage());
            throw new AppException(ErrorCode.ERROR_WHEN_CALL_ADAFRUIT_API);
        }
    }



}
