package pi.tracker.runners;

import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.service.SensorHubService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;

@Slf4j
@Singleton
@Requires(property = "pi-tracker.scheduled-mode.enabled", value = "true")
public class ScheduledRunner {
    private SensorHubService sensorHubService;

    @Inject
    public ScheduledRunner(SensorHubService sensorHubService) {
        this.sensorHubService = sensorHubService;
    }

    @Scheduled(cron = "${pi-tracker.scheduled-mode.cron}")
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Running according to schedule. Current time = {}", ZonedDateTime.now());
        }
        sensorHubService.serve();
    }

}
