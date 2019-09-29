# Desperate Pi IoT  (pi-tracker)
It is a small microservice written in Java with the help of Micronaut framework.
The main purpose of this project is to collect data from on-board sensors and publish it to MQTT Broker.
## Technical specification & Requirements
**Desperate Pi IoT (DPI)** was created with certain hardware in mind hence it should meet following requirements. 
Notice that currently there are no plans to make it more device agnostic, however they might be in further releases.

### Board Requirements
Currently only Raspberry 3B+ is supported. However limitations here are inherited from [Pi4J project](https://pi4j.com/1.2/index.html)
as it used to access I/O capabilities of the board. So theoretically any board which is supported by Pi4J should do fine.<br>
NOTICE: I2C should be enabled for DPI to work (it is disabled by default in Raspbian builds).

### Sensors requirements
As for now only 
[DockerPi Sensor Hub Development Board SKU: EP-0106](https://wiki.52pi.com/index.php/DockerPi_Sensor_Hub_Development_Board_SKU:_EP-0106?spm=a2g0o.detail.1000023.1.3cbc6268w9u6GE)
 is supported. As sensor's addresses and appropriate MQTT Topics are hardcoded there is currently no way DPI will
  properly work with sensors/sensor-hubs other than EP-0106. There might be changes in this requirement in future releases though.

### Software requirements
For proper functioning DPI requires:
 - Java 11
 - MQTT Broker
 
Testing was performed on Raspbian() with default OpenJDK build(build 11.0.3+7-post-Raspbian-5) and [Mosquitto](https://mosquitto.org/) with
 default configuration used as a MQTT Broker. 
However OS and Broker requirements should not be considered strict as no exclusive functionality of these components was used.
So again theoretically any OS having a JRE build and some MQTTBroker deployed should do. 

## Architecture & Configuration
### MQTT Config
#### Topics
By default sensor's names are used as MQTT Topics names:<br>

| Sensor Name | MQTT Topic |
| ----------- | ---------- |
| Temperature Sensor | /temperature |
| Light Sensor | /light |
| Human Sensor | /human |
| Air Pressure Temperature | /air-pressure-temperature |
| Air Pressure | /air-pressure |
| On-board Humidity | /on-board-humidity |
| On-board Temperature | /on-board-temperature |

To specify a custom prefix for topics property _pi-tracker.mqtt.topic-prefix_ is used.
Specified prefix is added before the topic name from the table above.<br>
Example:<br> pi-tracker.mqtt.topic-prefix = pi/living-room/<br> will result in all topics have such prefix:<br>
* pi/living-room/temperature
* pi/living-room/light
* etc

#### QoS
By specifying value for property _pi-tracker.mqtt.qos_ 
you can set QoS for publishing messages (by default it is 0).<br> 
