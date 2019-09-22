package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import pi.tracker.dockerpi.DockerPiConstants;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.exceptions.SensorException;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.*;

public class AirPressureSensor implements Sensor {
    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        if(device.read(BMP280_STATUS) == 0) {
            console.println("the air pressure is :" + (device.read(BMP280_PRESSURE_REG_L)
                    | device.read(BMP280_PRESSURE_REG_M) << 8
                    | device.read(BMP280_PRESSURE_REG_H) << 16) + "pa");
            return device.read(BMP280_PRESSURE_REG_L) | device.read(BMP280_PRESSURE_REG_M) << 8 | device.read(BMP280_PRESSURE_REG_H) << 16;
        } else {
            console.println("Onboard barometer works abnormally!");
            throw new SensorException("Onboard barometer works abnormally!", getSensorName());
        }
    }

    @Override
    public String getSensorName() {
        return DockerPiConstants.SensorNames.AIR_PRESSURE;
    }
}
