package pi.tracker.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
public class PiSensorHubMetric implements Serializable {

    private String timeStamp;
    private String deviceLocation;
    private double externalTemperatureSensor;
    private double light;
    private int human;
    private double airPressureTemperature;
    private double airPressureSensor;
    private double onBoardHumidity;
    private double onBoardTemperature;

}
