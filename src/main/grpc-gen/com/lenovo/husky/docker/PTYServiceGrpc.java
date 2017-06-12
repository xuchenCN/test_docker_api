package com.lenovo.husky.docker;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 0.14.1)",
    comments = "Source: test_pty.proto")
public class PTYServiceGrpc {

  private PTYServiceGrpc() {}

  public static final String SERVICE_NAME = "PTYService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.lenovo.husky.docker.TestPTYProtocol.CmdRequest,
      com.lenovo.husky.docker.TestPTYProtocol.CmdResponse> METHOD_COMMAND =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "PTYService", "command"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.lenovo.husky.docker.TestPTYProtocol.CmdRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.lenovo.husky.docker.TestPTYProtocol.CmdResponse.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PTYServiceStub newStub(io.grpc.Channel channel) {
    return new PTYServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PTYServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PTYServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static PTYServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PTYServiceFutureStub(channel);
  }

  /**
   */
  public static interface PTYService {

    /**
     */
    public void command(com.lenovo.husky.docker.TestPTYProtocol.CmdRequest request,
        io.grpc.stub.StreamObserver<com.lenovo.husky.docker.TestPTYProtocol.CmdResponse> responseObserver);
  }

  @io.grpc.ExperimentalApi
  public static abstract class AbstractPTYService implements PTYService, io.grpc.BindableService {

    @java.lang.Override
    public void command(com.lenovo.husky.docker.TestPTYProtocol.CmdRequest request,
        io.grpc.stub.StreamObserver<com.lenovo.husky.docker.TestPTYProtocol.CmdResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_COMMAND, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return PTYServiceGrpc.bindService(this);
    }
  }

  /**
   */
  public static interface PTYServiceBlockingClient {

    /**
     */
    public java.util.Iterator<com.lenovo.husky.docker.TestPTYProtocol.CmdResponse> command(
        com.lenovo.husky.docker.TestPTYProtocol.CmdRequest request);
  }

  /**
   */
  public static interface PTYServiceFutureClient {
  }

  public static class PTYServiceStub extends io.grpc.stub.AbstractStub<PTYServiceStub>
      implements PTYService {
    private PTYServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PTYServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PTYServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PTYServiceStub(channel, callOptions);
    }

    @java.lang.Override
    public void command(com.lenovo.husky.docker.TestPTYProtocol.CmdRequest request,
        io.grpc.stub.StreamObserver<com.lenovo.husky.docker.TestPTYProtocol.CmdResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_COMMAND, getCallOptions()), request, responseObserver);
    }
  }

  public static class PTYServiceBlockingStub extends io.grpc.stub.AbstractStub<PTYServiceBlockingStub>
      implements PTYServiceBlockingClient {
    private PTYServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PTYServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PTYServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PTYServiceBlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public java.util.Iterator<com.lenovo.husky.docker.TestPTYProtocol.CmdResponse> command(
        com.lenovo.husky.docker.TestPTYProtocol.CmdRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_COMMAND, getCallOptions(), request);
    }
  }

  public static class PTYServiceFutureStub extends io.grpc.stub.AbstractStub<PTYServiceFutureStub>
      implements PTYServiceFutureClient {
    private PTYServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PTYServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PTYServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PTYServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_COMMAND = 0;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PTYService serviceImpl;
    private final int methodId;

    public MethodHandlers(PTYService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_COMMAND:
          serviceImpl.command((com.lenovo.husky.docker.TestPTYProtocol.CmdRequest) request,
              (io.grpc.stub.StreamObserver<com.lenovo.husky.docker.TestPTYProtocol.CmdResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final PTYService serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME)
        .addMethod(
          METHOD_COMMAND,
          asyncServerStreamingCall(
            new MethodHandlers<
              com.lenovo.husky.docker.TestPTYProtocol.CmdRequest,
              com.lenovo.husky.docker.TestPTYProtocol.CmdResponse>(
                serviceImpl, METHODID_COMMAND)))
        .build();
  }
}
