import grpc

import test_pty_pb2
import test_pty_pb2_grpc

def run():
    print("hello")
    channel = grpc.insecure_channel('localhost:39999')
    stub = test_pty_pb2_grpc.PTYServiceStub(channel)
    responseSet = stub.command(test_pty_pb2.CmdRequest(body="client py"))
    for resp in responseSet : 
        print("client received: " + resp.body)
   

if __name__ == '__main__':
  run()