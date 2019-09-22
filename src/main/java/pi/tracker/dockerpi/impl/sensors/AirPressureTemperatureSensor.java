package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.exceptions.SensorException;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.*;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.AIR_PRESSURE_TEMPERATURE;

public class AirPressureTemperatureSensor implements Sensor {
    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        if (device.read(BMP280_STATUS) == 0) {
            Integer airPresTemp = device.read(BMP280_TEMP_REG);
            console.println("the temp of air pressure sensor is :" + airPresTemp + "centigrade");
            return airPresTemp;
        } else {
            console.println("Onboard barometer works abnormally!");
            throw new SensorException("Onboard barometer works abnormally!", getSensorName());
        }
    }

    @Override
    public String getSensorName() {
        return AIR_PRESSURE_TEMPERATURE;
    }
}
