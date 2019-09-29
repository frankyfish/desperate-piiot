package pi.tracker;

import io.micronaut.runtime.Micronaut;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.service.SensorHubServiceImpl;

@Slf4j
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
        /*SensorHubServiceImpl sensorHubService = Micronaut.run(Application.class).getBean(SensorHubServiceImpl.class);
        while (true) {
            sensorHubService.serve();
        }*/
    }
}