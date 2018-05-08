package com.duongtd.androidclient;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioTrack.MODE_STREAM;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AudioPlayerService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.duongtd.androidclient.action.FOO";
    private static final String ACTION_BAZ = "com.duongtd.androidclient.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.duongtd.androidclient.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.duongtd.androidclient.extra.PARAM2";

    public AudioPlayerService() {
        super("AudioPlayerService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AudioPlayerService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    static final int sampleFreq = 16000;
    static final int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    static final int streamType = STREAM_MUSIC;
    static final int audioMode = MODE_STREAM;
    static final int bufferSize = 10000;//getMinBufferSize(sampleFreq, channelConfig, audioMode);

    //socket variables
    public Socket mySocket;
    private static final int SERVERPORT = 33333;
    private static final int BUFFER = 10000;

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
