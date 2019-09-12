package pi.tracker.dockerpi.exceptions;

import lombok.Getter;

/**
 * Exception class that is supposed to be used in case there is some unwanted response
 * received from sensor.
 */
@Getter
public class SensorException extends RuntimeException {

    private final String sensorName;

    public SensorException(String rootCause, String sensorName) {
        super(rootCause);
        this.sensorName = sensorName;
    }
}
