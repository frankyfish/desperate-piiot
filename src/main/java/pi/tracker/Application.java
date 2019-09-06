package pi.tracker;

import io.micronaut.runtime.Micronaut;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.service.SensorHubService;

@Slf4j
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class).getBean(SensorHubService.class).serve();
    }
}