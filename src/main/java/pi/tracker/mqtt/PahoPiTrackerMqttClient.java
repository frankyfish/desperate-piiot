package pi.tracker.mqtt;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
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
// this allows to create this bean during Micronaut context creation, what makes this bean to intialize earlier
// than the others
@Context //FIXME: TBD-0 not sure if this needed if no logic to automatically fallback to local metrics saving is implemented
@Requires(property = "pi-tracker.mqtt.enable", value = "true")
public class PahoPiTrackerMqttClient implements PiTrackerMqttClient {

    //these injections work only after constructor has been executed
    @Value("${pi-tracker.mqtt.host}")
    private String brokerUrl;

    @Value("${pi-tracker.mqtt.port}")
    private int brokerPort;

    @Value("${pi-tracker.mqtt.client-id}")
    private String clientId;

    private MqttClient mqttClient;

    @PostConstruct
    public void initialize() throws MqttException {
        String brokerAddress = brokerUrl + ":" + brokerPort;
        this.mqttClient = new MqttClient(brokerAddress, clientId);
        log.info("Trying to connect to MQTT broker @ {}", brokerAddress);
        mqttClient.connect();
        if (mqttClient.isConnected()) {
            log.debug("Connection to broker established with clientId: {}", clientId);
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
