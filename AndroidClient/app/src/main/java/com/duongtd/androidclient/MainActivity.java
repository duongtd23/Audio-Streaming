package com.duongtd.androidclient;

import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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
        Button btnSendInfo = (Button) findViewById(R.id.btnSendInfo);

        txtPcIp = (EditText)findViewById(R.id.textPCIp);
        txtPcPort = (EditText)findViewById(R.id.textPCPort);
        txtPhoneIp = (EditText)findViewById(R.id.textPhoneIp);
        txtPhonePort = (EditText)findViewById(R.id.textPhonePort);

        Init();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayerService.startActionFoo(MainActivity.this, "", "");
//        new Receiver().run();
            }
        });

        btnSendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendInfoTcp();
                    Toast.makeText(getApplicationContext(), "Sent the ip and port listen to pc", Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Init() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        phoneIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        txtPhoneIp.setText(phoneIp);
    }

    public void sendInfoTcp() throws Exception {
        String pcIpStr = txtPcIp.getText().toString();
        String pcPortStr = txtPcPort.getText().toString();
        String phoneIp = txtPhoneIp.getText().toString();
        String phonePort = txtPhonePort.getText().toString();
        InetAddress serverAddr = InetAddress.getByName(pcIpStr);
//        Log.e("TCP Client", "C: Connecting...");
        Socket socket = new Socket(serverAddr, Integer.parseInt(pcPortStr));
        OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
//        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
        PrintWriter _bufferOut = new PrintWriter(new BufferedWriter(osw), true);
//        _bufferIn = new BufferedReader(isr);
        _bufferOut.println(phoneIp + ":" + phonePort);
        _bufferOut.flush();
    }
}
