package pi.tracker.dockerpi;

import pi.tracker.dto.DockerPiSensorHubMetric;
import pi.tracker.service.SensorDataCollector;
import java.util.Collections;
import java.util.Map;

/**
 * Instance of {@link SensorDataCollector} used in tests.
 */
public class FakeSensorDataCollector implements SensorDataCollector {
    @Override
    public DockerPiSensorHubMetric collectMetric() {
        return DockerPiSensorHubMetric.builder()
                .airPressureSensor(2.0)
                .externalTemperatureSensor(36.6)
                .human(0)
                .light(33)
                .deviceLocation("Earth")
                .build();
    }

    @Override
    public Map<String, Integer> collectRawData() {
        return Collections.singletonMap(DockerPiConstants.SensorNames.HUMAN_SENSOR, 1);
    }
}
