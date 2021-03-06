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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.ShoppingBagBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SocketUtils;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.adapter.GoodsAdapter;
import com.liqun.www.liqunalifacepay.ui.adapter.ShoppingBag2Adapter;
import com.liqun.www.liqunalifacepay.ui.view.InputNumberDialog;
import com.liqun.www.liqunalifacepay.ui.view.MultipleDialog;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson.JSONArray.parseArray;
import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelGoodsBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.PreparePaymentBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.VipCardBean.*;

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
    private static final String EXTRA_IS_VIP = "com.liqun.www.liqunalifacepay.is_vip";
    public static SelfHelpPayActivity sActivity;
    public static Thread sServerSocketThread;
    public static ServerSocket sServerSocket;
    private StringBuffer mSb = new StringBuffer();
    private TextView mBtnCancelDeal;
    private TextView mBtnInput;
    private TextView mBtnPay;
    private Message mMessage;
    private LinearLayout mLLSelfPayFirst;
    private LinearLayout mLLSelfPaySecond;
    private TextView mTvResultHint;
    private RecyclerView mRvGoods;
    private TextView mBtnAddBag;
    private LinearLayoutManager mLayoutManager;
    private List<ScanGoodsResponseBean> mList = new ArrayList<>();
    private GoodsAdapter mAdapter;
    private final int REQUEST_CODE_INPUT_BAR_CODE_DIALOG = 0;
    private final int REQUEST_CODE_INPUT_PHONE_NUM_DIALOG = 1;
    private int mCount; // 记录商品数量
    private float mTotalPrice = 0.00f; // 记录商品的总价格
    private TextView mTvGoodsNum;
    private TextView mTvGoodsTotalPrice;
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
                        handleServerResult(msg.obj);
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
    private MultipleDialog mBagDialog;
    private List<ShoppingBagBean> mBagList = new ArrayList<>();
    private ShoppingBag2Adapter mBagAdapter;
    private int mIndex = -1;
    private TextView mBtnInputBarCode;
    private WarnDialog mWarnDialog;
    private InputNumberDialog mCardNumDialog;
    private TextView mTvMessage;
    private EditText mEtCardNum;
    private TextView mTvTitle;
    private TextView mTvErr;

    /**
     * 显示警告类型的对话框
     * @param msg 警告内容串
     */
    private void showWarnDialogActivity(String msg) {
        Intent intent = WarnDialogActivity.newIntent(this,msg);
        startActivity(intent);
    }

    /**
     * 处理从服务端读取过来的返回结果
     * @param obj
     */
    private void handleServerResult(Object obj) {
        if (obj instanceof VipCardResponseBean) { // 会员登录
            // 4.关闭当前对话框并修改title为-您好,尊敬的利群会员\n卡号:hjgnwklehgkwekg
            VipCardResponseBean vcrb = (VipCardResponseBean) obj;
            String retflag = vcrb.getRetflag();
            String retmsg = vcrb.getRetmsg();
            if ("1".equals(retflag)) {
                mTvMessage.setVisibility(View.VISIBLE);
                mTvMessage.setText(retmsg);
            } else if ("0".equals(retflag)) {
                mCardNumDialog.dismiss();
                mTvTitle.setText("您好 , 尊敬的利群会员\n卡号 : "+vcrb.getVipno());
            }
        }
        if (obj instanceof CancelDealResponseBean) { // 取消交易
            CancelDealResponseBean cdrb = (CancelDealResponseBean) obj;
            String retflag = cdrb.getRetflag();
            String retmsg = null;
            if ("0".equals(retflag)) {
                retmsg = "POS交易取消成功!";
            } else if ("1".equals(retflag)) {
                retmsg = cdrb.getRetmsg();
            }
            SocketUtils.closeServer(sServerSocket,sServerSocketThread);
            showWarnDialogActivity(retmsg);
        }
        if (obj instanceof ScanGoodsResponseBean) { // 扫描商品
            ScanGoodsResponseBean sgrb = (ScanGoodsResponseBean) obj;
            String retflag = sgrb.getRetflag();
            String retmsg = sgrb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "添加成功 , 请继续扫描下一件商品！";
                setGoodsListMsg("+",sgrb);
            }
            mTvResultHint.setText(retmsg);
        }
        if (obj instanceof CancelGoodsResponseBean) { // 取消商品
            CancelGoodsResponseBean cgrb = (CancelGoodsResponseBean) obj;
            String retflag = cgrb.getRetflag();
            String retmsg = cgrb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "删除成功 , 请继续扫描下一件商品！";
                ScanGoodsResponseBean goodsBean = mList.get(mIndex);
                setGoodsListMsg("-",goodsBean);
            }
            mTvResultHint.setText(retmsg);
        }
        if (obj instanceof PreparePaymentResponseBean) { // 准备付款
            PreparePaymentResponseBean pprb = (PreparePaymentResponseBean) obj;
            String retmsg = pprb.getRetmsg();
            String retflag = pprb.getRetflag();
            if ("0".equals(retflag)) {
                // 关闭服务端侦听
                SocketUtils.closeServer(sServerSocket,sServerSocketThread);
                // 数量
                float qty = pprb.getQty();
                // 总金额
                float total = pprb.getTotal();
                // 优惠金额
                float totaldsc = pprb.getTotaldsc();
                // 跳转选择支付方式界面
                Intent intent = SelectPayTypeActivity.newIntent(
                        SelfHelpPayActivity.this,qty,total,totaldsc);
                startActivity(intent);
            } else if ("1".equals(retflag)) {
                showWarnDialog(retmsg);
            }
        }
    }
    public static Intent newIntent(Context packageContext, boolean isVip) {
        Intent intent = new Intent(packageContext, SelfHelpPayActivity.class);
        intent.putExtra(EXTRA_IS_VIP, isVip);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = this;
        setContentView(R.layout.activity_self_help_pay);
        initData();
        initUI();
        setListener();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean isVip = intent.getBooleanExtra(
                    EXTRA_IS_VIP
                    , false
            );
            // 如果isVip==true则弹出提示输入手机号码的对话框
            if (isVip) {
                showCardNumDialog();
            }
        }
    }

    /**
     * 弹出输入手机号码的对话框
     */
    private void showCardNumDialog() {
        if (mCardNumDialog == null) {
            mCardNumDialog = new InputNumberDialog(this);
            mCardNumDialog.setLayoutResId(R.layout.dialog_input_phone_num);
            mCardNumDialog.setTitle("会员登录");
            mCardNumDialog.setOnYesClickListener(new InputNumberDialog.OnYesClickListener() {
                @Override
                public void onYesClicked() {
                    String cardNum = mEtCardNum.getText().toString().trim();
                    // 1.判空(空则提示手机号不能为空)
                    if (TextUtils.isEmpty(cardNum)) {
                        mTvMessage.setVisibility(View.VISIBLE);
                        mTvMessage.setText(R.string.hint_card_number_empty_err);
                        return;
                    }
                    // 2.判格式(不符合则提示手机号格式有误,并清空手机号输入框)
                    if(cardNum.length()!=7 && cardNum.length()!=8 && cardNum.length()!=11){
                        mTvMessage.setVisibility(View.VISIBLE);
                        mTvMessage.setText(R.string.hint_card_number_digit_err);
                        mEtCardNum.setText("");
                        return;
                    }
                    // 3.请求会员登录(失败则提示返回信息)
                    requestNetWorkServer(
                            ConstantValue.TAG_VIP_CARD,
                            new VipCardRequestBean(
                                    ALiFacePayApplication.getInstance().getHostIP(),
                                    cardNum,
                                    "1"
                            )
                    );
                }
            });
        }
        mCardNumDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mCardNumDialog.findViewById(R.id.tv_message);
        }
        if (mEtCardNum == null) {
            mEtCardNum = mCardNumDialog.findViewById(R.id.et_number);
        }
        // 每次显示dialog时,提示默认不显示
        mTvMessage.setVisibility(View.GONE);
    }


    private void initUI() {
        // 默认
        mTvTitle = findViewById(R.id.tv_title);
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
        mBtnAddBag = findViewById(R.id.btn_add_bag);

        mBtnInputBarCode = findViewById(R.id.btn_input_bar_code);
        mTvGoodsNum = findViewById(R.id.tv_goods_num);
        mTvGoodsTotalPrice = findViewById(R.id.tv_goods_total_price);
    }

    // 监听键盘按下
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent){
        // 每次按下获取键值后存贮(如果不是回车,则拼接)
        if (paramKeyEvent.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            mSb.append(asciiToString(String.valueOf(paramKeyEvent.getUnicodeChar())));
        }else{
            String barCode = mSb.toString().trim();
            // 检测到回车后获取到商品码
            if ((barCode.length() == 7
                    || barCode.length() == 8
                    || barCode.length() == 12
                    || barCode.length() == 13
                    || barCode.length() == 15
                    || barCode.length() == 20)) {
                // 请求扫描商品
                requestNetWorkServer(
                        ConstantValue.TAG_SCAN_GOODS,
                        new ScanGoodsRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                barCode,
                                1
                        )
                );
                mSb.delete(0,mSb.length());
            }
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
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
    private void setListener() {
        // 默认
        mBtnCancelDeal.setOnClickListener(this);
        mBtnInput.setOnClickListener(this);
        // 商品信息
        mBtnAddBag.setOnClickListener(this);
        mBtnPay.setOnClickListener(this);
        mAdapter.setOnItemDeleteClickListener(new GoodsAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemDeleteClicked(int i) {
                /**
                 * 从前添加,序号从1开始
                 */
                if (mList != null && mList.size() > 0) {
                    // 1.获取到当前要删除的goods
                    ScanGoodsResponseBean goodsBean = mList.get(i);
                    // 2.发起取消商品的请求(tag,ip、序号、barCode)
                    mIndex = i;
                    requestNetWorkServer(
                            ConstantValue.TAG_CANCEL_GOODS,
                            new CancelGoodsRequestBean(
                                    ALiFacePayApplication.getInstance().getHostIP(),
                                    mList.size()-i,
                                    goodsBean.getBarcode()
                            )
                    );
                }
            }
        });
        mBtnInputBarCode.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 等待结果的服务端监听
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
                showBarCodeDialog();
                break;
            case R.id.btn_add_bag: // 添加购物袋
                showShoppingBagDialog();
                break;
            case R.id.btn_input_bar_code:
                // 手输条码
                showBarCodeDialog();
                break;
            case R.id.btn_pay:
                // 跳转到选择支付方式界面
                if (mCount == 0) {
                    showWarnDialog(getString(R.string.hint_goods_number_err));
                }else{
                    // 发起准备付款的请求
                    requestNetWorkServer(
                            ConstantValue.TAG_PREPARE_PAYMENT,
                            new PreparePaymentRequestBean(
                                    ALiFacePayApplication.getInstance().getHostIP(),
                                    mCount,
                                    mCount,
                                    mTotalPrice
                            )
                    );
                }
                break;
        }
    }

    /**
     * 弹出警告类型的对话框
     */
    private void showWarnDialog(String err) {
        if (mWarnDialog == null) {
            mWarnDialog = new WarnDialog(this);
            mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClicked() {
                    mWarnDialog.dismiss();
                }
            });
        }
        mWarnDialog.show();
        if (mTvErr == null) {
            mTvErr = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvErr.setText(err);
    }

    /**
     * 显示添加购物袋的对话框
     */
    private void showShoppingBagDialog() {
        if (mBagDialog == null) {
            mBagDialog = new MultipleDialog(this);
            mBagDialog.setTitle("添加购物袋");
            // 将Sp中保存的购物袋信息保存为集合数据
            if (mBagList != null && mBagList.size() > 0) {
                mBagList.clear();
            }
            List<ShoppingBagBean> bagBeanList = parseArray(
                    SpUtils.getString(
                            this,
                            ConstantValue.SHOPPING_BAG_MSG,
                            ""
                    ), ShoppingBagBean.class
            );
            // 筛选出被选中的,更新其选中状态为false并添加到集合中
            for (int i = 0; i < bagBeanList.size(); i++) {
                ShoppingBagBean bagBean = bagBeanList.get(i);
                if (bagBean.isSelected()) {
                    bagBean.setSelected(false);
                    mBagList.add(bagBean);
                }
            }
            mBagAdapter = new ShoppingBag2Adapter(this, mBagList);
            mBagDialog.setAdapter(mBagAdapter);
            mBagAdapter.setOnItemCheckedChangeListener(new ShoppingBag2Adapter.OnItemCheckedChangeListener() {
                @Override
                public void onItemCheckedChanged(int i) {
                    ShoppingBagBean bagBean = mBagList.get(i);
                    bagBean.setSelected(!bagBean.isSelected());
                    mBagAdapter.notifyDataSetChanged();
                }
            });
            setBagDialogListener();
        }
        mBagDialog.show();
    }

    /**
     * 设置多选对话框监听
     */
    private void setBagDialogListener() {
        mBagDialog.setOnNoClickListener("取消", new MultipleDialog.OnNoClickListener() {
            @Override
            public void onNoClick() {
                mBagDialog.dismiss();
            }
        });
        mBagDialog.setOnYesClickListener("确定", new MultipleDialog.OnYesClickListener() {
            @Override
            public void onYesClicked() {
                // 筛选出用户选中的购物袋并发起扫描商品请求
                for (int i = 0; i < mBagList.size(); i++) {
                    ShoppingBagBean bagBean = mBagList.get(i);
                    if (bagBean.isSelected()) {
                        requestNetWorkServer(
                                ConstantValue.TAG_SCAN_GOODS,
                                new ScanGoodsRequestBean(
                                        ALiFacePayApplication.getInstance().getHostIP(),
                                        bagBean.getProductNo(),
                                        1
                                )
                        );
                    }
                }
                mBagDialog.dismiss();
            }
        });
    }

    /**
     * 弹出条码号输入对话框
     */
    private void showBarCodeDialog() {
        Intent intent = InputBarCodeActivity.newIntent(this);
        startActivityForResult(intent,REQUEST_CODE_INPUT_BAR_CODE_DIALOG);
        SocketUtils.closeServer(sServerSocket,sServerSocketThread);
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
                            ALiFacePayApplication.getInstance().getPosServerIp(),
                            Integer.valueOf(ALiFacePayApplication.getInstance().getPosServerPort()));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INPUT_BAR_CODE_DIALOG && resultCode == RESULT_OK) {
            if (data != null) {
                ScanGoodsResponseBean goodsBean =
                        (ScanGoodsResponseBean) data.
                                getSerializableExtra(InputBarCodeActivity.EXTRA_RET_MSG);
                // 设置商品信息
                setGoodsListMsg("+",goodsBean);
            }
        }
    }

    /**
     * 设置商品列表信息
     * @param operator 操作符
     * @param goodsBean 商品
     */
    private void setGoodsListMsg(String operator, ScanGoodsResponseBean goodsBean) {
        switch (operator) {
            case "+":
                if (mList.size() == 0) {
                    // 隐藏默认界面,显示商品信息界面
                    mLLSelfPayFirst.setVisibility(View.GONE);
                    mLLSelfPaySecond.setVisibility(View.VISIBLE);
                }
                mList.add(0,goodsBean);
                mCount++;
                mTotalPrice += goodsBean.getPrice();
                break;
            case "-":
                mList.remove(goodsBean);
                mCount--;
                mTotalPrice -= goodsBean.getPrice();
                break;
        }
        // 设置商品个数
        mTvGoodsNum.setText("共"+(mCount)+"件商品");
        // 设置商品总价格
        BigDecimal bd = new BigDecimal(mTotalPrice);
        mTotalPrice = bd.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
        mTvGoodsTotalPrice.setText("￥ " + mTotalPrice);
        mAdapter.notifyDataSetChanged();
    }
}
