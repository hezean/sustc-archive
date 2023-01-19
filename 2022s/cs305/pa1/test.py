import threading
import time
import datetime


class Test(threading.Thread):
    def hello(self):
        while True:
            print(datetime.datetime.now())
            time.sleep(1)


if __name__ == '__main__':
    # t = Test()
    #
    # print('th1')
    # th1 = threading.Thread(target=t.hello, args=())
    # # th1.daemon = True
    # th1.start()
    #
    # print('th2')
    # th2 = threading.Thread(target=t.hello, args=())
    # # th2.daemon = True
    # th2.start()

    ss = 0
    if ss:
        print(ss)