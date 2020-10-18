package pi.tracker.service.impl;

import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dto.DockerPiSensorHubMetric;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.SensorDataCollector;
import pi.tracker.service.SensorHubService;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Service that triggers sensor data collection specific to <a href="https://wiki.52pi.com/index.php/DockerPi_Sensor_Hub_Development_Board_SKU:_EP-0106?spm=a2g0o.detail.1000023.1.3cbc6268w9u6GE">EP-0106 </a>
 * Also triggers delivery using whichever {@link MetricDeliverer} implementation was selected via settings.
 * @see MqttDeliveryService
 * @see ToFileDeliveryService
 */
@Slf4j
@Singleton
public class SensorHubServiceImpl implements SensorHubService {
    //todo: property class
    @Value("${pi-tracker.use-raw-output}")
    private Boolean isRaw;

    private final SensorDataCollector sensorDataCollector;
    private final MetricDeliverer deliverer;

    @Inject
    public SensorHubServiceImpl(SensorDataCollector sensorDataCollector, MetricDeliverer deliverer) {
        this.sensorDataCollector = sensorDataCollector;
        this.deliverer = deliverer;
    }

    public void serve() {
        log.debug("Starting to serve sensors.");
        if (isRaw) {
            serveAsRaw();
        } else {
            serveAsMetric();
        }
    }

    private void serveAsMetric() {
        try {
            publishMetric(sensorDataCollector.collectMetric());
        } catch (DeliveryException de) {
            log.error("Metric publishing failed, reason: ", de);
        }
    }

    private void serveAsRaw() {
        try {
            deliverSensorsData(sensorDataCollector.collectRawData());
        } catch (DeliveryException e) {
            log.error("Data delivery failed, skipping... Reason: ", e);
        }
    }

    private void deliverSensorsData(Map<String, Integer> sensorData) throws DeliveryException {
        log.debug("Publishing sensor");
        for (Map.Entry<String, Integer> sensorMetric : sensorData.entrySet()) {
            deliverer.deliver(sensorMetric);
        }
    }

    private void publishMetric(DockerPiSensorHubMetric metric) throws DeliveryException {
        // todo add check on where to publish
        log.debug("Starting metric delivery {}", metric);
        deliverer.deliver(metric);
    }

}
