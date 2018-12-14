package com.liqun.www.liqunalifacepay;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Demo1Client {
    public static void main(String[] args) {
        String msg = "POSCERTIFY${\"ip\":\"128.192.80.252\",\"operators\":\"90001\",\"flag\":\"0\"}";
        //建立tcp的服务
        try {
            Socket socket = new Socket("128.192.80.113",3012);
            //获取到Socket的输出流对象
            OutputStream outputStream = socket.getOutputStream();
            //利用输出流对象把数据写出即可。
            outputStream.write(msg.getBytes("utf-8"));
            System.out.println("内容已写出!");
            //关闭资源
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
