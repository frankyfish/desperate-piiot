package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dockerpi.Sensor;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.*;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.LIGHT_SENSOR;

@Slf4j
public class LightSensor implements Sensor {

    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        Integer metric = null;
        if ((device.read(STATUS_REG) & 0x04) == 0x04) {
            console.println("Onboard brightness sensor overrange!");
        } else if ((device.read(STATUS_REG) & 0x08) == 0x08) {
            console.println("Onboard brightness sensor failure!");
        } else {
            metric = (device.read(LIGHT_REG_L) | device.read(LIGHT_REG_H) << 8);
            console.println("Current onboard sensor brightness is :" + metric + "lux");
            log.trace("Successfully retrieved sensor data!");
        }
        return metric;
    }

    @Override
    public String getSensorName() {
        return LIGHT_SENSOR;
    }

}
