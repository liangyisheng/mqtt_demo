package mqtt.demo;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Administrator on 2019/10/25.
 */

public class MQTTManage {
    private final String TAG = "hui666";
    private static MQTTManage manage ;
    private MqttAsyncClient client;
    private MqttConnectOptions mqttConnectOptions;
    private MqttStateCallback mqttStateCallback;


    public static  MQTTManage getInstance(){
        if(manage == null){
            manage = new MQTTManage();
        }
        return manage;
    }



    /**
     * 初始化
     */
    public void init(MqttStateCallback mqttStateCallback,String ip){
        this.mqttStateCallback = mqttStateCallback;
        try {
            client = new MqttAsyncClient(ip,"adminxxxx" + System.currentTimeMillis() / 1000,new MemoryPersistence());
            //设置监听消息，连接中断收消息啥的
            client.setCallback(mqttCallbackExtended);
            //构建核心数据
            mqttConnectOptions = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
             mqttConnectOptions.setCleanSession(false);
            // 设置超时时间，单位：秒
             mqttConnectOptions.setConnectionTimeout(360);
            // 设置会话心跳时间，单位：秒，服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
             mqttConnectOptions.setKeepAliveInterval(6 * 10);
            // 设置mqtt断线重连
             mqttConnectOptions.setAutomaticReconnect(true);
            //设置用户名
            mqttConnectOptions.setUserName("admin");
            mqttConnectOptions.setPassword("password".toCharArray());
            //连接
            if(client != null){
                clientConnection();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




    private void clientConnection(){
        if(!client.isConnected()){
            try {
                client.connect(mqttConnectOptions, null, actionListener).waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 监听连接状态
     */
    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            mqttStateCallback.onMqttPassagewayStatus(reconnect, serverURI);
            Log.d(TAG,"MQTT连接connectComplete返回状态：" + reconnect + "---serverURI:" + serverURI);
        }

        @Override
        public void connectionLost(Throwable cause) {
            mqttStateCallback.onMqttConnectionLost(cause.getMessage());
            Log.d(TAG,"MQTT连接异常断开：" + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String  msg = new String(message.getPayload(), "UTF-8");
            Log.d(TAG,"MQTT收到消息：" + msg + "/" + topic);
            mqttStateCallback.onMqttReceiveMessage(topic,msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    /**
     * 发布消息
     *
     * @param topic 消息主题
     * @param msg   消息内容
     */
    public void publish(String topic, Integer qos, byte[] msg) {
        Boolean retained = false;
        if (client != null) {
            try {
                client.publish(topic, msg, qos.intValue(), retained.booleanValue());
                mqttStateCallback.onToastShow("发送成功");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 订阅
     *
     * @param topic 订阅主题
     */
    public void subscribe(String topic) {
        Integer qos = 2;
        if (client != null && client.isConnected()) {
            try {
                client.subscribe(topic, qos);
                mqttStateCallback.onToastShow("订阅成功");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * 监听连接结果
     */
    private IMqttActionListener actionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            mqttStateCallback.onMqttConnectStatus(true);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            mqttStateCallback.onMqttConnectStatus(false);
        }
    };



}
