package pi.tracker.service;

import pi.tracker.service.exceptions.DeliveryException;

import java.util.Map;

public interface MetricDeliverer {
    void deliver(Map<String, Integer> sensorData) throws DeliveryException;
}
