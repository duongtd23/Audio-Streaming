import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class PcServer extends JFrame {

    boolean stopaudioCapture = false;
    ByteArrayOutputStream byteOutputStream;
    AudioFormat adFormat;
    TargetDataLine targetDataLine;
    AudioInputStream inputStream;
    SourceDataLine sourceLine;
    Graphics g;

    public static final int BUFFER_SIZE = 10000;
    public static final int SAMPLE_RATE = 16000;

    String serverIPStr = "192.168.137.103";
    String serverPortStr = "33333";

    public static void main(String args[]) {
//        BUFFER_SIZE = SAMPLE_RATE/5;
        new PcServer();
    }

    public PcServer() {
        final JTextField serverIP = new JTextField(serverIPStr);//"192.168.137.103");
        final JTextField serverPort = new JTextField(serverPortStr);

        final JButton capture = new JButton("Capture");
        final JButton stop = new JButton("Stop");
        final JButton play = new JButton("Playback");
        final JButton listenPhoneInfo = new JButton("Listen Info");

        capture.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        getContentPane().add(serverIP);
        getContentPane().add(serverPort);

        capture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                serverIPStr = serverIP.getText();
                serverPortStr = serverPort.getText();
                capture.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(false);
                captureAudio();
            }
        });
        getContentPane().add(capture);

        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                capture.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                stopaudioCapture = true;
                targetDataLine.close();
            }
        });
        getContentPane().add(stop);

        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playAudio();
            }
        });
//        getContentPane().add(play);

        listenPhoneInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerSocket welcomeSocket = new ServerSocket(33334);
                    Socket connectionSocket = welcomeSocket.accept();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    String str = inFromClient.readLine();
                    System.out.println("Received: " + str);
                    String[] temp = str.trim().split(":");
                    serverIP.setText(temp[0]);
                    serverPort.setText(temp[1]);
                    getContentPane().revalidate();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        getContentPane().add(listenPhoneInfo);

        getContentPane().setLayout(new FlowLayout());
        setTitle("Capture/Playback Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 100);
        getContentPane().setBackground(Color.white);
        setVisible(true);

        g = (Graphics) this.getGraphics();
    }

    private void captureAudio() {
        try {
            adFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(adFormat);
            targetDataLine.start();
            int size = targetDataLine.getBufferSize();
            System.out.println(size);
            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            StackTraceElement stackEle[] = e.getStackTrace();
            for (StackTraceElement val : stackEle) {
                System.out.println(val);
            }
            System.exit(0);
        }
    }

    private void playAudio() {
        try {
            byte audioData[] = byteOutputStream.toByteArray();
            InputStream byteInputStream = new ByteArrayInputStream(audioData);
            AudioFormat adFormat = getAudioFormat();
            inputStream = new AudioInputStream(byteInputStream, adFormat, audioData.length / adFormat.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceLine.open(adFormat);
            sourceLine.start();
            Thread playThread = new Thread(new PlayThread());
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte tempBuffer[] = new byte[BUFFER_SIZE];

        public void run() {

            byteOutputStream = new ByteArrayOutputStream();
            stopaudioCapture = false;
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName(serverIPStr);//"192.168.137.251");
                while (!stopaudioCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        DatagramPacket sendPacket = new DatagramPacket(tempBuffer, tempBuffer.length, IPAddress, Integer.parseInt(serverPortStr));
                        clientSocket.send(sendPacket);
//                        byteOutputStream.write(tempBuffer, 0, cnt);
                    }
                }
                byteOutputStream.close();
            } catch (Exception e) {
                System.out.println("CaptureThread::run()" + e);
                System.exit(0);
            }
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[BUFFER_SIZE];

        public void run() {
            try {
                int cnt;
                while ((cnt = inputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceLine.write(tempBuffer, 0, cnt);
                    }
                }
                //                sourceLine.drain();
                //             sourceLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public static AudioFormat getAudioFormat() {
        float sampleRate = SAMPLE_RATE;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(
                sampleRate, sampleInbits, channels,
                signed,
                bigEndian);
   }
}