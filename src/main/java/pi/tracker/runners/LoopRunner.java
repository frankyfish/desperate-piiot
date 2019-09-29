package pi.tracker.runners;

import io.micronaut.context.annotation.Requires;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import pi.tracker.service.SensorHubService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Requires(missingBeans = ScheduledRunner.class)
public class LoopRunner {

    private SensorHubService sensorHubService;

    @Inject
    public LoopRunner(SensorHubService sensorHubService) {
        this.sensorHubService = sensorHubService;
    }

    @EventListener
    public void run(final ServiceStartedEvent serviceStartedEvent) {
        while (true) {
            sensorHubService.serve();
        }
    }
}
