package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SelfHelpPayActivity extends AppCompatActivity
implements View.OnClickListener {

    private Button mBtnCancelDeal;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SelfHelpPayActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_help_pay);
        initUI();
        setListener();
    }

    private void initUI() {
        mBtnCancelDeal = findViewById(R.id.btn_cancel_deal);
    }

    private void setListener() {
        mBtnCancelDeal.setOnClickListener(this);
        // 等待取消交易结果的服务端监听
        initNetWorkServer();
    }

    /**
     * 开启本地服务端,以监听此时作为客户端的服务器返回数据
     */
    private void initNetWorkServer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    // 建立Tcp的服务端,并且监听一个端口
                    ServerSocket serverSocket = new ServerSocket(
                            ConstantValue.PORT_SERVER_RETURN);
                    // 接受客户端的连接
                    Socket socket = serverSocket.accept(); // 接受客户端的连接(该方法是一个阻塞型的方法,当没有客户端与其连接时会一直等待下去)
                    // 获取输入流对象,读取客户端发送的内容
                    InputStream inputStream = socket.getInputStream();
                    byte[] buf = new byte[1024];
                    int length = 0;
                    length = inputStream.read(buf);
                    L.i("服务端返回 = " + new String(buf, 0, length));
                    /**
                     * 为了避免出现msg被重用的问题,每次的msg对象都要通过Message.obtain()方法获取
                     */
                    mMessage = Message.obtain();
                    mMessage.what = 2;
                    mMessage.obj = JointDismantleUtils.dismantleResponse(
                            new String(buf, 0, length)
                    );
                    //关闭资源
                    serverSocket.close();
                } catch (IOException e) {
                    mMessage = Message.obtain();
                    mMessage.what = 0;
                    e.printStackTrace();
                } finally {
                    mHandler.sendMessage(mMessage);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_deal:
                // 取消交易
                break;
        }
    }
}
