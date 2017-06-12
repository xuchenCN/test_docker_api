package docker_client_test.docker_client_test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import com.google.protobuf.ByteString;
import com.lenovo.husky.docker.PTYServiceGrpc.AbstractPTYService;
import com.lenovo.husky.docker.TestPTYProtocol.CmdRequest;
import com.lenovo.husky.docker.TestPTYProtocol.CmdResponse;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class PTYServerMain {

	public static void main(String[] args) throws Exception {

		PipedInputStream remoteIn = new PipedInputStream();
		PipedOutputStream pipOut = new PipedOutputStream(remoteIn);

		PipedOutputStream remoteOut = new PipedOutputStream();
		PipedInputStream pipIn = new PipedInputStream(remoteOut);

		AbstractPTYService ptyService = new PTYService(remoteIn, remoteOut);
		Server serv = ServerBuilder.forPort(39999).addService(ptyService).build();

		StartExec startExec = new StartExec();
		startExec.start(pipIn, pipOut);

		serv.start();
		System.out.println("Server startup");
		serv.awaitTermination();
	}

}

class PTYService extends AbstractPTYService {
	InputStream remoteIn;
	OutputStream remoteOut;
	byte[] buf = new byte[1024];

	public PTYService(InputStream remoteIn, OutputStream remoteOut) {
		this.remoteIn = remoteIn;
		this.remoteOut = remoteOut;
	}

	@Override
	public void command(CmdRequest request, StreamObserver<CmdResponse> responseObserver) {
		try {
			ByteString body = request.getBody();
			if (body != null) {
				System.out.println("request : " + body);

				remoteOut.write(body.toByteArray());
			}
//			int read;
			while (remoteIn.read(buf) != -1) {
				CmdResponse resp = CmdResponse.newBuilder().setBody(ByteString.copyFrom(buf)).build();
				responseObserver.onNext(resp);
				System.out.println("on next " + resp);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("grpc command completed");
		responseObserver.onCompleted();
	}

}

class CmdObserver implements StreamObserver<CmdResponse> {

	@Override
	public void onNext(CmdResponse value) {
		System.out.println("onNext : " + value.getBody());
	}

	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		System.out.println("onError : ");
		t.printStackTrace();
	}

	@Override
	public void onCompleted() {
		System.out.println("onCompleted : ");
	}

}

class StartExec {

	public void start(InputStream attachIn, OutputStream attachOut) {

		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withApiVersion("1.24")
				.withDockerHost("unix:///var/run/docker.sock").build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config)
				.withDockerCmdExecFactory(new NettyDockerCmdExecFactory()).build();

		ExecCreateCmdResponse interactiveCmd = dockerClient.execCreateCmd("test_client").withAttachStderr(true)
				.withAttachStdin(true).withAttachStdout(true).withTty(true).withCmd("/bin/bash").exec();

		// InputStream stdin = new ByteArrayInputStream("echo
		// STDIN\n".getBytes());

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					dockerClient.execStartCmd(interactiveCmd.getId()).withStdIn(attachIn).exec(new ExecStartStream(attachOut))
							.awaitCompletion();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

}

class ExecStartStream extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

	OutputStream out;

	public ExecStartStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void onNext(Frame frame) {
		if (frame != null) {
			try {
				switch (frame.getStreamType()) {
				case STDOUT:
					// System.out.println("stdout : " + new
					// String(frame.getPayload()));
				case RAW:
					System.out.println("raw : " + new String(frame.getPayload()));
					out.write(frame.getPayload());
					break;
				case STDERR:
					System.out.println("stderr : " + new String(frame.getPayload()));
					break;
				default:
					System.out.println("unknown stream type:" + frame.getStreamType());
				}
			} catch (Exception e) {
				onError(e);
			}

			// System.out.println(frame.toString());
		}
	}
}