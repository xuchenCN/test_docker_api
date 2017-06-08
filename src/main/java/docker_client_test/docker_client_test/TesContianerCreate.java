package docker_client_test.docker_client_test;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Device;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;

public class TesContianerCreate {
	public static void main(String[] args) throws InterruptedException {
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withApiVersion("1.24")
				.withDockerHost("unix:///var/run/docker.sock").build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
		try {
			dockerClient.removeContainerCmd("test_client").withRemoveVolumes(true).withForce(true).exec();
		} catch (Exception e) {
		}

		Volume nvDriver = new Volume("/usr/local/nvidia");
		Bind bind = new Bind("nvidia_driver_375.51", nvDriver, AccessMode.ro);

		// CreateVolumeResponse nvidiaVolume =
		// dockerClient.createVolumeCmd().withDriver("nvidia-docker")
		// .withName("nvidia_driver_375.51").exec();
		// String nvidiaVolumePath = nvidiaVolume.getMountpoint()

		HostConfig hostConfig = new HostConfig();
		hostConfig.withVolumeDriver("nvidia-docker");
		CreateContainerResponse container = dockerClient.createContainerCmd("ede52c2645b2").withHostConfig(hostConfig)
				.withVolumes(nvDriver).withBinds(bind).withAttachStdin(true).withAttachStdout(true).withAttachStderr(true)
				.withTty(true).withStdinOpen(true).withStdInOnce(true)
				.withDevices(new Device("rwm", "/dev/nvidia-uvm-tools", "/dev/nvidia-uvm-tools"),
						new Device("rwm", "/dev/nvidia-uvm", "/dev/nvidia-uvm"),
						new Device("rwm", "/dev/nvidiactl", "/dev/nvidiactl"),
						new Device("rwm", "/dev/nvidia0", "/dev/nvidia0"))
				.withCmd("/bin/bash")
				// .withCmd("echo","hell1")
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

		//
		// dockerClient.attachContainerCmd(container.getId())
		// .withStdOut(true)
		// .withStdErr(true)
		// .withLogs(true)
		// .exec(new ExecStartResultCallback(System.out,
		// System.err)).awaitCompletion();
		//

		//
		// String execId =
		// dockerClient.execCreateCmd(container.getId()).withCmd("echo1",
		// "hello").withAttachStdout(true)
		// .withAttachStderr(true).withTty(true).exec().getId();
		// dockerClient.execStartCmd(execId).exec(new
		// ExecStartResultCallback(System.out, System.err)).awaitCompletion();
	}
}

class LogContainerTestCallback extends LogContainerResultCallback {
	protected final StringBuffer log = new StringBuffer();
	List<Frame> collectedFrames = new ArrayList<Frame>();
	boolean collectFrames = false;

	public LogContainerTestCallback() {
		this(false);
	}

	public LogContainerTestCallback(boolean collectFrames) {
		this.collectFrames = collectFrames;
	}

	@Override
	public void onNext(Frame frame) {
		System.out.println("frame " + frame.toString());
		if (collectFrames)
			collectedFrames.add(frame);
		log.append(new String(frame.getPayload()));
	}

	@Override
	public String toString() {
		return log.toString();
	}

	public List<Frame> getCollectedFrames() {
		return collectedFrames;
	}
}

class ExecCallback implements ResultCallback<Frame> {
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("close");
	}

	@Override
	public void onStart(Closeable closeable) {
		// TODO Auto-generated method stub
		System.out.println("onStart");
	}

	@Override
	public void onNext(Frame object) {
		// TODO Auto-generated method stub
		System.out.println("onNext " + object);
	}

	@Override
	public void onError(Throwable throwable) {
		// TODO Auto-generated method stub
		System.out.println("onError " + throwable);
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		System.out.println("onComplete ");
	}
}

class ExecStartTestCallback extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

	@Override
	public void onNext(Frame frame) {
		if (frame != null) {
			try {
				switch (frame.getStreamType()) {
				case STDOUT:
					System.out.println("stdout : " + new String(frame.getPayload()));
				case RAW:
					System.out.println("raw : " + new String(frame.getPayload()));
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

			 System.out.println(frame.toString());
		}
	}
}