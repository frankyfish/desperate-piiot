package pi.tracker.config;

import io.micronaut.context.annotation.Value;
import lombok.Getter;

import javax.inject.Singleton;

@Singleton
@Getter
public class MqttDeliveryProperties {

    @Value("${pi-tracker.mqtt.qos}")
    private int mqttQos;

    @Value("${pi-tracker.mqtt.topic-prefix}")
    private String topicPrefix;

}
