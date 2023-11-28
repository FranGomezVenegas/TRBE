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
    public static String getFromMosquitto(){
        String broker = "tcp://51.75.202.142:1883"; // Dirección del servidor Mosquitto
        String clientId = "trazitClient"; // Choose a unique client ID
        String topic = "myTopic"; // Replace with the topic you want to subscribe to

        try (MqttClient mqttClient = new MqttClient(broker, clientId)) {

            // Set up MQTT connection options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("trazit");
            connOpts.setPassword("trazit4ever".toCharArray());

            // Connect to the MQTT broker
            mqttClient.connect(connOpts);

            // Subscribe to the specified topic
            mqttClient.subscribe(topic);

            // Set up a callback for incoming messages
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Handle the received message
                    String payload = new String(message.getPayload());
                    System.out.println("Received message on topic: " + topic);
                    System.out.println("Message: " + payload);                    
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // This method is called when a message is successfully delivered
                }
            });

            // Wait for messages indefinitely
            while (true) {
                Thread.sleep(1000); // You can adjust the sleep duration as needed
            }

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }    
        return "hola";
    }
    public static String sendMosquitto() {
        String broker = "tcp://51.75.202.142:1883"; // Dirección del servidor Mosquitto
        String topic = "myTopic"; // Tema al que se enviará el mensaje
        String message = "Hello, MQTT!"; // Mensaje a enviar

        String clientId = MqttClient.generateClientId();
        try (MqttClient client = new MqttClient(broker, clientId)) {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("trazit");
            connOpts.setPassword("trazit4ever".toCharArray());
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

