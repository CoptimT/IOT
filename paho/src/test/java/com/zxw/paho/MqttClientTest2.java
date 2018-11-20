package com.zxw.paho;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClientTest2 {

	public static void main(String[] args) throws MqttException{
		String brokerUrl = "tcp://172.17.171.17:1883";
		String clientId = "subscribe-cli";
		boolean cleanSession = true;
		String userName = "admin";
		String password = "public";
		int keepAlive = 60;
		
		String topicName = "topic/test/msg";
		int qos = 0;
		MqttClient client = null;
		try {
			client = new MqttClient(brokerUrl, clientId, cleanSession, userName, password, keepAlive);
			client.connect();
			client.subscribe(topicName, qos);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}finally {
			if(client != null && client.isConnected()) {
				client.disconnect();
			}
		}
	}

}
