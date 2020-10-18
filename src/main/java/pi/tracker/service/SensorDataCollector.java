package pi.tracker.service;

import pi.tracker.dto.DockerPiSensorHubMetric;

import java.util.Map;

/**
 * Represents a service which implementations can collect data from specific board types.
 */
public interface SensorDataCollector {
    /**
     * Collects data from specific board and build a metric object, containing data from all available sensors.
     * Notice that some fields might be null if corresponding sensors are not asked for the data,
     * see {@link pi.tracker.dockerpi.SensorHub}
     * @return metric object with fields containing data from sensors.
     */
    DockerPiSensorHubMetric collectMetric(); //todo replace return type with proper type hierarchy (no only one board metric)

    /**
     * Collects data from sensors and returns a map with key as sensor name and value it's current value.
     * Notice that some sensors might be missing here because their list is taken from {@link pi.tracker.dockerpi.SensorHub}
     * @return map containing sensor names and their current values.
     */
    Map<String, Integer> collectRawData();
}
