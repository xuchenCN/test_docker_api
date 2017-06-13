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
	
	StreamObserver<CmdResponse> oneWriter;

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
			if(oneWriter == null) {
				oneWriter = responseObserver;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							while (remoteIn.read(buf) != -1) {
								CmdResponse resp = CmdResponse.newBuilder().setBody(ByteString.copyFrom(buf)).build();
								responseObserver.onNext(resp);
								System.out.println("on next " + resp);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			} else {
				System.out.println("grpc command completed");
				responseObserver.onCompleted();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
