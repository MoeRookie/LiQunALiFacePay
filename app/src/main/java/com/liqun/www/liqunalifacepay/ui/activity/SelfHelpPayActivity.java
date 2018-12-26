package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.adapter.GoodsAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsResponseBean;

/**
 * 取消交易&输码|扫码->商品信息界面
 * 1.启用服务端监听(复用)
 * 2.取消交易->一次请求,成功后提示并点击"确认"|倒计时结束->主界面
 * 3.输码|扫码->商品信息界面
 *  多次请求,每次获取到返回结果后显示在界面上
 * 4.复用客户端?
 *  4.1.客户端请求,重点在于拼接请求串;
 *  4.2.拼接请求串时涉及到的参数包括标识符+requestBean
 * 5.复用服务端监听?
 *  5.1.监听了同一个端口返回的数据,但只能接收到成功/失败的返回结果,如何分类?
 *  5.2.若成功,则返回实例的类型不一致;
 */
public class SelfHelpPayActivity extends AppCompatActivity
implements View.OnClickListener {
    public static SelfHelpPayActivity mActivity;
    public static Thread sServerSocketThread;
    public static ServerSocket sServerSocket;
    private Button mBtnCancelDeal;
    private Button mBtnInput;
    private Button mBtnPay;
    private Message mMessage;
    private LinearLayout mLLSelfPayFirst;
    private LinearLayout mLLSelfPaySecond;
    private TextView mTvResultHint;
    private RecyclerView mRvGoods;
    private LinearLayoutManager mLayoutManager;
    private List<ScanGoodsResponseBean> mList = new ArrayList<>();
    private GoodsAdapter mAdapter;
    private final int REQUEST_CODE_INPUT_BAR_CODE_DIALOG = 0;
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
     * 显示警告类型的对话框
     * @param msg 警告内容串
     */
    private void showWarnDialog(String msg) {
        Intent intent = WarnDialogActivity.newIntent(this,msg);
        startActivity(intent);
    }

    /**
     * 处理从服务端读取过来的返回结果
     * @param obj
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof CancelDealResponseBean) { // 取消交易
            CancelDealResponseBean cdrb = (CancelDealResponseBean) obj;
            String retflag = cdrb.getRetflag();
            String retmsg = null;
            if ("0".equals(retflag)) {
                retmsg = "POS交易取消成功!";
            } else if ("1".equals(retflag)) {
                retmsg = cdrb.getRetmsg();
            }
            showWarnDialog(retmsg);
        }
        if (obj instanceof ScanGoodsResponseBean) { // 扫描商品
            ScanGoodsResponseBean sgrb = (ScanGoodsResponseBean) obj;
            String retflag = sgrb.getRetflag();
            String retmsg = sgrb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "添加成功 , 请继续扫描下一件商品！";
                if (mList.size() <= 0) {
                    // 隐藏默认界面,显示商品信息界面
                    mLLSelfPayFirst.setVisibility(View.GONE);
                    mLLSelfPaySecond.setVisibility(View.VISIBLE);
                }
                mList.add(sgrb);
                mAdapter.notifyDataSetChanged();
            }
            mTvResultHint.setText(retmsg);
        }
    }
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SelfHelpPayActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_self_help_pay);
        initUI();
        setListener();
    }


    private void initUI() {
        // 默认
        mBtnCancelDeal = findViewById(R.id.btn_cancel_deal);
        mBtnInput = findViewById(R.id.btn_input);
        // 商品信息
        mBtnPay = findViewById(R.id.btn_pay);
        // 默认界面
        mLLSelfPayFirst = findViewById(R.id.ll_self_pay_first);
        // 商品信息界面
        mLLSelfPaySecond = findViewById(R.id.ll_self_pay_second);
        mTvResultHint = findViewById(R.id.tv_result_hint);
        mRvGoods = findViewById(R.id.rv_goods);
        mLayoutManager = new LinearLayoutManager(this);
        mRvGoods.setLayoutManager(mLayoutManager);
        mAdapter = new GoodsAdapter(this,mList);
        mRvGoods.setAdapter(mAdapter);
    }

    private void setListener() {
        // 默认
        mBtnCancelDeal.setOnClickListener(this);
        mBtnInput.setOnClickListener(this);
        // 商品信息
        mBtnPay.setOnClickListener(this);
        // 等待结果的服务端监听
        initNetWorkServer();
    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_deal:
                // 取消交易
                requestNetWorkServer(
                        // 取消交易
                        ConstantValue.TAG_CANCEL_DEAL,
                        new CancelDealRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "0"
                        )
                );
                break;
            case R.id.btn_input:
                // 手输条码
                showInputBarCodeDialog();
                break;
            case R.id.btn_pay:
                // 跳转到选择支付方式界面
                Intent intent = SelectPayTypeActivity.newIntent(this);
                startActivity(intent);
                break;
        }
    }
    /**
     * 弹出条码号输入对话框
     */
    private void showInputBarCodeDialog() {
        Intent intent = InputBarCodeDialogActivity.newIntent(this);
        startActivityForResult(intent,REQUEST_CODE_INPUT_BAR_CODE_DIALOG);
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
                    mMessage.what = 1;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
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
                mMessage.what = 2;
                e.printStackTrace();
            }finally {
                mHandler.sendMessage(mMessage);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initNetWorkServer();
        if (requestCode == REQUEST_CODE_INPUT_BAR_CODE_DIALOG && resultCode == RESULT_OK) {
            if (data != null) {
                ScanGoodsResponseBean goodsBean =
                        (ScanGoodsResponseBean) data.
                                getSerializableExtra(InputBarCodeDialogActivity.EXTRA_RET_MSG);
                if (mList.size() <= 0) {
                    // 隐藏默认界面,显示商品信息界面
                    mLLSelfPayFirst.setVisibility(View.GONE);
                    mLLSelfPaySecond.setVisibility(View.VISIBLE);
                }
                mList.add(0,goodsBean);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
