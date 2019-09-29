package pi.tracker.service.impl;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import pi.tracker.config.MqttDeliveryProperties;
import pi.tracker.mqtt.PahoPiTrackerMqttClient;
import pi.tracker.mqtt.PiTrackerMqttClient;
import pi.tracker.service.exceptions.DeliveryException;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MqttDeliveryServiceTest {

    PiTrackerMqttClient mqttClient = mock(PahoPiTrackerMqttClient.class);
    MqttDeliveryProperties mqttDeliveryProperties = mock(MqttDeliveryProperties.class);
    MqttDeliveryService deliveryService = new MqttDeliveryService(mqttClient, mqttDeliveryProperties);
    String sensorName = "temp";
    Integer sensorData = 12;

    @Test
    public void dataIsDeliveredAndConnectionIsAlive() throws DeliveryException, MqttException {
        //GIVEN: app is running
        //AND: connection to MQTT broker was alive
        when(mqttClient.isConnected()).thenReturn(true);
        when(mqttDeliveryProperties.getTopicPrefix()).thenReturn("");
        when(mqttDeliveryProperties.getMqttQos()).thenReturn(0);
        //AND: metric is received
        Map<String, Integer> metric = Collections.singletonMap(sensorName, sensorData);
        //WHEN: MqttDeliveryService is called
        deliveryService.deliver(metric);
        //THEN: metrics are delivered
        verify(mqttClient, times(1)).publish(eq(sensorName), any(MqttMessage.class));
    }

    @Test
    public void dataIsDeliveredAndConnectionIsReestablished() throws DeliveryException, MqttException {
        //GIVEN: app is running
        //AND: connection to MQTT was closed
        when(mqttClient.isConnected()).thenReturn(false);
        when(mqttDeliveryProperties.getTopicPrefix()).thenReturn("");
        when(mqttDeliveryProperties.getMqttQos()).thenReturn(0);
        //AND: metric is received
        Map<String, Integer> metric = Collections.singletonMap(sensorName, sensorData);
        //WHEN: MqttDeliveryService is called
        deliveryService.deliver(metric);
        //THEN: connection is reestablished successfully
        verify(mqttClient, times(1)).connect();
        //AND: metric is delivered to MQTT broker
        verify(mqttClient, times(1)).publish(eq(sensorName), any(MqttMessage.class));
    }

}
