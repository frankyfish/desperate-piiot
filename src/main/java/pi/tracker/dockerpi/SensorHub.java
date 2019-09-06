package pi.tracker.dockerpi;

import lombok.Getter;
import pi.tracker.dockerpi.impl.sensors.HumanSensor;
import pi.tracker.dockerpi.impl.sensors.LightSensor;
import pi.tracker.dockerpi.impl.sensors.TemperatureSensor;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Getter
@Singleton
public class SensorHub {

    private final List<Sensor> availableSensors;

    public SensorHub(){
        availableSensors = Arrays.asList(new TemperatureSensor(),
                new LightSensor(),
                new HumanSensor()); //todo: add other sensors
    }

}
