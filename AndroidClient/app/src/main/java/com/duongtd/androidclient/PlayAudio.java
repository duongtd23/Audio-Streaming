package com.duongtd.androidclient;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioTrack.MODE_STREAM;
import static android.media.AudioTrack.getMinBufferSize;

public class PlayAudio {
    //audio variables
    static final int sampleFreq = 16000;
    static final int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    static final int streamType = STREAM_MUSIC;
    static final int audioMode = MODE_STREAM;
    static final int bufferSize = 10000;//getMinBufferSize(sampleFreq, channelConfig, audioMode);
    static final String debugStr = "DEBUG";

    //socket variables
    public Socket mySocket;
    private static final int SERVERPORT = 33333;
    private static final int BUFFER = 10000;
    private static final String SERVER_IP = "192.168.137.1";

    public PlayAudio() {
        Log.d("INFOMY", "init");
    }

    public void play() {
//        new Listen().execute();
        Do2();
    }

    private class Listen extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Do();
            return null;
        }
    }

    public void Do()
    {
        try {
            //get IP and port number
//                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
//                Log.d(debugStr, "In initial listen connect");
//                //create socket
//                mySocket = new Socket(serverAddr, SERVERPORT);
            AudioTrack myAudioTrack = new AudioTrack(streamType, sampleFreq, channelConfig, audioEncoding, bufferSize, audioMode);

            byte[] buffer = new byte[BUFFER];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                DatagramSocket dsocket = new DatagramSocket(SERVERPORT);
                dsocket.receive(packet);
                myAudioTrack.play();
                myAudioTrack.write(buffer, 0, buffer.length);
                Log.d("INFOMY", "Receive data");

//                    lText = new String(buffer, 0, packet.getLength());
//                    Log.i("UDP packet received", lText);
//                    data.setText(lText);
                packet.setLength(buffer.length);
            }

//                byte[] audioBuffer = new byte[BUFFER];

            //creates input stream readers to read incoming data
//                BufferedInputStream myBis = new BufferedInputStream(mySocket.getInputStream());
//                DataInputStream myDis = new DataInputStream(myBis);
//
//                Log.d(debugStr, "Input created, listener");
//                //Log.d(debugStr, String.valueOf(mySocket.getInputStream().read(audioBuffer)));
//
//                Log.d(debugStr, "track made");
//                // Read the file into the music array.
//                int i = 0;
//                //TODO unsure of while loop condition
//                while (mySocket.getInputStream().read(audioBuffer) != -1) {
//
//                    audioBuffer[audioBuffer.length - 1 - i] = myDis.readByte();
//                    myAudioTrack.play();
//                    myAudioTrack.write(audioBuffer, 0, audioBuffer.length);
//                    i++;
//                }
//                //close input streams
//                myDis.close();
//                myBis.close();

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mySocket == null) {
                String str1 = "Socket became null, listener";
            }
        }
    }

    private void Do2() {
        try{
            AudioTrack myAudioTrack = new AudioTrack(streamType, sampleFreq, channelConfig, audioEncoding, bufferSize, audioMode);

            DatagramSocket Socket = new DatagramSocket(SERVERPORT);
            byte[] receiveData = new byte[BUFFER];
            while(true)
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                Socket.receive(receivePacket);
//                String sentence = new String(receivePacket.getData());
                Log.d("Audio-RECEIVED: ", receivePacket.getLength() + "");

                myAudioTrack.play();
                myAudioTrack.write(receiveData, 0, receiveData.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
