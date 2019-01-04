package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alipay.zoloz.smile2pay.service.Zoloz;
import com.alipay.zoloz.smile2pay.service.ZolozCallback;
import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.SettingItemBean;
import com.liqun.www.liqunalifacepay.data.utils.CommonUtils;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.*;

public class SelectPayTypeActivity extends AppCompatActivity
implements View.OnClickListener {
    private static final String EXTRA_RETMSG = "com.liqun.www.liqunalifacepay.retmsg";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;
    private static Thread sServerSocketThread;
    private static ServerSocket sServerSocket;
    private Message mMessage;
    //刷脸支付相关
    public static final String KEY_INIT_RESP_NAME = "zim.init.resp";
    private Zoloz zoloz;

    // 值为"1000"调用成功
    // 值为"1003"用户选择退出
    // 值为"1004"超时
    // 值为"1005"用户选用其他支付方式
    static final String CODE_SUCCESS = "1000";
    static final String CODE_EXIT = "1003";
    static final String CODE_TIMEOUT = "1004";
    static final String CODE_OTHER_PAY = "1005";

    static final String TXT_EXIT = "已退出刷脸支付";
    static final String TXT_TIMEOUT = "操作超时";
    static final String TXT_OTHER_PAY = "已退出刷脸支付";
    static final String TXT_OTHER = "抱歉未支付成功，请重新支付";

    //刷脸支付相关
    static final String SMILEPAY_CODE_SUCCESS = "10000";
    static final String SMILEPAY_SUBCODE_LIMIT = "ACQ.PRODUCT_AMOUNT_LIMIT_ERROR";
    static final String SMILEPAY_SUBCODE_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BALANCE_NOT_ENOUGH";
    static final String SMILEPAY_SUBCODE_BANKCARD_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BANKCARD_BALANCE_NOT_ENOUGH";

    static final String SMILEPAY_TXT_LIMIT = "刷脸支付超出限额，请选用其他支付方式";
    static final String SMILEPAY_TXT_EBALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
    static final String SMILEPAY_TXT_BANKCARD_BALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
    static final String SMILEPAY_TXT_FAIL = "抱歉未支付成功，请重新支付";
    static final String SMILEPAY_TXT_SUCCESS = "刷脸支付成功";
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
    private float mTotalPrice = 0.00f;
    private int mCount;

    /**
     * 处理请求结果
     * @param obj
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof CancelPaymentResponseBean) {
            CancelPaymentResponseBean cprb = (CancelPaymentResponseBean) obj;
            String retflag = cprb.getRetflag();
            String retmsg = cprb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "取消付款成功！";
            }
            // 成功后关闭当前服务端侦听&当前界面
            closeServer();
            showWarnDialog(retmsg);
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

    private Button mBtnClose;
    private ImageButton mIbScanCode,mIbSmile;

    public static Intent newIntent(Context packageContext, String retmsg, int count, float totalPrice) {
        Intent intent = new Intent(packageContext, SelectPayTypeActivity.class);
        intent.putExtra(EXTRA_RETMSG, retmsg);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_TOTAL_PRICE,totalPrice);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pay_type);
        zoloz = Zoloz.getInstance(getApplicationContext());
        initError();
        initUI();
        setListener();
    }

    private void initUI() {
        mBtnClose = findViewById(R.id.btn_close);
        mIbScanCode = findViewById(R.id.ib_scan_code);
        mIbSmile = findViewById(R.id.ib_smile);
    }

    private void setListener() {
        mBtnClose.setOnClickListener(this);
        mIbScanCode.setOnClickListener(this);
        mIbSmile.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                // 取消付款(成功后关闭当前服务端侦听&当前界面)
                requestNetWorkServer(
                        ConstantValue.TAG_CANCEL_PAYMENT,
                        new CancelPaymentRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "0"
                        )
                );
                break;
            case R.id.ib_scan_code: // 扫码支付
                closeServer();
                Intent intent = ScanCodePayActivity.newIntent(this,mCount,mTotalPrice);
                startActivity(intent);
                finish();
                break;
            case R.id.ib_smile: // 刷脸付
                smilePay();
                break;
        }
    }
    /**
     * 发起刷脸支付请求，先zolozGetMetaInfo获取本地app信息，然后调用服务端获取刷脸付协议.
     */
    private void smilePay() {
        zoloz.zolozGetMetaInfo(mockInfo(), new ZolozCallback() {
            @Override
            public void response(Map smileToPayResponse) {
                if (smileToPayResponse == null) {
                    L.e("zoloz.zolozGetMetaInfo response is null");
                    return;
                }
                String code = (String)smileToPayResponse.get("code");
                final String metaInfo = (String)smileToPayResponse.get("metainfo");
                //获取metainfo成功
                if (CODE_SUCCESS.equalsIgnoreCase(code) && metaInfo != null) {
                    L.i("获取metainfo成功->metanfo is:" + metaInfo);
                    // 将metaInfo发送给商户服务端，由商户服务端发起刷脸初始化OpenAPI的调用
                    // 人脸初始化
                    requestFacePay(
                            ConstantValue.METHOD_ZOLOZ_INIT,
                            metaInfo);
//                    alipayClient.execute(request,
//                            new AlipayCallBack() {
//                                @Override
//                                public AlipayResponse onResponse(AlipayResponse response) {
//                                    if (response != null && SMILEPAY_CODE_SUCCESS.equals(response.getCode())) {
//                                        try {
//                                            ZolozAuthenticationCustomerSmilepayInitializeResponse zolozResponse
//                                                    = (ZolozAuthenticationCustomerSmilepayInitializeResponse)response;
//
//                                            String result = zolozResponse.getResult();
//                                            JSONObject resultJson = JSON.parseObject(result);
//                                            String zimId = resultJson.getString("zimId");
//                                            String zimInitClientData = resultJson.getString("zimInitClientData");
//                                            //人脸调用
//                                            smile(zimId, zimInitClientData);
//                                        } catch (Exception e) {
//                                            promptText(TXT_OTHER);
//                                        }
//                                    } else {
//                                        promptText(TXT_OTHER);
//                                    }
//                                    return null;
//                                }
//                            });
                } else {
                    // 考虑使用dialog进行友好提示
                    L.e(SMILEPAY_TXT_FAIL);
                }
            }
        });
    }

    /**
     * 请求刷脸付
     * @param methodName 方法名
     * @param requestData 请求数据
     */
    private void requestFacePay(final String methodName, final String requestData) {
        //起一个异步线程发起网络请求
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 发起支付宝人脸初始化请求
                SoapObject request = new SoapObject(
                        ConstantValue.NAME_SPACE,
                        methodName
                        );
                // 设置需调用WebService接口传入的参数
                request.addProperty(
                        ConstantValue.REQUEST_KEY,
                        requestData);
                // 创建SoapSerializationEnvelope 对象,同时指定soap版本号(之前在wsdl中看到的)
                SoapSerializationEnvelope envelope
                        = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
                // 由于是发送请求,所以是设置bodyOut
                envelope.bodyOut = request;
                // 由于是.net开发的webservice,所以这里要设置为true
                envelope.dotNet = true;
                HttpTransportSE httpTransportSE
                        = new HttpTransportSE(ConstantValue.REQUEST_URI);
                try {
                    httpTransportSE.call(
                            null, envelope);
                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String bodyIn = object.getProperty(0).toString();
                    L.e("bodyIn = " + bodyIn);
                } catch (IOException e) {
                    L.e("==========WebService调用IO异常==========");
                } catch (XmlPullParserException e) {
                    L.e("==========WebService调用Xml异常==========");
                }
            }
        }.start();
    }

    /**
     * mock数据，真实商户请填写真实信息.
     */
    private Map mockInfo() {
        // 获取设置信息
        String settingMsg = SpUtils.getString(
                SelectPayTypeActivity.this,
                ConstantValue.KEY_SETTING_CONTENT,
                ""
        );
        // 转换为集合对象
        List<SettingItemBean> settingList
                = JSON.parseArray(settingMsg, SettingItemBean.class);
        // 门店编码
        String shopNo = settingList.get(3).getContent();
        // 门店商户号
        String merchantNo = settingList.get(4).getContent();
        // 款台号
        String catwalkNo = settingList.get(5).getContent();
        Map merchantInfo = new HashMap();
        //以下信息请根据真实情况填写
        //商户 Pid
        merchantInfo.put("merchantId", "2088521308744741");
        //ISV PID
        merchantInfo.put("partnerId", "2088031960490332");
        //添加刷脸付功能的appid
        merchantInfo.put("appId", "2018041960033206");
        //机具编号，便于关联商家管理的机具
        merchantInfo.put("deviceNum", catwalkNo);
        //商户的门店编号
        merchantInfo.put("storeCode", shopNo);
        //口碑店铺号
        merchantInfo.put("alipayStoreCode", "lqjt");
        return merchantInfo;
    }
    /**
     * 发起刷脸支付请求.
     * @param txt toast文案
     */
    void promptText(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.showLongToast(txt);
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

    /**
     * 设置错误信息
     */
    private void initError() {
        Intent intent = getIntent();
        if (intent != null) {
            String errStr = intent.getStringExtra(EXTRA_RETMSG);
            if (!TextUtils.isEmpty(errStr)) {
                showWarnDialog(errStr);
            }else{
                mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
                mCount = intent.getIntExtra(EXTRA_COUNT, 0);
                // 1.开启服务端侦听
                initNetWorkServer();
            }
        }
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
                    finish();
                }
            });
        }
        mWarnDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setText(msg);
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
