package pi.tracker.service.impl;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import pi.tracker.dto.DockerPiSensorHubMetric;
import pi.tracker.service.exceptions.DeliveryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static pi.tracker.service.impl.ToFileDeliveryService.FILE_FULL_PATH;

public class ToFileDeliveryServiceTest {

    @Before
    public void setUp() throws IOException {
        cleanTestDir();
    }

    private void cleanTestDir() throws IOException {
        if (Files.deleteIfExists(Paths.get(FILE_FULL_PATH))) {
            System.out.println("Found files from previous test runs, deleted them");
        }
    }

    @Test
    public void testWritingToFile() throws DeliveryException, IOException {
        // GIVEN metric from board has arrived
        DockerPiSensorHubMetric testMetric = DockerPiSensorHubMetric.builder()
                .airPressureSensor(2.0)
                .airPressureTemperature(12.0)
                .human(1)
                .light(42.0)
                .onBoardHumidity(23.0)
                .onBoardTemperature(32.0)
                .externalTemperatureSensor(12.0)
                .build();

        // WHEN calling service to write to file
        ToFileDeliveryService toFileDeliveryService = new ToFileDeliveryService();
        toFileDeliveryService.deliver(testMetric);
        // THEN expecting file to exist
        boolean fileExists = Files.exists(Path.of(FILE_FULL_PATH));
        assertThat(fileExists).isTrue();
        List<DockerPiSensorHubMetric> metricsFromFile = readFromMetricsFile();
        // AND expecting file to contain exactly one line
        assertThat(metricsFromFile.size()).isEqualTo(1);
        // AND expecting metric to be the same
        assertThat(metricsFromFile.get(0)).isEqualTo(testMetric);
    }

    @Test
    public void testWritingMultipleMetricsToFile() throws DeliveryException, IOException {
        // GIVEN metrics from board has arrived
        DockerPiSensorHubMetric testMetric = DockerPiSensorHubMetric.builder()
                .airPressureSensor(2.0)
                .airPressureTemperature(12.0)
                .human(1)
                .light(42.0)
                .onBoardHumidity(23.0)
                .onBoardTemperature(32.0)
                .externalTemperatureSensor(12.0)
                .build();

        DockerPiSensorHubMetric metric1 = DockerPiSensorHubMetric.builder()
                .airPressureSensor(2.0)
                .airPressureTemperature(12.0)
                .human(1)
                .light(42.0)
                .onBoardHumidity(23.0)
                .onBoardTemperature(32.0)
                .externalTemperatureSensor(12.0)
                .build();

        DockerPiSensorHubMetric metric2 = DockerPiSensorHubMetric.builder()
                .airPressureSensor(2.0)
                .airPressureTemperature(12.0)
                .human(1)
                .light(42.0)
                .onBoardHumidity(22.0)
                .onBoardTemperature(22.0)
                .externalTemperatureSensor(22.0)
                .build();

        List<DockerPiSensorHubMetric> testMetrics = Arrays.asList(testMetric, metric1, metric2);

        // WHEN calling service each to write to file
        ToFileDeliveryService toFileDeliveryService = new ToFileDeliveryService();
        testMetrics.forEach(metric -> {
            try {
                toFileDeliveryService.deliver(metric);
            } catch (DeliveryException e) {
                fail("Failed to call write to file");
            }
        });
        // THEN expecting file to exist
        boolean fileExists = Files.exists(Path.of(FILE_FULL_PATH));
        assertThat(fileExists).isTrue();
        List<DockerPiSensorHubMetric> metricsFromFile = readFromMetricsFile();
        // AND expecting file to contain orginal number of metrics
        assertThat(metricsFromFile.size()).isEqualTo(testMetrics.size());
        // AND expecting metric to be the same
        metricsFromFile.forEach(metricFromFile -> {
            assertThat(testMetrics).contains(metricFromFile);
        });
    }

    private List<DockerPiSensorHubMetric> readFromMetricsFile() throws IOException {
        List<DockerPiSensorHubMetric> metricsFromFile = new ArrayList<>();
        // reading file
        try (Stream<String> linesStream = Files.lines(Paths.get(FILE_FULL_PATH))) {
            linesStream.forEach(line -> {
                metricsFromFile.add(new Gson().fromJson(line, DockerPiSensorHubMetric.class));
            });
        }
        return metricsFromFile;
    }

}
