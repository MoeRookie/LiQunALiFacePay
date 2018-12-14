package com.liqun.www.liqunalifacepay;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Demo1Server {
    public static void main(String[] args) {
        //建立Tcp的服务端,并且监听一个端口。
        try {
            ServerSocket serverSocket = new ServerSocket(2001);
            //接受客户端的连接
            Socket socket  =  serverSocket.accept(); //accept()  接受客户端的连接 该方法也是一个阻塞型的方法，没有客户端与其连接时，会一直等待下去。
            //获取输入流对象，读取客户端发送的内容。
            InputStream inputStream = socket.getInputStream();
            byte[] buf = new byte[1024];
            int length = 0;
            length = inputStream.read(buf);
            System.out.println("内容已接收："+ new String(buf,0,length));
            //关闭资源
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
