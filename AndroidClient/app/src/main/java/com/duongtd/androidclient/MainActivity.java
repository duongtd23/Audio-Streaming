package com.duongtd.androidclient;

import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String PHONE_PORT_STR = "phone_port";
    public static final String PHONE_IP_STR = "phone_ip";
    public static final String PC_PORT_STR = "pc_port";
    public static final String PC_IP_STR = "pc_ip";

    public String phoneIp;
    public String phonePort;
    public String pcIp;
    public String pcPort;

    EditText txtPhoneIp, txtPhonePort, txtPcIp, txtPcPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        phonePort = getResources().getString(R.string.phone_port);
        pcIp = getResources().getString(R.string.pc_ip);
        pcPort = getResources().getString(R.string.pc_port);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = (Button) findViewById(R.id.btnStart);
        txtPcIp = (EditText)findViewById(R.id.textPCIp);
        txtPcPort = (EditText)findViewById(R.id.textPCPort);
        txtPhoneIp = (EditText)findViewById(R.id.textPhoneIp);
        txtPhonePort = (EditText)findViewById(R.id.textPhonePort);

        Init();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlayAudio().play();
//        new Receiver().run();
            }
        });
    }

    public void Init() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        phoneIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        txtPhoneIp.setText(phoneIp);

    }
}
