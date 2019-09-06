package pi.tracker.dockerpi;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.util.Console;

import java.io.IOException;

public interface Sensor {

    Integer read(final I2CDevice device, final Console console) throws IOException;
    default String getSensorName() {
        return "sensor";
    }

}
