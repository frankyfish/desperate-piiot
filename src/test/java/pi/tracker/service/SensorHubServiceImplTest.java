package pi.tracker.service;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pi.tracker.dockerpi.FakeI2CConnector;
import pi.tracker.dockerpi.FakeI2CDevice;
import pi.tracker.dockerpi.I2CConnector;
import pi.tracker.dockerpi.SensorHub;
import pi.tracker.service.exceptions.DeliveryException;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class SensorHubServiceImplTest {

    private SensorHubService sensorHubService;
    private I2CDevice i2CDevice = new FakeI2CDevice();
    private MetricDeliverer metricDeliverer = mock(MetricDeliverer.class);
    private SensorHub sensorHub = new SensorHub();

    @BeforeEach
    public void setUp() throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CConnector connector = new FakeI2CConnector(i2CDevice);
        this.sensorHubService = new SensorHubServiceImpl(metricDeliverer, connector, sensorHub);
    }

    @Test
    public void serveIsCalledThenMessageIsPublished() throws IOException, DeliveryException {
        // given: service is running
        // when serve is called
        sensorHubService.serve();
        // then metrics from each sensor of the hub are delivered to MQTT broker
        sensorHub.getAvailableSensors().forEach(sensorName -> {
            try {
                Map.Entry<String, Integer> sensorData = new AbstractMap.SimpleEntry<>(sensorName.getSensorName(), 0);
                verify(metricDeliverer, times(1)).deliver(sensorData);
            } catch (DeliveryException e) {
                e.printStackTrace();
            }
        });
    }

}
