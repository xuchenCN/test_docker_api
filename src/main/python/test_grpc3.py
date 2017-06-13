import grpc

import test_pty_pb2
import test_pty_pb2_grpc
import sys
import py_tty
import threading

channel = grpc.insecure_channel('localhost:39999')
stub = test_pty_pb2_grpc.PTYServiceStub(channel)
print("connect")

def run():
    
    t1 = threading.Thread(target=consumeOut)
    t1.start()
    print("send cmd1")
    with py_tty.Terminal(sys.stdin, raw=True):
        while True:
            ch = sys.stdin.read(1)
            print("send cmd")
            stub.command(test_pty_pb2.CmdRequest(body=ch))
            #consumeOut(responseSet)

def consumeOut():
    print("threading start")
    oneReader = stub.command(test_pty_pb2.CmdRequest(body="\n"))
    for resp in oneReader : 
        #print("client received: " + resp.body)
        sys.stdout.write(resp.body)
    print("threading stop")

if __name__ == '__main__':
  run()
  
