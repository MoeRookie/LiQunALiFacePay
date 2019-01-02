package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alipay.xdevicemanager.api.XDeviceManager;
import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.SettingItemBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.MD5Utils;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.view.LoadingDialog;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.liqun.www.liqunalifacepay.data.bean.ALiPayBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentResponseBean;

//重要约定：在正常情况下，客户端代码需要保证这个Activity是整个App的生命周期
//重要约定：在正常情况下，客户端代码需要保证这个Activity是整个App的生命周期
//重要约定：在正常情况下，客户端代码需要保证这个Activity是整个App的生命周期
public class ScanCodePayActivity extends AppCompatActivity {
    private XDeviceManager mXDeviceManager;
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private TextView mBtnCancelPay,mTvTotalPrice;
    private final static int time = 118000;
    private MyCountDownTimer cdt;
    private static Thread sServerSocketThread;
    private static ServerSocket sServerSocket;
    private Message mMessage;
    private float mTotalPrice = 0.00f;
    private StringBuffer mSb = new StringBuffer();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 连接服务器失败
                    // 弹出警告对话框,点击确定返回到主界面
                    showWarnDialog(
                            getString(R.string.connect_server_fail)
                    );
                    break;
                case 1:
                    // 处理返回结果
                    if (msg.obj != null) {
                        handlerServerResult(msg.obj);
                    }
                    break;
                case 2:
                    // 读取服务器失败
                    showWarnDialog(
                            getString(R.string.connect_client_fail)
                    );
                    break;
                case 3: // 支付成功
                case 4: // 加签失败
                case 5: // WebService调用IO异常
                case 6: // WebServiceXML解析异常
                    if (msg.obj != null) {
                        String message = (String) msg.obj;
                        // 跳转扫码支付结果界面
                        Intent intent = ScanCodeResultActivity.newIntent(
                                ScanCodePayActivity.this,
                                mCount,
                                mTotalPrice,
                                msg.what,
                                message
                        );
                        startActivity(intent);
                    }
                    break;
            }
        }
    };
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;
    private LoadingDialog mLoadingDialog;
    private int mCount;

    /**
     * 处理服务端返回结果
     * @param obj 对象
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof CancelPaymentResponseBean) {
            CancelPaymentResponseBean cprb = (CancelPaymentResponseBean) obj;
            String retflag = cprb.getRetflag();
            String retmsg = cprb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "取消付款成功！";
            }
            showWarnDialog(retmsg);
        }
    }

    /**
     * 显示警告类型的对话框
     * @param msg 消息
     */
    private void showWarnDialog(String msg) {
        if (mWarnDialog == null) {
            mWarnDialog = new WarnDialog(this);
            mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClicked() {
                    cdt.cancel();
                    cdt.onFinish();
                }
            });
        }
        mWarnDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setText(msg);
    }

    public static Intent newIntent(Context packageContext, int count, float totalPrice) {
        Intent intent = new Intent(packageContext, ScanCodePayActivity.class);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_TOTAL_PRICE,totalPrice);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        cdt = new MyCountDownTimer(time,1000);
        cdt.start();
        mXDeviceManager = ALiFacePayApplication.getInstance().getXDeviceManager();
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        mBtnCancelPay = findViewById(R.id.btn_cancel_pay);
        mTvTotalPrice = findViewById(R.id.tv_total_price);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
            mCount = intent.getIntExtra(EXTRA_COUNT, 0);
            mTvTotalPrice.setText("￥ " + mTotalPrice);
        }
    }

    private void setListener() {
        // 开启服务端侦听
        initNetWorkServer();
        mBtnCancelPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发起取消支付的请求
                requestNetWorkServer(
                        ConstantValue.TAG_CANCEL_PAYMENT,
                        new CancelPaymentRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "0"
                        )
                );
            }
        });
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
                    mMessage.what = 0;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
    }
    // 监听键盘按下
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent){
        // 每次按下获取键值后存贮(如果不是回车,则拼接)
        if (paramKeyEvent.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            mSb.append(asciiToString(String.valueOf(paramKeyEvent.getUnicodeChar())));
        }else{
            // 获取用户支付宝付款码码值
            String barCode = mSb.toString().trim();
            // 请求支付
            requestALiPay(barCode);
            // 清空stringBuffer
            mSb.delete(0,mSb.length());
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    /**
     * 请求支付宝支付
     * @param barCode 二维码码值
     */
    private void requestALiPay(final String barCode) {
        // 弹出加载对话框
        showLoadingDialog();
        /**
         * 先做支付宝付款请求
         *  成功:跳转支付结果页(显示支付成功界面)
         *      请求支付方式
         *          成功:打印小票
         *          失败:提示信息(界面提示)
         *  失败:跳转支付结果页(显示支付失败界面)
         *      提示信息(界面提示)
         */
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 请求时间
                String requestTime = getRequestTime(System.currentTimeMillis());
                // 重新格式化
                String orderTimeMsg = getOrderTime(requestTime);
                String[] orderTimeArr = orderTimeMsg.split(" ");
                String orderDate = orderTimeArr[0];
                String orderTime = orderTimeArr[1];
                // 获取设置信息
                String settingMsg = SpUtils.getString(
                        ScanCodePayActivity.this,
                        ConstantValue.KEY_SETTING_CONTENT,
                        ""
                );
                List<SettingItemBean> settingList
                            = JSONArray.parseArray(settingMsg, SettingItemBean.class);
                // 门店编码
                String merchantNo = settingList.get(3).getContent();
                // 款台号
                String catwalkNo = settingList.get(5).getContent();
                // 加签
                int result[] = new int[1];
                String signedBarCode = mXDeviceManager.sign(barCode.getBytes(), result);
                // 请求
                SoapObject request = null;
                if (result[0] != 0) {
                    // 加签失败
                    mMessage = Message.obtain();
                    mMessage.what = 4;
                    mMessage.obj = "加签失败:"+result[0];
                    mHandler.sendMessage(mMessage);
                } else{
                    // 2.请求支付
                    request = new SoapObject(
                            ConstantValue.NAME_SPACE,
                            ConstantValue.METHOD_ROOT);
                    // 设置需调用WebService接口传入的参数
                    // key -> 业态
                    request.addProperty(
                            ConstantValue.REQUEST_PARAMS_KEY,
                            ConstantValue.KEY_VALUE_LQBH);
                    // method -> 方法名
                    request.addProperty(
                            ConstantValue.REQUEST_PARAMS_METHOD_NAME,
                            ConstantValue.METHOD_TRADE_PAY);
                    // json
                    ALiPayRequestBean requestBean = new ALiPayRequestBean(
                            // 支付订单号(业态编号[lqbh]+款台号+流水号+日期+时间(订单规则))
                            ConstantValue.KEY_VALUE_LQBH
                                    + catwalkNo
                                    + ALiFacePayApplication.getInstance().getFlowNo()
                                    + orderDate
                                    + orderTime,
                            // 顾客手机条码
                            barCode,
                            // 门店编码
                            merchantNo,
                            // 款台号
                            catwalkNo,
                            // 收款员号(写死即可)
                            "90001",
                            // 金额 单位为元,精确到小数点后两位
                            new BigDecimal(
                                    String.valueOf("0.01")
                            ).setScale(2, BigDecimal.ROUND_HALF_UP),
                            new BigDecimal(
                                    String.valueOf("0.00")
                            ).setScale(2, BigDecimal.ROUND_HALF_UP),
                            new BigDecimal(
                                    String.valueOf("0.00")
                            ).setScale(2, BigDecimal.ROUND_HALF_UP),
                            // 机具信息
                            signedBarCode
                    );
                    request.addProperty(
                            ConstantValue.REQUEST_PARAMS_JSON,
                            JSON.toJSONString(requestBean)
                    );
                    // resquesttime -> 请求时间(格式为:yyyy-mm-dd hh:mm:ss)
                    request.addProperty(
                            ConstantValue.REQUEST_PARAMS_REQUEST_TIME,
                            requestTime
                    );
                    // sign -> 数字签名(MD5)
                    request.addProperty(
                            ConstantValue.REQUEST_PARAMS_SIGN,
                            MD5Utils.md5(
                                    ConstantValue.KEY_VALUE_LQBH
                                            + requestTime
                            )
                    );
                }
                // 调用
                try {
                    // 创建SoapSerializationEnvelope 对象,同时指定soap版本号(之前在wsdl中看到的)
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                    // 由于是发送请求,所以是设置bodyOut
                    envelope.bodyOut = request;
                    // 由于是.net开发的webservice,所以这里要设置为true
                    envelope.dotNet = true;
                    HttpTransportSE httpTransportSE = new HttpTransportSE(ConstantValue.REQUEST_URI);
                    httpTransportSE.call(null, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的数据
                    String bodyIn = object.getProperty(0).toString();
                    mMessage = Message.obtain();
                    mMessage.what = 3;
                    mMessage.obj = bodyIn;
                } catch (IOException e) {
                    // WebService调用IO异常
                    mMessage = Message.obtain();
                    mMessage.what = 5;
                    mMessage.obj = "WebService调用IO异常";
                } catch (XmlPullParserException e) {
                    // WebServiceXML解析异常
                    mMessage = Message.obtain();
                    mMessage.what = 6;
                    mMessage.obj = "WebServiceXML解析异常";
                }finally {
                    mHandler.sendMessage(mMessage);
                }
            }
        }.start();
    }

    /**
     * 弹出加载类型的对话框
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this,R.style.LoadingDialogStyle);
        }
        mLoadingDialog.show();
        mLoadingDialog.setMessage("支付中 . . .");
    }

    /**
     * 将请求时间转换为订单时间
     * @param requestTime 请求时间
     * @return 订单时间
     */
    private String getOrderTime(String requestTime) {
        // 字符串->日期
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        try {
            Date date = oldFormat.parse(requestTime);
            // 格式化日期 ->
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyymmdd hhmmss");
            // 获取日期字符串和时间字符串
            return newFormat.format(date);
        } catch (ParseException e) {
            L.e("======================字符串转日期异常=====================");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取发送支付请求的时间
     * @param currentTime 当前时间的时间戳
     * @return 发送支付请求的时间
     */
    private String getRequestTime(long currentTime) {
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return sdf.format(date); // 发送请求的时间;
    }
    /**
     * ascii码转换为字符串
     * @param paramString ascii码值
     * @return 对应的字符串
     */
    private String asciiToString(String paramString) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append((char) Integer.parseInt(paramString));
        return localStringBuffer.toString();

    }

    /**
     * 启用服务端侦听
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
    class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtnCancelPay.setText(getString(R.string.cancel_pay) + "(" + millisUntilFinished/1000 + "s)");
        }

        @Override
        public void onFinish() {
            // 关闭服务端监听
            closeServer();
            finish();
        }
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
}
