package mqtt.demo;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText edTopic, etSendTopic, edMqttIp, etSendTopicMessage;
    private Button btnSenTopic, btnSubscription, btnConnect;
    private TextView tvMessage;
    private final String TAG = "hui666";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(Main2Activity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();
    }

    private void initView() {
        edTopic = (EditText) findViewById(R.id.ed_topic);
        etSendTopic = (EditText) findViewById(R.id.et_send_topic);
        etSendTopicMessage = (EditText) findViewById(R.id.send_topic_message);
        btnSenTopic = (Button) findViewById(R.id.btn_send_topic);
        btnSubscription = (Button) findViewById(R.id.btn_subscription);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        edMqttIp = (EditText) findViewById(R.id.ed_mqttip);
        btnConnect.setOnClickListener(this);
        btnSenTopic.setOnClickListener(this);
        btnSubscription.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_subscription:
                subscribe();
                break;
            case R.id.btn_send_topic:
                publish();
                break;
            default:
                break;

        }
    }


    private void publish() {
        String tcp = etSendTopic.getText().toString();
        byte[] message = etSendTopicMessage.getText().toString().getBytes();
        MQTTManage.getInstance().publish(tcp, 2, message);
    }

    /**
     * 订阅
     */
    private void subscribe() {
        String tcp = edTopic.getText().toString();
        MQTTManage.getInstance().subscribe(tcp);
    }


    /**
     * 连接服务
     */
    private void connect() {
        String mqttIp = edMqttIp.getText().toString().trim();
        if (TextUtils.isEmpty(mqttIp)) {
            Toast.makeText(this, "emmmmmmmmm", Toast.LENGTH_SHORT).show();
            return;
        }
        String ip = "tcp://" + mqttIp + ":61613";
        MQTTManage.getInstance().init(mqttStateCallback, ip);
    }


    private MqttStateCallback mqttStateCallback = new MqttStateCallback() {
        @Override
        public void onMqttPassagewayStatus(boolean reconnect, String serverURI) {
            Log.d(TAG, "MQTT连接connectComplete返回状态：" + reconnect + "---serverURI:" + serverURI);
        }

        @Override
        public void onMqttConnectionLost(String error) {
            Log.d(TAG, "MQTT连接异常断开：" + error);
        }

        @Override
        public void onMqttConnectStatus(final boolean isSuccess) {
            Message message = handler.obtainMessage();
            message.obj = isSuccess ? "连接成功" : "连接不成功";
            handler.sendMessage(message);
        }

        @Override
        public void onMqttReceiveMessage(String topic, String message) {
            tvMessage.setText("收到消息主题：" + topic + "/" + message);
        }

        @Override
        public void onToastShow(String msg) {
            Message message = handler.obtainMessage();
            message.obj = msg;
            handler.sendMessage(message);
        }
    };


}

