package com.zxw.paho;

import java.io.File;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class MqttClient {
	private MqttAsyncClient client;
	private String brokerUrl;
	private MqttConnectOptions conOpt;
	private boolean cleanSession;
	private String userName;
	private String password;
	private int keepAlive;
	
	private IMqttActionListener mqttActionListener = new IMqttActionListener() {
		@Override
		public void onSuccess(IMqttToken asyncActionToken) {
			log("Connected to " + client.getServerURI());
		}

		@Override
		public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
			log("Connect failed:" + exception);
			//log("Reconnecting...");
			//MqttTest.this.connect();
		}
	};
	
	private MqttCallback mqttCallback = new MqttCallback() {
		public void connectionLost(Throwable cause) {
			log("Connection to " + brokerUrl + " was lost!" + cause);
			//log("Reconnecting...");
			//MqttTest.this.connect();
		}
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String messageString = new String(message.getPayload());
			log(topic+": "+messageString);
		}
		public void deliveryComplete(IMqttDeliveryToken token) {
			try {
				MqttMessage msg = token.getMessage();
				log("deliveryComplete: "+new String(msg.getPayload()));
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	};
	
	public MqttClient(String brokerUrl, String clientId, boolean cleanSession, String userName, String password, int keepAlive) throws MqttException {
		this.brokerUrl = brokerUrl;
		this.cleanSession = cleanSession;
		this.userName = userName;
		this.password = password;
		this.keepAlive = keepAlive;
		
		// Construct the connection options object that contains connection parameters such as cleanSession and LWT
		conOpt = new MqttConnectOptions();
		conOpt.setAutomaticReconnect(false);
		conOpt.setCleanSession(this.cleanSession);
		if (password != null) {
			conOpt.setPassword(this.password.toCharArray());
		}
		if (userName != null) {
			conOpt.setUserName(this.userName);
		}
		/**
		 * 设置“保持活动”间隔。 此值（以秒为单位）定义发送或接收的消息之间的最大时间间隔。 它使客户端能够检测服务器是否不再可用，而无需等待TCP / IP超时。
		 * 客户端将确保在每个KeepAlive期间至少有一条消息在网络中传播。 在该时间段没有与数据相关的消息时，客户端发送非常小的“ping”消息，服务器将确认该消息。
		 * 值为0将禁用客户端中的KeepAlive处理。 默认值为60秒。
		 */
		conOpt.setKeepAliveInterval(this.keepAlive);
		if (this.brokerUrl.startsWith("ssl")) {
			try {
				/*String path = SSL.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
				int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
				int lastIndex = path.lastIndexOf(File.separator) + 1;
				path = path.substring(firstIndex, lastIndex);
				if (!path.isEmpty()) {
					path = path + "/";
				}
				conOpt.setSocketFactory(new SSL(path).getSSLSocktet());*/
				System.out.println("ssl ...");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// This sample stores in a temporary directory where messages temporarily stored until the message has been delivered to the server.
		// a real application ought to store them somewhere where they are not likely to get deleted or tampered with
		// 一个真实的应用程序应该将它们存储在不太可能被删除或被篡改的地方
		String tmpDir = System.getProperty("java.io.tmpdir");
		MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(tmpDir);
		// Construct an MQTT blocking mode client
		client = new MqttAsyncClient(this.brokerUrl, clientId, persistence);
		// Set this wrapper as the callback handler
		client.setCallback(this.mqttCallback);
	}

	public void connect() throws MqttException {
		client.connect(conOpt, this, mqttActionListener).waitForCompletion(-1);
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}

	public void subscribe(String topicName, int qos) throws MqttException {
		// Subscribe to the requested topic
		// The QoS specified is the maximum level that messages will be sent to the client at.
		// For instance if QoS 1 is specified, any messages originally published at QoS 2 will be downgraded to 1 when delivering to the client
		// but messages published at 1 and 0 will be received at the same level they were published at.
		String[] topicArray = topicName.split(";");
		int[] qosArray = new int[topicArray.length];
		for (int i = 0; i < topicArray.length; i++) {
			qosArray[i] = qos;
		}

		client.subscribe(topicArray, qosArray);
	}

	public void publish(String topicName, String message, int qos) throws MqttException {
		MqttMessage msg = new MqttMessage();
		msg.setPayload(message.getBytes());
		msg.setQos(qos);
		msg.setRetained(true);
		client.publish(topicName, msg);
	}

	public void disconnect() throws MqttException {
		// Disconnect the client from the server
		client.disconnect();
	}

	private static void log(String message) {
		System.out.println(message);
	}
}
