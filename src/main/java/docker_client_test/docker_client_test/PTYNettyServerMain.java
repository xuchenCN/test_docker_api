package docker_client_test.docker_client_test;

import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class PTYNettyServerMain {

	public static void main(String[] args) throws Exception {
		PipedInputStream remoteIn = new PipedInputStream();
		PipedOutputStream pipOut = new PipedOutputStream(remoteIn);

		PipedOutputStream remoteOut = new PipedOutputStream();
		PipedInputStream pipIn = new PipedInputStream(remoteOut);

		StartExec startExec = new StartExec();
		startExec.start(pipIn, pipOut);

		ExecStreamServer server = new ExecStreamServer(39999);
		server.start(remoteIn, remoteOut);

	}
}

class ExecStreamServer {
	private Channel channel;
	private int port;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public ExecStreamServer(int port) {
		this.port = port;
		bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss", true));
		workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("worker", true));
	}

	public void start(InputStream remoteIn, OutputStream remoteOut) throws IOException {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);

		// if (NioServerSocketChannel.class.isAssignableFrom(channelType)) {
		b.option(SO_BACKLOG, 128);
		b.childOption(SO_KEEPALIVE, true);
		// }

		b.childHandler(new ChannelInitializer<Channel>() {
			@Override
			public void initChannel(Channel ch) throws Exception {

				System.out.println("Channel init");

				ch.pipeline().addLast(new ExecHanduler(remoteIn, remoteOut));

				ch.closeFuture().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						System.out.println("Channel closed");
					}
				});
			}
		});

		// Bind and start to accept incoming connections.
		ChannelFuture future = b.bind(port);
		try {
			future.await();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted waiting for bind");
		}
		if (!future.isSuccess()) {
			throw new IOException("Failed to bind", future.cause());
		}
		channel = future.channel();

		System.out.println("NettyServer listen on : " + port);
	}

	public void await() {
		try {
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (channel != null || channel.isOpen()) {
			channel.close();
			return;
		}

		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}

class ExecHanduler extends ChannelInboundHandlerAdapter {

	InputStream remoteIn;
	OutputStream remoteOut;
	StreamWriter streamWriter;

	ExecHanduler(InputStream remoteIn, OutputStream remoteOut) {
		this.remoteIn = remoteIn;
		this.remoteOut = remoteOut;
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("handlerAdded " + ctx.channel().remoteAddress());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("handlerRemoved " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("channelRead " + ctx.channel().remoteAddress());
		
		ByteBuf in = (ByteBuf) msg;
		byte[] readBody = new byte[in.readableBytes()];
		in.getBytes(0, readBody);
		System.out.println("channelRead : " + new String(readBody));
		remoteOut.write(readBody);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete " + ctx.channel().remoteAddress());
		remoteOut.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		
		System.out.println("channelActive " + ctx.channel().remoteAddress());
		streamWriter = new StreamWriter(remoteIn,ctx.channel());
		streamWriter.start();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelInactive" + ctx.channel().remoteAddress());
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelRegistered" + ctx.channel().remoteAddress());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelUnRegistered" + ctx.channel().remoteAddress());
		streamWriter.shutdown();
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
					dockerClient.execStartCmd(interactiveCmd.getId()).withStdIn(attachIn)
							.exec(new ExecStartStream(attachOut)).awaitCompletion();
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
	
	byte[] exitCmd = new byte[]{13, 10, 101, 120, 105, 116, 13, 10};

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
					byte[] payLoad = frame.getPayload();
					System.out.println("raw : " + new String(payLoad));
					out.write(frame.getPayload());
					out.flush();
					
					if(ByteUtils.equals(exitCmd, payLoad)) {
						System.out.println("client exit");
					}
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

class StreamWriter extends Thread {

	InputStream remoteIn;
	Channel ch;
	volatile boolean stop;

	public StreamWriter(InputStream remoteIn, Channel ch) {
		super();
		this.remoteIn = remoteIn;
		this.ch = ch;
		System.out.println("StreamWriter start " + ch.remoteAddress());
	}
	
	public void shutdown() {
		stop = true;
	}

	@Override
	public void run() {
		byte[] buf = new byte[2048];
		try {
			while (remoteIn.read(buf) != -1 && !stop) {
				ByteBuf meg = Unpooled.wrappedBuffer(buf);
				ch.writeAndFlush(meg);
				buf = new byte[2048];
				System.out.println("StreamWriter write " + new String(buf));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				remoteIn.close();
			} catch (IOException e) {
			}
		}
	}

}
