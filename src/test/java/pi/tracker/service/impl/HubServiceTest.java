package pi.tracker.service.impl;

import io.micronaut.context.annotation.Primary;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pi.tracker.dockerpi.FakeI2CConnector;
import pi.tracker.dockerpi.FakeI2CDevice;
import pi.tracker.dockerpi.I2CConnector;
import pi.tracker.dockerpi.SensorHub;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.SensorHubService;

import javax.inject.Inject;


@MicronautTest
public class HubServiceTest {

    @Inject
    SensorHubService sensorHubService;

    @Primary
    @MockBean(MqttDeliveryService.class)
    MetricDeliverer metricDeliverer() {
        return Mockito.mock(MetricDeliverer.class);
    }

//    @Primary
//    @MockBean(I2CConnector.class)
//    I2CConnector i2CConnector() {
//        return new FakeI2CConnector(new FakeI2CDevice());
//    }

    @MockBean(SensorHub.class)
    SensorHub sensorHub() {
        return Mockito.mock(SensorHub.class);
    }

}
