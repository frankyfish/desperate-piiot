package pi.tracker.service.impl;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dockerpi.I2CConnector;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.SensorHub;
import pi.tracker.dockerpi.exceptions.SensorException;
import pi.tracker.dto.PiSensorHubMetric;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.SensorHubService;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pi.tracker.dockerpi.DockerPiConstants.DOCKERPI_SENSORHUB_BOARD;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.*;

@Slf4j
@Singleton
public class SensorHubServiceImpl implements SensorHubService {
    @Value("${pi-tracker.use-raw-output}")
    private Boolean isRaw;

    private List<Sensor> availableSensors;
    private final I2CDevice device;
    private final Console console;

    private final MetricDeliverer deliverer;

    @Inject
    public SensorHubServiceImpl(MetricDeliverer metricDeliverer, I2CConnector connector, SensorHub sensorHub)
            throws IOException {
        I2CBus i2c = connector.getBus();
        this.deliverer = metricDeliverer;
        this.availableSensors = sensorHub.getAvailableSensors();
        this.device = i2c.getDevice(DOCKERPI_SENSORHUB_BOARD);
        this.console = new Console();
    }

    public void serve() {
        log.debug("Starting to serve sensors.");
        if (isRaw) {
            serveAsRaw();
        } else {
            serveAsMetric();
        }
    }

    private void serveAsMetric() {
        log.debug("Starting to collect data for metric from sensors...");

        PiSensorHubMetric metric = PiSensorHubMetric.builder().build();
        metric.setTimeStamp(Timestamp.from(ZonedDateTime.now().toInstant()).toString());
        for (Sensor sensor : availableSensors) {
            String currentSensorName = sensor.getSensorName();
            try {
                Integer currentSensorData = sensor.read(this.device, this.console);
                switch(currentSensorName) {
                    case TEMPERATURE_SENSOR:
                        metric.setExternalTemperatureSensor(currentSensorData);
                        break;
                    case LIGHT_SENSOR:
                        metric.setLight(currentSensorData);
                        break;
                    case HUMAN_SENSOR:
                        metric.setHuman(currentSensorData);
                        break;
                    case AIR_PRESSURE_TEMPERATURE:
                        metric.setAirPressureTemperature(currentSensorData);
                        break;
                    case AIR_PRESSURE:
                        metric.setAirPressureSensor(currentSensorData);
                        break;
                    case ONBOARD_HUMIDITY:
                        metric.setOnBoardHumidity(currentSensorData);
                        break;
                    case ONBOARD_TEMPERATURE:
                        metric.setOnBoardTemperature(currentSensorData);
                        break;
                }
            } catch (SensorException se) {
                log.error("Got error from {} sensor, error:", se.getSensorName(), se);
            } catch (IOException e) {
                log.error("Failed to read data from sensor: {}", sensor.getSensorName());
            }
        }
        log.debug("Successfully collected data from sensor, sending...");
        try {
            publishMetric(metric);
        } catch (DeliveryException de) {
            log.error("Metric publishing failed, reason: ", de);
        }
    }

    private void serveAsRaw() {
        log.debug("Starting to collect raw data from sensors...");
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
            log.error("Data delivery failed, skipping... Reason: ", e);
        }
    }

    private void publish(Map<String, Integer> sensorData) throws DeliveryException {
        log.debug("Publishing sensor");
        for (Map.Entry<String, Integer> sensorMetric : sensorData.entrySet()) {
            deliverer.deliver(sensorMetric);
        }
    }

    private void publishMetric(PiSensorHubMetric metric) throws DeliveryException {
        // todo add check on where to publish
        log.debug("Publishing metric {}", metric);
        deliverer.deliver(metric);
    }

}
