package pi.tracker.service;

import pi.tracker.dto.PiSensorHubMetric;
import pi.tracker.service.exceptions.DeliveryException;

import java.util.Map;

public interface MetricDeliverer {
    void deliver(Map.Entry<String, Integer> sensorData) throws DeliveryException;
    void deliver(PiSensorHubMetric metric) throws DeliveryException;
}
