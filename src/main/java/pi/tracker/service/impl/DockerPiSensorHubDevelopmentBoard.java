package pi.tracker.service.impl;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dockerpi.I2CConnector;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.SensorHub;
import pi.tracker.dockerpi.exceptions.SensorException;
import pi.tracker.dto.DockerPiSensorHubMetric;
import pi.tracker.service.SensorDataCollector;

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

/**
 * Used to collect Sensor Data from DockerPi Sensor Hub Development Board SKU: EP-0106
 * Visit following link for board information.
 * @see <a href="https://wiki.52pi.com/index.php/DockerPi_Sensor_Hub_Development_Board_SKU:_EP-0106?spm=a2g0o.detail.1000023.1.3cbc6268w9u6GE">
 *     EP-0106
 *     </a>
 * Was tested with ver.1 of the board.
 */
@Slf4j
@Singleton
public class DockerPiSensorHubDevelopmentBoard implements SensorDataCollector {

    private final List<Sensor> availableSensors;
    private final I2CDevice device;
    private final Console console;

    @Inject
    public DockerPiSensorHubDevelopmentBoard(I2CConnector connector, SensorHub sensorHub) throws IOException {
        this.availableSensors = sensorHub.getAvailableSensors();
        I2CBus i2c = connector.getBus();
        this.device = i2c.getDevice(DOCKERPI_SENSORHUB_BOARD);
        this.console = new Console();
    }

    @Override
    public DockerPiSensorHubMetric collectMetric() {
        log.debug("Starting to collect data for metric from sensors...");
        DockerPiSensorHubMetric metric = DockerPiSensorHubMetric.builder().build();
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
        return metric;
    }

    @Override
    public Map<String, Integer> collectRawData() {
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
        return metrics;
    }
}
