package pi.tracker.dockerpi;

import lombok.Getter;
import pi.tracker.dockerpi.impl.sensors.*;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Defines which sensors of DockerPi Sensor Hub Development Board to collect data from.
 */
@Getter
@Singleton
public class SensorHub {
    /**
     * Data from sensors from this list will be collected.
     */
    private final List<Sensor> availableSensors;

    /**
     * Default constructor that contains all sensors available on the board.
     * @see <a href="https://wiki.52pi.com/index.php/DockerPi_Sensor_Hub_Development_Board_SKU:_EP-0106?spm=a2g0o.detail.1000023.1.3cbc6268w9u6GE">
     * EP-0106
     *  </a>
     */
    public SensorHub(){
        availableSensors = Arrays.asList(new TemperatureSensor(),
                new LightSensor(),
                new HumanSensor(),
                new AirPressureTemperatureSensor(),
                new AirPressureSensor(),
                new OnboardHumiditySensor(),
                new OnboardTemperatureSensor());
    }

}
