from socket import *
import threading
import py_tty
import sys

exit_code = bytearray([13, 10, 101, 120, 105, 116, 13, 10])

def bytesCmp(obj1 , obj2):
    print(len(obj1))
    print(len(obj2))
    if(len(obj1) != len(obj2)):
        return False
    for index in range(0 , len(obj1)) :
        if(obj1[index] != obj2[index]) :
            return False
    
    return True
   

exitCmd = False

class TcpClient(threading.Thread):
    BUFSIZ = 1024
    
    def __init__(self,client,terminalHandler):
        threading.Thread.__init__(self)
        self.client = client
        self.terminalHandler = terminalHandler
    
    def run(self):
        while True:
            data = self.client.recv(self.BUFSIZ)
            if not data:
                exitCmd = True
                self.terminalHandler.shutdown()
                #self.interrupt_main()
                sys.stdout.write(exit_code)
                sys.stdout.flush()
                sys.exit()
                break
            
            sys.stdout.write(data)
            sys.stdout.flush()
       

class TerminalHandler(threading.Thread):
    
    def __init__(self,client):
        threading.Thread.__init__(self)
        self.client = client
        self.shouldRun = True
    def run(self):
         with py_tty.Terminal(sys.stdin, raw=True):
            while self.shouldRun:
                ch = sys.stdin.read(1)
                self.client.send(ch)
    
    def shutdown(self):
        self.shouldRun = False
    
def main():
    HOST = '127.0.0.1'
    PORT = 39999
    ADDR = (HOST, PORT)
    
    client = socket(AF_INET, SOCK_STREAM)
    client.connect(ADDR)
    th = TerminalHandler(client)
    TcpClient(client,th).start()
    th.start()
    
if __name__ == '__main__':
     main()
