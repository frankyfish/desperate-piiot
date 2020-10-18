package pi.tracker.service.impl;

import io.micronaut.context.annotation.Requires;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import pi.tracker.config.MqttDeliveryProperties;
import pi.tracker.dto.DockerPiSensorHubMetric;
import pi.tracker.mqtt.PahoPiTrackerMqttClient;
import pi.tracker.mqtt.PiTrackerMqttClient;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Inject;
import javax.inject.Named;

import javax.inject.Singleton;
import java.util.Map;

/**
 * This bean is only initialized in case there is an instance of {@link PahoPiTrackerMqttClient};
 * otherwise sensor data would be written into local file, see {@link ToFileDeliveryService}
 */
@Slf4j
@Singleton
@Named("mqtt")
@Requires(beans = PahoPiTrackerMqttClient.class)
public class MqttDeliveryService implements MetricDeliverer {

    private final PiTrackerMqttClient client;
    private final MqttDeliveryProperties deliveryProperties;

    @Inject
    public MqttDeliveryService(PiTrackerMqttClient mqttClient, MqttDeliveryProperties mqttDeliveryProperties) {
        this.client = mqttClient;
        this.deliveryProperties = mqttDeliveryProperties;
    }

    @Deprecated
    public void deliver(Map<String, Integer> sensorData) throws DeliveryException {
        log.debug("Starting to deliver sensor data");
        for (Map.Entry<String, Integer> sd : sensorData.entrySet()) {
            MqttMessage mqttMessage = new MqttMessage(sd.getValue().toString().getBytes());
            mqttMessage.setQos(deliveryProperties.getMqttQos());
            String prefixedTopic = formatEnding(deliveryProperties.getTopicPrefix()) + sd.getKey(); // todo:move to single place of usage
            prepareAndPublish(prefixedTopic, mqttMessage);
        }
    }

    @Override
    public void deliver(Map.Entry<String, Integer> sensorData) throws DeliveryException {
        log.debug("Starting to deliver sensor data");

        String prefixedTopic = formatEnding(deliveryProperties.getTopicPrefix()) + sensorData.getKey();
        log.trace("Preparing message for broker topic={}, metric value {}", prefixedTopic, sensorData.getValue());
        MqttMessage mqttMessage = new MqttMessage(sensorData.getValue().toString().getBytes());
        mqttMessage.setQos(deliveryProperties.getMqttQos());

        prepareAndPublish(prefixedTopic, mqttMessage);
    }

    @Override
    public void deliver(DockerPiSensorHubMetric metric) throws DeliveryException {
        log.debug("Starting to deliver metric");

        MqttMessage mqttMessage = new MqttMessage(SerializationUtils.serialize(metric));
        mqttMessage.setQos(deliveryProperties.getMqttQos());
        String prefixedTopic = formatEnding(deliveryProperties.getTopicPrefix()) + deliveryProperties.getTopicForMetric();

        prepareAndPublish(prefixedTopic, mqttMessage);
    }

    private void prepareAndPublish(String topic, MqttMessage mqttMessage) throws DeliveryException {
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
