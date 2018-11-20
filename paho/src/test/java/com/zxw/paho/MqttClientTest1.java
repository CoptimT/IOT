package com.zxw.paho;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClientTest1 {

	public static void main(String[] args) throws MqttException{
		String brokerUrl = "tcp://172.17.171.17:1883";
		String clientId = "publish-cli";
		boolean cleanSession = true;
		String userName = "admin";
		String password = "public";
		int keepAlive = 60;
		
		String topicName = "topic/test/msg";
		String message = "Hello EMQ";
		int qos = 0;
		MqttClient client = null;
		try {
			client = new MqttClient(brokerUrl, clientId, cleanSession, userName, password, keepAlive);
			client.connect();
			for(int i=0;i<10;i++) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				client.publish(topicName, message+" "+i, qos);
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
