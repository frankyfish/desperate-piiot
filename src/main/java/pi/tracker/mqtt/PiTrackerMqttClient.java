package pi.tracker.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface PiTrackerMqttClient {
    boolean isConnected();
    void publish(String topic, MqttMessage message) throws MqttException;
    void connect() throws MqttException;
}
