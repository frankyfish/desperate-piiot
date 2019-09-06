package pi.tracker.mqtt;

import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class PahoPiTrackerMqttClient implements PiTrackerMqttClient {

    @Value("${pi-tracker.mqtt.host}") //this works only after constructor has been executed
    private String brokerUrl;

    @Value("${pi-tracker.mqtt.port}") // todo: do really need this?
    private int brokerPort;

    @Value("${pi-tracker.mqtt.client-id}")
    private String clientId;

    private MqttClient mqttClient;

    @PostConstruct
    public void initialize() throws MqttException {
        String brokerAddress = new StringBuilder(brokerUrl)
                .append(":")
                .append(brokerPort)
                .toString();
        this.mqttClient = new MqttClient(brokerAddress, "my-unique-client-id");
        log.info("Trying to connect to MQTT broker @ {}", brokerAddress);
        mqttClient.connect();
        if (mqttClient.isConnected()) {
            log.info("Connection to broker established with clientId: {}", clientId);
        }
    }

    @PreDestroy
    public void closeConnection() throws MqttException {
        try {
            log.trace("Closing MQTT connection...");
            mqttClient.close();
        } catch (MqttException e) {
            log.error("Failed to gracefully close MQTT connection, using force...");
            mqttClient.close(true);
        }
    }

    @Override
    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    @Override
    public void connect() throws MqttException {
        this.mqttClient.connect();
    }

    public void publish(String topic, MqttMessage message) throws MqttException {
        log.trace("Publishing message: {} to topic {}", message, topic);
        mqttClient.publish(topic, message);
    }
}
