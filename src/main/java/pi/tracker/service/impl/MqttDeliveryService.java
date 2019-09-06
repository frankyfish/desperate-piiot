package pi.tracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import pi.tracker.mqtt.PiTrackerMqttClient;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Slf4j
@Singleton
public class MqttDeliveryService implements MetricDeliverer {

    private PiTrackerMqttClient client;

    @Inject
    public MqttDeliveryService(PiTrackerMqttClient mqttClient) {
        this.client = mqttClient;
    }

    public void deliver(Map<String, Integer> sensorData) throws DeliveryException {
        log.debug("Starting to deliver data");
        for (Map.Entry<String, Integer> sd : sensorData.entrySet()) {
            publishToBroker(sd.getKey(), sd.getValue());
        }
    }

    private void publishToBroker(String topic, Integer metricData) throws DeliveryException {
        if (!client.isConnected()) {
            log.warn("Looks like mqtt connection was lost, performing reconnection.");
            try {
                client.connect();
            } catch (MqttException me) {
                log.error("Failed to reconnect to MQTT Broker!");
                throw new DeliveryException(me); // stop execution in case MQTT connection is lost
            }
        }
        log.trace("Preparing message for broker topic={}, metric value {}", topic, metricData);
        MqttMessage mqttMessage = new MqttMessage(new byte[]{metricData.byteValue()});
        try {
            client.publish(topic, mqttMessage);
        } catch (MqttException me) {
            log.error("Failed to publish message to topic {}", topic);
        }
    }

}
