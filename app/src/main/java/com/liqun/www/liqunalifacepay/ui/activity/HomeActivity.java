package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.DealRecordBean;
import com.liqun.www.liqunalifacepay.data.utils.CommonUtils;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liqun.www.liqunalifacepay.data.bean.DealRecordBean.*;

/**
 * 1.HomeActivity中请求并获取
 *  1.1.在onResume方法中启动服务端侦听
 *      注*:
 *          为防止SelfHelpPayActivity界面的操作数据被HomeActivity中服务端所拦截
 *          须在onPause方法中关闭服务端侦听
 *  1.2.点击非|会员按钮时发起获取本次交易流水号的请求
 *  1.3.请求成功后跳转SelfHelpPayActivity界面
 */
public class HomeActivity extends AppCompatActivity
implements View.OnClickListener {
    private Display mDefaultDisplay;
    private ImageView mIvHead;
    private Button mBtnVip,mBtnNoVip;
    private long[] mHits = new long[5]; // 设置多击时需要的点击次数
    private ImageView mIvLiQun;
    private ImageView mIvRt;
    public static Thread sServerSocketThread;
    public static ServerSocket sServerSocket;
    private Message mMessage;
    private WarnDialog mDialog;
    private TextView mTvMsg;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 读取服务器失败
                    showWarnDialog(
                            getString(R.string.connect_client_fail)
                    );
                break;
                case 1:
                    // 处理返回结果
                    if (msg.obj != null) {
                        handlerServerResult(msg.obj);
                    }
                    break;
                case 2:
                    // 连接服务器失败
                    // 弹出警告对话框,点击确定返回到主界面
                    showWarnDialog(
                            getString(R.string.connect_server_fail)
                    );
                    break;
            }
        }
    };

    /**
     * 处理服务端返回的结果
     * @param obj 对象
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof DealRecordResponseBean) {
            DealRecordResponseBean drrb = (DealRecordResponseBean) obj;
            String retflag = drrb.getRetflag();
            String retmsg = drrb.getRetmsg();
            if ("1".equals(retflag)) {
                showWarnDialog(retmsg);
            } else if ("0".equals(retflag)) {
                // 保存流水号
                ALiFacePayApplication.getInstance().setFlowNo(drrb.getFlow_no());
                // 跳转SelfHelpPayActivity界面
                Intent intent = SelfHelpPayActivity.newIntent(HomeActivity.this);
                startActivity(intent);
            }
        }
    }

    /**
     * 显示警告类型的对话框
     * @param msg
     */
    private void showWarnDialog(String msg) {
        if (mDialog == null) {
            mDialog = new WarnDialog(HomeActivity.this);
            mDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClicked() {
                    mDialog.dismiss();
                }
            });
        }
        mDialog.show();
        if (mTvMsg == null) {
            mTvMsg = mDialog.findViewById(R.id.tv_message);
        }
        mTvMsg.setText(msg);
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        setListener();
    }

    private void setListener() {
        mIvLiQun.setOnClickListener(this);
        mIvRt.setOnClickListener(this);
        mBtnNoVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNetWorkServer(
                        ConstantValue.TAG_DEAL_RECORD,
                        new DealRecordRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "90001",
                                "0"
                        )
                );
            }
        });
        mBtnVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.e("======================先什么都不做========================");
            }
        });
    }


    private void initUI() {
        mDefaultDisplay = getWindowManager().getDefaultDisplay();
        // 适配home界面的head图片
        mIvHead = findViewById(R.id.iv_head);
        setImageDisplay(mIvHead,898,1920);
        mIvLiQun = findViewById(R.id.iv_liqun);
        mIvRt = findViewById(R.id.iv_rt);
        // 会员结账&非会员结账
        mBtnVip = findViewById(R.id.btn_vip);
        mBtnNoVip = findViewById(R.id.btn_novip);
        /**
         * 1.编写"自助收银"界面布局
         * 2."取消交易"
         * 3."手输条码"
         * 4."扫描商品码"
         * 5.编写商品信息界面布局
         * 6.添加购物袋
         * 7."选择支付方式"界面布局
         * 8.编写"扫码支付"界面布局
         * 9.编写"刷脸付"界面布局
         */
    }

    /**
     * 根据图片宽高比适配其显示
     * @param iv 显示图片的view
     * @param width 图片的宽
     * @param height 图片的高
     */
    private void setImageDisplay(ImageView iv, int width, int height) {
        LayoutParams layoutParams = (LayoutParams) mIvHead.getLayoutParams();
        layoutParams.width = mDefaultDisplay.getWidth();
        layoutParams.height = layoutParams.width * width / height;
        iv.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 启动服务端侦听
        initNetWorkServer();
    }

    /**
     * 开启本地服务端,以监听此时作为客户端的服务器返回数据
     */
    private void initNetWorkServer() {
        sServerSocketThread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sServerSocket = new ServerSocket(2001);
                    while (true) {
                        Socket socket = sServerSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                        SocketServerThread socketThread = new SocketServerThread(socket);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        sServerSocketThread.start();
    }

    /**
     * 关闭服务端侦听
     */
    public static void closeServer(){
        if (sServerSocket != null) {
            try {
                sServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                L.e("=======================哎呀,关闭服务端侦听失败啦=======================");
            }
        }
        if (sServerSocketThread != null) {
            sServerSocketThread.interrupt();
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent = null;
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[mHits.length - 1] - mHits[0] < 1000) {
            switch (v.getId()) {
                case R.id.iv_liqun:
                    // 满足"五击事件"后,跳转"日结"界面
                    intent = DayEndActivity.newIntent(HomeActivity.this);
                    break;
                case R.id.iv_rt:
                    // 满足"五击事件"后,跳转"设置"界面
                    intent = SettingActivity.newIntent(HomeActivity.this);
                    break;
            }
            startActivity(intent);
        }
    }
    /**
     * 连接服务端,请求数据
     */
    private void requestNetWorkServer(final String tag, final Object requestBean) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 拼接请求串
                String msg = JointDismantleUtils.jointRequest(
                        tag,
                        requestBean
                );
                L.e(msg);
                //建立tcp的服务
                try {
                    Socket socket = new Socket(
                            ConstantValue.IP_SERVER_ADDRESS,
                            ConstantValue.PORT_SERVER_RECEIVE);
                    //获取到Socket的输出流对象
                    OutputStream outputStream = socket.getOutputStream();
                    // 将输出流包装成打印流
                    PrintWriter printWriter=new PrintWriter(outputStream);
                    printWriter.print(msg);
                    printWriter.flush();
                    socket.close();
                } catch (IOException e) {
                    mMessage = Message.obtain();
                    mMessage.what = 2;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 关闭服务端侦听
        closeServer();
    }

    /**
     * Socket多线程处理类 用来处理服务端接收到的客户端请求(处理Socket对象)
     */
    class SocketServerThread extends Thread {
        private Socket socket;
        public SocketServerThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            super.run();
            // 根据输入输出流和客户端连接
            try {
                // 得到一个输入流，接收客户端传递的信息
                InputStream inputStream = socket.getInputStream();
                // 提高效率，将自己字节流转为字符流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // 加入缓冲区
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String temp = null;
                String info = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    info += temp;
                }
                L.e(info);
                /**
                 * 为了避免出现msg被重用的问题,每次的msg对象都要通过Message.obtain()方法获取
                 */
                mMessage = Message.obtain();
                mMessage.what = 1;
                mMessage.obj = JointDismantleUtils.dismantleResponse(info);
                //关闭资源
                socket.close();
            } catch (IOException e) {
                mMessage = Message.obtain();
                mMessage.what = 0;
                e.printStackTrace();
            }finally {
                mHandler.sendMessage(mMessage);
            }
        }
    }
}
