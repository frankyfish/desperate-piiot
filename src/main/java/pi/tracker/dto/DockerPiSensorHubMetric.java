package pi.tracker.dto;

import lombok.*;

import java.io.Serializable;

/**
 * A data object representing one single metric collected from EP-0106 board.
 * Notice that some fields might be empty if corresponding sensors were not mentioned in {@link pi.tracker.dockerpi.SensorHub}
 * instance.
 * @see <a href="https://wiki.52pi.com/index.php/DockerPi_Sensor_Hub_Development_Board_SKU:_EP-0106?spm=a2g0o.detail.1000023.1.3cbc6268w9u6GE">
 *     DockerPi Sensor Hub Development Board SKU: EP-0106
 *     </a>
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockerPiSensorHubMetric implements Serializable {

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
