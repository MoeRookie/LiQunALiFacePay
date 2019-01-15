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
import com.alibaba.fastjson.JSONArray;
import com.alipay.xdevicemanager.api.XDeviceManager;
import com.alipay.zoloz.smile2pay.service.Zoloz;
import com.alipay.zoloz.smile2pay.service.ZolozCallback;
import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.SettingItemBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayInitBean;
import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayResponseBean;

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
                case 3: // 人脸初始化请求成功
                    if (msg.obj != null) {
                        String json = (String) msg.obj;
                        // 解析json->zimId+zimInitClientData
                        FacePayInitBean facePayInitBean =
                                JSON.parseObject(json, FacePayInitBean.class);
                        String zimId = facePayInitBean.getJson().getZimId();
                        String zimInitClientData = facePayInitBean.getJson().getZimInitClientData();
                        // 唤起人脸识别
                        smile(zimId,zimInitClientData);
                    }
                    break;
                case 4: // 支付结果
                    if (msg.obj != null) {
                        if (msg.obj instanceof String) {
                            String json = (String) msg.obj;
                            FacePayResponseBean fprb = JSON.parseObject(json, FacePayResponseBean.class);
                            L.e("fprb.code = " + fprb.getJson().getAlipay_trade_pay_response().getCode());
                            // 1.关闭当前服务端侦听
                            closeServer();
                            // 3.finish掉当前界面
                            finish();
                            // 4.关闭提示支付中界面
                            PayingActivity.sInstance.finish();
                            // 2.跳转到刷脸结果界面
                            Intent intent = FacePayResultActivity.newIntent(
                                    SelectPayTypeActivity.this,
                                    mTotalPrice,
                                    mCount,
                                    fprb
                            );
                            startActivity(intent);
                        }
                    }
                    break;
                case 5:
                    // 弹出提示支付中的界面
                    enterPayingActivity();
                    break;
            }
        }
    };

    private void enterPayingActivity() {
        Intent intent = PayingActivity.newIntent(this);
        startActivity(intent);
    }

    private float mTotalPrice = 0.00f;
    private int mCount;
    private LoadingDialog mLoadingDialog;

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
    /**
     * 发起刷脸支付请求.
     * @param zimId 刷脸付token，从服务端获取，不要mock传入
     * @param protocal 刷脸付协议，从服务端获取，不要mock传入
     */
    private void smile(String zimId, String protocal) {
        Map params = new HashMap();
        params.put(KEY_INIT_RESP_NAME, protocal);
        /* start: 如果是预输入手机号方案，请加入以下代码,填入会员绑定的手机号，必须与支付宝帐号对应的手机号一致 */
        params.put("phone_number", "1381XXXXX");
        /* end: --------------------------------------------- */
        zoloz.zolozVerify(zimId, params, new ZolozCallback() {
            @Override
            public void response(final Map smileToPayResponse) {
                if (smileToPayResponse == null) {
                    L.e("==================人脸识别验证失败=================");
                    return;
                }
                String code = (String)smileToPayResponse.get("code");
                String fToken = (String)smileToPayResponse.get("ftoken");
                //刷脸成功
                if (CODE_SUCCESS.equalsIgnoreCase(code) && fToken != null) {
                    // 请求支付(显示提示支付中的对话框)
                    mMessage = Message.obtain();
                    mMessage.what = 5; // 刷脸成功,请求支付
                    mHandler.sendMessage(mMessage);
                    pay(fToken);
                }
            }
        });
    }
    /**
     * 弹出加载类型的对话框
     */
//    private void showLoadingDialog() {
//        if (mLoadingDialog == null) {
//            mLoadingDialog = new LoadingDialog(this,R.style.LoadingDialogStyle);
//        }
//        mLoadingDialog.show();
//        mLoadingDialog.setMessage("支付中 . . .");
//    }

    /**
     * 支付
     * @param fToken
     */
    private void pay(String fToken) {
        // 拼接参数
        // 获取设置信息
        String settingMsg = SpUtils.getString(
                this,
                ConstantValue.KEY_SETTING_CONTENT,
                ""
        );
        // 将设置信息转换为集合对象
        List<SettingItemBean> settingList = JSONArray.parseArray(settingMsg, SettingItemBean.class);
        // 获取门店编码+门店商户号+款台号
        String shopNo = settingList.get(3).getContent();
        String merchantNo = settingList.get(4).getContent();
        String catwalkNo = settingList.get(5).getContent();
        // 获取固定规则的当前时间
        String requestTime = getRequestTime(System.currentTimeMillis());
        // 获取流水号
        String flowNo = ALiFacePayApplication.getInstance().getFlowNo();
        // 1.生成订单号(规则:lqbh+门店编码+款台号+流水号的后6位+固定规则的当前时间)
        String orderNo =
                "lqbh"
                        + shopNo
                        + catwalkNo
                        + flowNo.substring(flowNo.length() - 6)
                        + requestTime;
        // 2.签名token值
        // 获取xDeviceManager
        XDeviceManager xDeviceManager = ALiFacePayApplication.getInstance().getXDeviceManager();
        // 加签
        int result[] = new int[1];
        String signedFToken = xDeviceManager.sign(fToken.getBytes(), result);
        if (result[0] != 0) {
            // 加签失败
            L.e("加签失败：" + result[0]);
        }else{
            // 发起支付请求
            requestFacePay(
                    ConstantValue.METHOD_RE_PAY,
                    JSON.toJSONString(
                            new FacePayRequestBean(
                                    fToken,
                    "利群集团刷脸支付",
                                    new FacePayRequestBean.ExtendParamsBean("2088031960490332"),
                                    ALiFacePayApplication.getInstance().getOperatorNo(),
                                    orderNo,
                                    "2088031960490332",
                                    "ALIPAY_F2F_PAYMENT",
                                    "security_code",
                                    merchantNo,
                                    shopNo,
                                    "群集团刷脸付消费",
                                    catwalkNo,
                                    signedFToken,
                                    "1m",
                                    String.valueOf(mTotalPrice))
                    ),
                    4
            );
        }
    }
    /**
     * 获取发送支付请求的时间
     * @param currentTime 当前时间的时间戳
     * @return 发送支付请求的时间
     */
    private String getRequestTime(long currentTime) {
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(date); // 发送请求的时间;
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
                    L.e("===========本地刷脸初始化失败==========");
                    return;
                }
                String code = (String)smileToPayResponse.get("code");
                final String metaInfo = (String)smileToPayResponse.get("metainfo");
                //获取metainfo成功
                if (CODE_SUCCESS.equalsIgnoreCase(code) && metaInfo != null) {
                    // 将metaInfo发送给商户服务端，由商户服务端发起刷脸初始化OpenAPI的调用
                    // 人脸初始化
                    requestFacePay(
                            ConstantValue.METHOD_ZOLOZ_INIT,
                            metaInfo,3);
                } else {
                    L.e("===========获取刷脸初始化参数失败==========");
                }
            }
        });
    }

    /**
     * 请求刷脸付
     * @param methodName 方法名
     * @param requestData 请求数据
     */
    private void requestFacePay(final String methodName, final String requestData, final int what) {
        L.e("requestData = " + requestData);
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
                    mMessage = Message.obtain();
                    mMessage.what = what;
                    mMessage.obj = bodyIn;
                    mHandler.sendMessage(mMessage);
                } catch (IOException e) {
                    L.e("==========客户端或者服务端数据异常(IO)==========");
                } catch (XmlPullParserException e) {
                    L.e("==========客户端或者服务端数据异常(XML)==========");
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
