package com.zxw.paho;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSubscribeSample {

	public static void main(String[] args) throws MqttException {
		String topic = "MQTT Examples";
		String content = "Message from MqttPublishSample";
		int qos = 2;
		String broker = "tcp://172.17.171.17:1883";
		String clientId = "MqttSubscribeSample";
		MqttClient sampleClient = null;
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			sampleClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			System.out.println("Connecting to broker: " + broker);
			sampleClient.connect(connOpts);
			System.out.println("Connected");
//			System.out.println("Publishing message: " + content);
//			MqttMessage message = new MqttMessage(content.getBytes());
//			message.setQos(qos);
//			sampleClient.publish(topic, message);
			sampleClient.subscribe(topic, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println("Message Arrived");
					System.out.println(topic + " : " +new String(message.getPayload()));
					
				}
			});
//			System.exit(0);
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}finally {
			sampleClient.disconnect();
			System.out.println("Disconnected");
		}
	}
}
