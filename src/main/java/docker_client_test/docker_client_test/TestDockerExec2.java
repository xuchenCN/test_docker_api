package docker_client_test.docker_client_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

public class TestDockerExec2 {

	public static void main(String[] args) throws Exception {
		ProcessBuilder pb = new ProcessBuilder();
//		 pb.inheritIO();
		pb.command("/Applications/Docker.app/Contents/Resources/bin/docker", "exec", "-i", "test_client", "/bin/bash");
//		pb.command("/bin/bash");
		Process process = pb.start();

		final BufferedReader errReader = new BufferedReader(
				new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()));
		final BufferedReader inReader = new BufferedReader(
				new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		// OutputStream out = process.getOutputStream();

		new Thread(new InputReader(errReader, "stderr")).start();
		new Thread(new InputReader(inReader, "stdout")).start();
		new Thread(new OutputSender(out)).start();
		System.out.println("exit code :" + process.waitFor());
	}

}

class OutputSender implements Runnable {

	BufferedWriter out;

	// OutputStream out;
	OutputSender(BufferedWriter out) {
		this.out = out;
	}

	@Override
	public void run() {

		try (Scanner sc = new Scanner(System.in)) {
			while (sc.hasNextLine() && !Thread.interrupted()) {
				String line = sc.nextLine();
				// System.out.println("stdin" + " : " + line);
				out.write(line);
				out.newLine();
				out.flush();
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}

}

class InputReader implements Runnable {
	BufferedReader reader;
	String name;

	public InputReader(BufferedReader reader, String name) {
		this.reader = reader;
		this.name = name;
	}

	@Override
	public void run() {
		try {
			String line = reader.readLine();
			while ((line != null) && !Thread.interrupted()) {
				System.out.println(name + " : " + line);
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}