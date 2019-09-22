package pi.tracker.dockerpi;

import lombok.Getter;
import pi.tracker.dockerpi.impl.sensors.*;

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
                new HumanSensor(),
                new AirPressureTemperatureSensor(),
                new AirPressureSensor(),
                new OnboardHumiditySensor(),
                new OnboardTemperatureSensor());
    }

}
