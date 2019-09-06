package pi.tracker.dockerpi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public class FakeI2CConnector implements I2CConnector {

    private final I2CDevice i2cDevice;

    public FakeI2CConnector(I2CDevice i2CDevice) {
        this.i2cDevice = i2CDevice;
    }

    @Override
    public I2CBus getBus() {
        return new I2CBus() {
            @Override
            public I2CDevice getDevice(int address) throws IOException {
                return i2cDevice;
            }

            @Override
            public int getBusNumber() {
                return 42;
            }

            @Override
            public void close() throws IOException {

            }
        };
    }
}
