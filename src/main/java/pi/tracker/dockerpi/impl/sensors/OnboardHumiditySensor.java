package pi.tracker.dockerpi.impl.sensors;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;
import pi.tracker.dockerpi.DockerPiConstants;
import pi.tracker.dockerpi.Sensor;
import pi.tracker.dockerpi.exceptions.SensorException;

import java.io.IOException;

import static pi.tracker.dockerpi.DockerPiConstants.ON_BOARD_HUMIDITY_REG;
import static pi.tracker.dockerpi.DockerPiConstants.ON_BOARD_SENSOR_ERROR;
import static pi.tracker.dockerpi.DockerPiConstants.SensorNames.*;

public class OnboardHumiditySensor implements Sensor {
    @Override
    public Integer read(I2CDevice device, Console console) throws IOException {
        if (device.read(ON_BOARD_SENSOR_ERROR) != 1) {
            int onBoardHumidity = device.read(ON_BOARD_HUMIDITY_REG);
            console.println("the humidity of sensor is :" + onBoardHumidity + "%");
            return onBoardHumidity;
        } else {
            console.println("Onboard temperature and humidity sensor data may not be up to date!");
            throw new SensorException("Onboard temperature and humidity sensor data may not be up to date!",
                    getSensorName());
        }
    }

    @Override
    public String getSensorName() {
        return ONBOARD_HUMIDITY;
    }
}
