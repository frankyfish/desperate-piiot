package pi.tracker.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import pi.tracker.dto.PiSensorHubMetric;
import pi.tracker.service.MetricDeliverer;
import pi.tracker.service.exceptions.DeliveryException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.nio.file.*;
import java.util.Map;

/**
 * Used for storing of sensor data locally in the file.
 */
@Slf4j
@Singleton
@Named("file")
public class ToFileDeliveryService implements MetricDeliverer {

    public static final String FILE_NAME = "pi-metrics";
    public static final String FILE_PATH = System.getProperty("user.dir");
    public static final String FILE_FULL_PATH = FILE_PATH + "/" + FILE_NAME;

    @Override
    public void deliver(Map.Entry<String, Integer> sensorData) throws DeliveryException {
        // todo iterate over the map & write to file
    }

    @Override
    public void deliver(PiSensorHubMetric metric) throws DeliveryException {
        toSaveToFile(serializeToJson(metric));
    }

    private String serializeToJson(PiSensorHubMetric metric) {
        return new Gson().toJson(metric);
    }

    private void toSaveToFile(String metricJson) {
        Path path2File = Path.of(FILE_FULL_PATH);
        if (Files.exists(path2File)) {
            log.debug("Found file with metrics, appending data: {}", FILE_FULL_PATH);
            try {
                String metricForFile = metricJson + '\n';
                Files.write(path2File, metricForFile.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error("Failed to write to file", e );
            }
        } else {
            log.debug("Creating file with metrics: {}", FILE_FULL_PATH);
            try {
//                Path filePath = Files.createFile(path2File);
                Writer writer = Files.newBufferedWriter(path2File);
                writer.append(metricJson).append('\n');
                writer.close();
            } catch (IOException e) {
                log.error("Failed to create file", e);
            }
        }
    }
}
