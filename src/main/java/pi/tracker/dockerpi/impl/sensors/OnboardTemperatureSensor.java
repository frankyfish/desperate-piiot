package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.exceptions.SensorException;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.*;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.ONBOARD_TEMPERATURE;

public class OnboardTemperatureSensor implements Sensor {
    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        if (device.read(ON_BOARD_SENSOR_ERROR) != 1) {
            int onBoardTemperature = device.read(ON_BOARD_TEMP_REG);
            console.println("the temp of sensor on board is :" + onBoardTemperature + "centigrade");
            return onBoardTemperature;
        } else {
            console.println("Onboard temperature and humidity sensor data may not be up to date!");
            throw new SensorException("Onboard temperature and humidity sensor data may not be up to date!",
                    getSensorName());
        }
    }

    @Override
    public String getSensorName() {
        return ONBOARD_TEMPERATURE;
    }
}
