package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
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
import com.liqun.www.liqunalifacepay.data.utils.MD5;
import com.liqun.www.liqunalifacepay.data.utils.MD5Utils;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import org.ksoap2.SoapFault;
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
            }
        }
    };
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;

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

    public static Intent newIntent(Context packageContext, float totalPrice) {
        Intent intent = new Intent(packageContext, ScanCodePayActivity.class);
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
            mTvTotalPrice.setText("￥ " + mTotalPrice);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开启服务端侦听
        initNetWorkServer();
    }

    private void setListener() {
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
            String barCode = mSb.toString().trim();
            requestALiPay(barCode);
            mSb.delete(0,mSb.length());
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    /**
     * 请求支付宝支付
     * @param barCode 二维码码值
     */
    private void requestALiPay(final String barCode) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 请求时间
                // 2.请求支付
                // 2.1.拼接请求参数
                // 2.1.1.key -> 业态
                // 2.1.2.method -> 方法名
                // 2.1.3.json -> 看参数说明
                // 2.1.4.resquesttime -> 请求时间(格式为:yyyy-mm-dd hh:mm:ss)
                // 2.1.5.sign -> 数字签名(MD5)
                // 2.2.发起支付请求
                /**
                 * 参数说明:
                 *  out_trade_no:支付订单号(lqbl+订单规则)[String((必填))]
                 *      订单规则说明:款台号+流水号+日期+时间
                 *  auth_code:顾客手机条码[String(必填)]
                 *  store_id:商户门店号[String(必填)]
                 *  terminal_id:款台号[String(必填)]
                 *  operator_id:收款员工号[String(必填)]
                 *  total_amount:金额 单位为元,精确到小数点后两位[bigdecimal(必填)]
                 *  terminal_params:机具信息[String(必填)]
                 */
                // 请求时间
                String requestTime = getRequestTime(System.currentTimeMillis());
                L.e("请求时间:" + requestTime);
                // 重新格式化
                String order = getOrderTime(requestTime);
                L.e("重新格式化:" + order);
                String[] orderTimeArr = order.split(" ");
                // 格式化后的日期
                String orderDate = orderTimeArr[0];
                L.e("格式化后的日期:" + orderDate);
                // 格式化后的时间
                String orderTime = orderTimeArr[1];
                L.e("格式化后的时间:" + orderTime);
                // 设置信息
                String settingMsg = SpUtils.getString(
                        ScanCodePayActivity.this,
                        ConstantValue.KEY_SETTING_CONTENT,
                        ""
                );
                List<SettingItemBean> settingList
                            = JSONArray.parseArray(settingMsg, SettingItemBean.class);
                // 门店编码
                String merchantNo = settingList.get(3).getContent();
                L.e("门店编码:" + merchantNo);
                // 款台号
                String catwalkNo = settingList.get(5).getContent();
                L.e("款台号:" + catwalkNo);
                // 加签
                L.e("barCode = " + barCode);
                int result[] = new int[1];
                String signedBarCode = mXDeviceManager.sign(barCode.getBytes(), result);
                L.e("加签成功:" + signedBarCode);
                // 请求
                SoapObject request = null;
                if (result[0] != 0) {
                    // 加签失败
                    L.e("加签失败:"+result[0]);
//                    mMessage = Message.obtain();
//                    mMessage.what = 3;
//                    mHandler.sendMessage(mMessage);
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
                            // 支付订单号(款台号+流水号+日期+时间)
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
                            // 收款员号
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
                    L.e("requestBean = " + requestBean.toString());
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
                    L.e("数字签名:" + ConstantValue.KEY_VALUE_LQBH + requestTime);
                    request.addProperty(
                            ConstantValue.REQUEST_PARAMS_SIGN,
                            MD5Utils.md5(
                                    ConstantValue.KEY_VALUE_LQBH
                                            + requestTime
                            )
                    );

                }
                // 创建SoapSerializationEnvelope 对象,同时指定soap版本号(之前在wsdl中看到的)
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                // 由于是发送请求,所以是设置bodyOut
                envelope.bodyOut = request;
                // 由于是.net开发的webservice,所以这里要设置为true
                envelope.dotNet = true;
                HttpTransportSE httpTransportSE = new HttpTransportSE(ConstantValue.REQUEST_URI);
                // 调用
                try {
                    L.e("===========================请求中===============================");
                    httpTransportSE.call(null, envelope);
                } catch (IOException e) {
                    // WebService调用IO异常
                    L.e("WebService调用IO异常");
//                    mMessage = Message.obtain();
//                    mMessage.what = 4;
//                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    // WebServiceXML解析异常
                    L.e("WebServiceXML解析异常");
//                    mMessage = Message.obtain();
//                    mMessage.what = 5;
//                    e.printStackTrace();
                }finally{
//                    mHandler.sendMessage(mMessage);
                }
                // 获取返回的数据
                Object bodyIn = envelope.bodyIn;
                // 获取返回的结果
                L.e("获取返回的结果:" + bodyIn.toString());
            }
        }.start();
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
