package habib.voip;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import com.purplefrog.speexjni.FrequencyBand;
import com.purplefrog.speexjni.SpeexEncoder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpSender extends Thread {

	@Override
	public void run() {
		try {
            SpeexEncoder encoder = new SpeexEncoder(FrequencyBand.WIDE_BAND, 4);
			int minBufferSize = AudioRecord.getMinBufferSize(
					MainActivity.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			AudioRecord audioRecord = new AudioRecord(AudioSource.MIC,
					MainActivity.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

			audioRecord.startRecording();
			DatagramSocket datagramSocket = Manager.getManager().getUdpSocket();

            Log.i(Values.LogTag, Values.running + "");
            // byte i = 0;
            while (Values.running) {
				short[] audioData = new short[encoder.getFrameSize()];
                int read = audioRecord.read(audioData, 0, audioData.length);
                if(read == audioData.length) {
                    byte[] encode = encoder.encode(audioData);
                    DatagramPacket datagramPacket = new DatagramPacket(encode, encode.length, Manager.getManager().ConnectedIpAddress, Manager.getManager().ConnectedPort);
                    datagramSocket.send(datagramPacket);
                }
            }
            datagramSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
