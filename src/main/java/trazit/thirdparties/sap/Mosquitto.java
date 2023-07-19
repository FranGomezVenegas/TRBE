/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.thirdparties.sap;
import org.eclipse.paho.client.mqttv3.*;
/**
 *
 * @author User
 */
public class Mosquitto {
    public static String sendMosquitto() {
        String broker = "tcp://51.75.202.142:9001"; // Dirección del servidor Mosquitto
        String topic = "myTopic"; // Tema al que se enviará el mensaje
        String message = "Hello, MQTT!"; // Mensaje a enviar

        String clientId = MqttClient.generateClientId();
        try (MqttClient client = new MqttClient(broker, clientId)) {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);

            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage);

            client.disconnect();
            return "mensaje enviado";
        } catch (MqttException e) {
            e.printStackTrace();
            return e.getMessage()+" "+broker;
        }
    }
}

