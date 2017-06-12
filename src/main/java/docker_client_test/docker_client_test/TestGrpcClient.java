package docker_client_test.docker_client_test;

import java.util.Iterator;

import com.google.protobuf.ByteString;
import com.lenovo.husky.docker.PTYServiceGrpc;
import com.lenovo.husky.docker.PTYServiceGrpc.PTYServiceBlockingStub;
import com.lenovo.husky.docker.TestPTYProtocol;
import com.lenovo.husky.docker.TestPTYProtocol.CmdRequest;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class TestGrpcClient {

	public static void main(String[] args) {
		Channel c = ManagedChannelBuilder.forAddress("localhost", 39999).usePlaintext(true).build();
		PTYServiceBlockingStub stub = PTYServiceGrpc.newBlockingStub(c);
		CmdRequest req = CmdRequest.newBuilder().setBody(ByteString.copyFromUtf8("pwd\n")).build();
		Iterator<TestPTYProtocol.CmdResponse> respIt = stub.command(req);
		while(respIt.hasNext()) {
			System.out.println(respIt.next());
		}
		
	}

}
