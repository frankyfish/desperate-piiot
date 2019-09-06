package pi.tracker.dockerpi.impl.connector;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import pi.tracker.dockerpi.I2CConnector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class I2CConnectorImpl implements I2CConnector {

    private I2CBus i2CBus;

    @Inject
    public I2CConnectorImpl() throws IOException, I2CFactory.UnsupportedBusNumberException {
        this.i2CBus = I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Override
    public I2CBus getBus() {
        return this.i2CBus;
    }
}
