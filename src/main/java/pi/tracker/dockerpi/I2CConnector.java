package pi.tracker.dockerpi;

import com.pi4j.io.i2c.I2CBus;

public interface I2CConnector {

    I2CBus getBus();

}
