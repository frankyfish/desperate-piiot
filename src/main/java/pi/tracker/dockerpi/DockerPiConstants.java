package pi.tracker.dockerpi;

import java.util.*;

public class DockerPiConstants {

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

    // Sensors names also used as a topics for messages to be pushed to
    public static class SensorNames {
        public static final String TEMPERATURE_SENSOR = "temperature";
        public static final String LIGHT_SENSOR = "light";
        public static final String HUMAN_SENSOR = "human";
        public static final String AIR_PRESSURE_TEMPERATURE = "air-pressure-temperature";
        public static final String AIR_PRESSURE = "air-pressure";
        public static final String ONBOARD_HUMIDITY = "on-board-humidity";
        public static final String ONBOARD_TEMPERATURE = "on-board-temperature";
    }

    static List<Byte> getSensorsIds() {
        return Arrays.asList(
                (byte) 0x01,
                (byte) 0x02,
                (byte) 0x03,
                (byte) 0x04,
                (byte) 0x05,
                (byte) 0x06,
                (byte) 0x07,
                (byte) 0x08,
                (byte) 0x09,
                (byte) 0x0A,
                (byte) 0X0B,
                (byte) 0x0C,
                (byte) 0x0D
        );
    }

}
