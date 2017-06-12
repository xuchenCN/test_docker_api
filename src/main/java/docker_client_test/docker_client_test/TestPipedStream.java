package docker_client_test.docker_client_test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import io.netty.buffer.Unpooled;

public class TestPipedStream {

	public static void main(String[] args) throws IOException {
		PipedOutputStream remoteOut = new PipedOutputStream();
		PipedInputStream pipIn = new PipedInputStream(remoteOut);
		
		remoteOut.write("hello".getBytes());

		new Thread(new Runnable() {

			private int read(InputStream is, byte[] buf) {
				try {
					return is.read(buf);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void run() {
				try {
					byte[] buffer = new byte[1024];
					int read;
					while ((read = read(pipIn, buffer)) != -1) {
						System.out.println("pipin : " + buffer);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		System.in.read();
	}

}
