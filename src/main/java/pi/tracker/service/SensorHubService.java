package pi.tracker.service;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dockerpi.I2CConnector;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.SensorHub;
import pi.tracker.dockerpi.exceptions.SensorException;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pi.tracker.dockerpi.DockerPiConstants.DOCKERPI_SENSORHUB_BOARD;

@Slf4j
@Singleton
public class SensorHubService {

    private List<Sensor> availableSensors;
    private final I2CDevice device;
    private final Console console;

    private final MetricDeliverer deliverer;

    @Inject
    public SensorHubService(MetricDeliverer metricDeliverer, I2CConnector connector, SensorHub sensorHub)
            throws IOException {
        I2CBus i2c = connector.getBus();
        this.deliverer = metricDeliverer;
        this.availableSensors = sensorHub.getAvailableSensors();
        this.device = i2c.getDevice(DOCKERPI_SENSORHUB_BOARD);
        this.console = new Console();
    }

    public void serve() {
        log.debug("Starting to serve sensors...");
        Map<String, Integer> metrics = new HashMap<>(availableSensors.size());
        for (Sensor sensor : availableSensors) {
            try {
                metrics.put(sensor.getSensorName(), sensor.read(this.device, this.console));
            } catch (SensorException se) {
                log.error("Got error from {} sensor, error:", se.getSensorName(), se);
            } catch (IOException e) {
                log.error("Failed to read data from sensor: {}", sensor.getSensorName());
            }
        }

        try {
            publish(metrics);
        } catch (DeliveryException e) {
            log.error("Data delivery failed, skipping...");
        }
    }

    private void publish(Map<String, Integer> sensorData) throws DeliveryException {
        log.debug("Publishing sensor");
        for (Map.Entry<String, Integer> sensorMetric : sensorData.entrySet()) {
            deliverer.deliver(sensorMetric);
        }
    }

}
