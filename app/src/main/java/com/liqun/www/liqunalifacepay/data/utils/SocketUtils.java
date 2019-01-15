package com.liqun.www.liqunalifacepay.data.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {
    /**
     * 关闭服务端侦听
     */
    public static void closeServer(ServerSocket serverSocket,Thread serverSocketThread){
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                L.e("=======================哎呀,关闭服务端侦听失败啦=======================");
            }
        }
        if (serverSocketThread != null) {
            serverSocketThread.interrupt();
        }
    }
}
