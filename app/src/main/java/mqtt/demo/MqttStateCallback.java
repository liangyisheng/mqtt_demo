package mqtt.demo;

/**
 * Created by Administrator on 2019/10/25.
 */

public interface MqttStateCallback {

    /**
     * mqtt 连接情况
     * @param reconnect 是否重连
     * @param serverURI 地址
     */
    void onMqttPassagewayStatus(boolean reconnect, String serverURI);


    /**
     * mqtt 异常断开
     * @param error 断开原因
     */
    void onMqttConnectionLost(String error);


    /**
     * mqtt 是否连接成功
     * @param isSuccess 成功
     */
    void onMqttConnectStatus(boolean isSuccess);


    /**
     * mqtt 收到消息
     * @param topic 主题
     * @param message 消息体结构
     */
    void onMqttReceiveMessage(String topic, String message);


    /**
     * 弹框提示
     * @param msg
     */
    void onToastShow(String msg);

}
