package docker_client_test.docker_client_test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

public class TesContianerExec {
	public static void main(String[] args) throws InterruptedException, IOException {
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withApiVersion("1.24")
				.withDockerHost("unix:///var/run/docker.sock")
//				.withDockerHost("tcp://localhost:2376")
				.build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config)
				.withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
				.build();

		// Volume nvDriver = new Volume("/usr/local/nvidia");
		// Bind bind = new Bind("nvidia_driver_375.51", nvDriver, AccessMode.ro);

		// CreateVolumeResponse nvidiaVolume =
		// dockerClient.createVolumeCmd().withDriver("nvidia-docker")
		// .withName("nvidia_driver_375.51").exec();
		// String nvidiaVolumePath = nvidiaVolume.getMountpoint()

//		ExecCreateCmdResponse echoCmd = dockerClient.execCreateCmd("test_client").withAttachStderr(true)
//				.withAttachStdin(true).withAttachStdout(true).withTty(true).withCmd("/bin/bash", "-c", "echo hello").exec();
//
//		dockerClient.execStartCmd(echoCmd.getId()).exec(new ExecStartResultCallback(System.out, System.err))
//				.awaitCompletion();

		ExecCreateCmdResponse interactiveCmd = dockerClient.execCreateCmd("test_client").withAttachStderr(true)
				.withAttachStdin(true).withAttachStdout(true).withTty(true).withCmd("/bin/bash").exec();
		
//		InputStream stdin = new ByteArrayInputStream("echo STDIN\n".getBytes());
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					dockerClient.execStartCmd(interactiveCmd.getId()).withStdIn(System.in).exec(new ExecStartTestCallback()).awaitCompletion();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
