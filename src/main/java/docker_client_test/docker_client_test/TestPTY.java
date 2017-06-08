package docker_client_test.docker_client_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.pty4j.PtyProcess;

public class TestPTY {

	public static void main(String[] args) throws Exception {
		String[] cmd = new String[] { "/Applications/Docker.app/Contents/Resources/bin/docker", "exec", "-i",
				"test_client", "/bin/bash" };
		PtyProcess pty = PtyProcess.exec(cmd);
		// pb.inheritIO();
		// pb.command("/bin/bash");

		final BufferedReader errReader = new BufferedReader(
				new InputStreamReader(pty.getErrorStream(), Charset.defaultCharset()));
		final BufferedReader inReader = new BufferedReader(
				new InputStreamReader(pty.getInputStream(), Charset.defaultCharset()));

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(pty.getOutputStream()));
		// OutputStream out = process.getOutputStream();

		new Thread(new InputReader(errReader, "stderr")).start();
		new Thread(new InputReader(inReader, "stdout")).start();
		new Thread(new OutputSender(out)).start();
		System.out.println("exit code :" + pty.waitFor());
	}

}
