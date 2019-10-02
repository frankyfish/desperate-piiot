package pi.tracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import pi.tracker.config.MqttDeliveryProperties;
import pi.tracker.mqtt.PiTrackerMqttClient;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.util.Map;

@Slf4j
@Singleton
public class MqttDeliveryService implements MetricDeliverer {

    private PiTrackerMqttClient client;
    private MqttDeliveryProperties deliveryProperties;

    @Inject
    public MqttDeliveryService(PiTrackerMqttClient mqttClient, MqttDeliveryProperties mqttDeliveryProperties) {
        this.client = mqttClient;
        this.deliveryProperties = mqttDeliveryProperties;
    }

    public void deliver(Map<String, Integer> sensorData) throws DeliveryException {
        log.debug("Starting to deliver sensor data");
        for (Map.Entry<String, Integer> sd : sensorData.entrySet()) {
            publishToBroker(sd.getKey(), sd.getValue());
        }
    }

    @Override
    public void deliver(Map.Entry<String, Integer> sensorData) throws DeliveryException {
        log.debug("Starting to deliver sensor data");
        publishToBroker(sensorData.getKey(), sensorData.getValue());
    }

    private void publishToBroker(String topic, Integer metricData) throws DeliveryException {
        String prefixedTopic = formatEnding(deliveryProperties.getTopicPrefix()) + topic;
        if (!client.isConnected()) {
            log.warn("Looks like mqtt connection was lost, performing reconnection.");
            try {
                client.connect();
            } catch (MqttException me) {
                log.error("Failed to reconnect to MQTT Broker!");
                throw new DeliveryException(me); // stop execution in case MQTT connection is lost
            }
        }
        log.trace("Preparing message for broker topic={}, metric value {}", prefixedTopic, metricData);
        MqttMessage mqttMessage = new MqttMessage(metricData.toString().getBytes());
        mqttMessage.setQos(deliveryProperties.getMqttQos());
        try {
            client.publish(prefixedTopic, mqttMessage);
        } catch (MqttException me) {
            log.error("Failed to publish message to topic {}, error: ", prefixedTopic, me);
        }
    }

    private String formatEnding(String userDefinedPrefix) {
        if (!userDefinedPrefix.isBlank()) {
            if (!userDefinedPrefix.endsWith("/")) {
                return userDefinedPrefix + "/";
            }
        }
        return userDefinedPrefix;
    }

}
