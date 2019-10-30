package mqtt.demo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static mqtt.demo.R.id.tv_message;

public class Main3Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        startService(new Intent(this, MQTTService.class));
        findViewById(R.id.btn_send_topic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText)findViewById(R.id.send_topic_message);
                MQTTService.publish(et.getText().toString());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMqttMessage(MQTTMessage mqttMessage) {
        Log.i(MQTTService.TAG, "get message:" + mqttMessage.getMessage());
        Toast.makeText(this, mqttMessage.getMessage(), Toast.LENGTH_LONG).show();
        TextView tv =(TextView)findViewById(tv_message) ;
        tv.setText(mqttMessage.getMessage());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


}
