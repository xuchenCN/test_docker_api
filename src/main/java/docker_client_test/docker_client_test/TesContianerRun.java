package docker_client_test.docker_client_test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

public class TesContianerRun {
	public static void main(String[] args) throws InterruptedException {
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withApiVersion("1.24")
				.withDockerHost("unix:///var/run/docker.sock")
				.build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config)
				.withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
				.build();
		try {
			dockerClient.removeContainerCmd("test_client").withRemoveVolumes(true).withForce(true).exec();
		} catch (Exception e) {
		}

//		Volume nvDriver = new Volume("/usr/local/nvidia");
//		Bind bind = new Bind("nvidia_driver_375.51", nvDriver, AccessMode.ro);

		// CreateVolumeResponse nvidiaVolume =
		// dockerClient.createVolumeCmd().withDriver("nvidia-docker")
		// .withName("nvidia_driver_375.51").exec();
		// String nvidiaVolumePath = nvidiaVolume.getMountpoint()

		HostConfig hostConfig = new HostConfig();
		hostConfig.withVolumeDriver("nvidia-docker");
		CreateContainerResponse container = dockerClient.createContainerCmd("d3add3c73fed").withHostConfig(hostConfig)
				.withTty(true)
				.withAttachStdin(true)
				.withAttachStdout(true)
				.withAttachStderr(true)
				.withStdinOpen(true)
				.withStdInOnce(true)
				.withCmd("/bin/bash")
				.withName("test_client")
				.exec();

		dockerClient.startContainerCmd(container.getId()).exec();
		//
		// LogContainerTestCallback loggingCallback = new
		// LogContainerTestCallback(true);
		//
		// dockerClient.logContainerCmd(container.getId())
		// .withStdErr(true)
		// .withStdOut(true)
		// .withFollowStream(true)
		// .withTailAll()
		// .exec(new ExecCallback());

		// System.out.println("log " +loggingCallback.toString());

		int code = dockerClient.waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback())
				.awaitStatusCode();
		System.out.println("exit code " + code);

	}
}
