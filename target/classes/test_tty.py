import py_tty
import sys

def run():
    with py_tty.Terminal(sys.stdin, raw=True):
        print("hello")
        while True:
            #print("hello1")
            ch = sys.stdin.read(1)
            print(ch)
            
if __name__ == '__main__':
  run()