package pi.tracker.dockerpi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;

import java.io.IOException;

@Deprecated
public class SensorHubOrig {

    //SensorHub Address
    public static final int DOCKERPI_SENSORHUB_BOARD = 0x17;

    //SensorHub's functions
    public static final byte STATUS_REG = (byte) 0x04;

    //air temperature
    public static final byte TEMP_REG = (byte) 0x01;

    //light sensor
    public static final byte LIGHT_REG_L = (byte) 0x02;
    public static final byte LIGHT_REG_H = (byte) 0x03;

    //onboard sensors
    public static final byte ON_BOARD_TEMP_REG = (byte) 0x05;
    public static final byte ON_BOARD_HUMIDITY_REG = (byte) 0x06;
    public static final byte ON_BOARD_SENSOR_ERROR = (byte) 0x07;

    // air pressure sensor
    public static final byte BMP280_TEMP_REG = (byte) 0x08;
    public static final byte BMP280_STATUS = (byte) 0x0C;
    public static final byte BMP280_PRESSURE_REG_L = (byte) 0x09;
    public static final byte BMP280_PRESSURE_REG_M = (byte) 0x0A;
    public static final byte BMP280_PRESSURE_REG_H = (byte) 0X0B;
    // human
    public static final byte HUMAN_DETECT = (byte) 0x0D;

    /*
    todo: mb use some sort of job(scheduled) here?
     */

    public void process() throws IOException, I2CFactory.UnsupportedBusNumberException {
        final Console console = new Console();

        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        I2CDevice device = i2c.getDevice(DOCKERPI_SENSORHUB_BOARD);
        int i = 0;

        if(device.read(DOCKERPI_SENSORHUB_BOARD) < 0)
            console.println("External board Can not be initialized, please enable I2C and try again!",device.read(DOCKERPI_SENSORHUB_BOARD));
        else
            console.println("External board has been initialized");

        if((device.read(STATUS_REG) & 0x01) == 0x01)
            console.println("off-chip temperature sensor overrange!");
        else if((device.read(STATUS_REG) & 0x02) == 0x02)
            console.println("No external temperature sensor!");
        else {
            console.println("the temp of air is :" + device.read(TEMP_REG) + "centigrade");
        }

        if((device.read(STATUS_REG) & 0x04) == 0x04) {
            console.println("Onboard brightness sensor overrange!");
        }
        else if((device.read(STATUS_REG) & 0x08) == 0x08) {
            console.println("Onboard brightness sensor failure!");
        }
        else {
            console.println("Current onboard sensor brightness is :" + (device.read(LIGHT_REG_L) | device.read(LIGHT_REG_H) << 8) + "lux");
        }

        if(device.read(ON_BOARD_SENSOR_ERROR) == 1)
            console.println("Onboard temperature and humidity sensor data may not be up to date!");

        console.println("the temp of sensor on board is :" + device.read(ON_BOARD_TEMP_REG) + "centigrade");
        console.println("the humidity of sensor is :" + device.read(ON_BOARD_HUMIDITY_REG) + "%");

        if(device.read(BMP280_STATUS) == 0) {
            console.println("the temp of air pressure sensor is :" + device.read(BMP280_TEMP_REG) + "centigrade");
            console.println("the air pressure is :" + (device.read(BMP280_PRESSURE_REG_L)
                    | device.read(BMP280_PRESSURE_REG_M) << 8
                    | device.read(BMP280_PRESSURE_REG_H) << 16) + "pa");
        }
        else
            console.println("Onboard barometer works abnormally!");

        if(device.read(HUMAN_DETECT) == 1) {
            console.println("Live body detected within 5 seconds!");
        }
        else {
            console.println("No humans detecte!");
        }
    }

}
