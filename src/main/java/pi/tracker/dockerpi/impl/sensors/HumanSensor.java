package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dockerpi.Sensor;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.HUMAN_DETECT;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.HUMAN_SENSOR;

@Slf4j
public class HumanSensor implements Sensor {
    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        Integer metric = null;
        if(device.read(HUMAN_DETECT) == 1) {
            log.trace("Successfully retrieved sensor data!");
            metric = 1;
            console.println("Live body detected within 5 seconds!");
        } else {
            metric = 0;
            console.println("No humans detected!");
        }
        return metric;
    }

    @Override
    public String getSensorName() {
        return HUMAN_SENSOR;
    }
}
