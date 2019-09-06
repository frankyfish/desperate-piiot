package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dockerpi.Sensor;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.STATUS_REG;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.TEMPERATURE_SENSOR;
import static pi.tracker.dockerpi.DockerPiConstants.TEMP_REG;

@Slf4j
public class TemperatureSensor implements Sensor {

    @Override
    public String getSensorName() {
        return TEMPERATURE_SENSOR;
    }

    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        log.trace("Trying to get data from {} sensor", getSensorName());
        Integer metric = null;
        if ((device.read(STATUS_REG) & 0x01) == 0x01) {
            console.println("off-chip temperature sensor overrange!");
        } else if ((device.read(STATUS_REG) & 0x02) == 0x02) {
            console.println("No external temperature sensor!");
        } else {
            metric = device.read(TEMP_REG);
            console.println("the temp of air is :" + metric + "centigrade");
            log.trace("Successfully retrieved sensor data!");
        }
        return metric;
    }
}
