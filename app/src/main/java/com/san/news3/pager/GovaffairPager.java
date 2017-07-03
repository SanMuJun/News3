package com.san.news3.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.san.news3.R;
import com.san.news3.adapter.GovaffairPagerAdapter;
import com.san.news3.base.BasePager;
import com.san.news3.domain.ShoppingCart;
import com.san.news3.pay.PayResult;
import com.san.news3.pay.SignUtils;
import com.san.news3.utils.CartProvider;
import com.san.news3.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by San on 2016/12/1.
 */
public class GovaffairPager extends BasePager {


    // 商户PID
    public static final String PARTNER = "2088911876712276";
    // 商户收款账号
    public static final String SELLER = "chenlei@atguigu.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO1Kgoevz9jI96/o\n" +
            "3Ghm+Ts7SEDTCm/Ct/vKMlNzGQ+T2KpL7k9P0FNH6uoLrDa5WFaCnyU663yV4oDc\n" +
            "OR2yRnWLhjX+oAFf8EBSxHWPgXqF+BmpTGwMDUVqY2fUAl+4+XGM20pabc7wT/Jt\n" +
            "FwBtdtbJuceCFEF8DyCXFBBKRzuZAgMBAAECgYAfpDbhpwT9MFcu4ss5NLn5Nv98\n" +
            "fWe/3WQxNBYf4JIv7JQQqU1e0BLEeCuQB/xv06k+5+4WxdOO6mGoszT+i74vPq30\n" +
            "hcFMjkiVFcyDLazhBOVEtAxYxhkyVo8jbDkJUbgMyaO3PQMGZdi5lyVrsvHU+/JV\n" +
            "b6CuZqaW+88Fg+h9AQJBAPnBqw7Mr8pcSnJtf2ngaaMEXHJ5wpfVi5kuZ1DVgbL0\n" +
            "28kUQfVjxKReK6sehHO9fxegmKtGvSyhYxx6ch8K/nkCQQDzORF2lTxUP5VA7wb0\n" +
            "y2AaoatOZzAEdkj0Z4jdgSFRpOVlFalQfxZAmqSxTVL7hlCGcOg94oUP62wHrf84\n" +
            "zF4hAkEAqCJXnLvw78LXn1bVeppHoyWxcqNDe+GXe8TAaquBB89NEn3ftWm3nIuE\n" +
            "zErcEeqU33wOqucRPTVcOnS31OwayQJBAOPKp/taq6Tv49ZrxyUPMJPgpDMK22Li\n" +
            "cVNNgFaL8OupNxrkHa4BSJL7ApH3rGdblSFEr43+D8coIwZSRH0qkmECQCKn6qrJ\n" +
            "fU/CjHxt7bczthjYq1xurOM0Cxj+tgixSHC0L5texpNICEBNjtCv1r7zkO3in3bU\n" +
            "+T+BEWRwGg86G3Y=";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDtSoKHr8/YyPev6NxoZvk7O0hA\n" +
            "0wpvwrf7yjJTcxkPk9iqS+5PT9BTR+rqC6w2uVhWgp8lOut8leKA3DkdskZ1i4Y1\n" +
            "/qABX/BAUsR1j4F6hfgZqUxsDA1FamNn1AJfuPlxjNtKWm3O8E/ybRcAbXbWybnH\n" +
            "ghRBfA8glxQQSkc7mQIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };



    private RecyclerView recyclerview;
    private CheckBox ck_chechall;
    private TextView tv_price_total;
    private Button btn_order;
    private Button btn_delete;
    private TextView tv_wares;

    private final CartProvider cartProvider;
    private List<ShoppingCart> data;
    private GovaffairPagerAdapter adapter;

    private final static int ACTION_EDIT=0;
    private final static int ACTION_COMPLETE=1;

    public GovaffairPager(Context context) {
        super(context);
        cartProvider = new CartProvider(context);
    }


    @Override
    public void initData() {
        super.initData();

        LogUtil.e("购物车被初始化了");
        tv_title.setText("购物车");
        btn_cart.setVisibility(View.VISIBLE);

        View view=View.inflate(context, R.layout.warespage,null);
        recyclerview= (RecyclerView) view.findViewById(R.id.recyclerview);
        ck_chechall= (CheckBox) view.findViewById(R.id.ck_chechall);
        tv_price_total= (TextView) view.findViewById(R.id.tv_price_total);
        tv_wares= (TextView) view.findViewById(R.id.tv_wares);
        btn_order= (Button) view.findViewById(R.id.btn_order);
        btn_delete= (Button) view.findViewById(R.id.btn_delete);

        if (fl_content!=null){
            fl_content.removeAllViews();
        }
        fl_content.addView(view);

        btn_cart.setTag(ACTION_EDIT);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int action= (int) btn_cart.getTag();
                if (action==ACTION_EDIT){

                    showDelete();

                }else if (action==ACTION_COMPLETE){

                    hideDelete();

                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             adapter.deleteData();
                adapter.showTotalPrice();
                checkSomething();
                adapter.checkAll();

            }
        });



        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pay(view);


            }
        });

        showData();

    }



    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay(View v) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(context).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //

                        }
                    }).show();
            return;
        }
        String orderInfo = getOrderInfo("购物商品", "买买买", adapter.getTotalPrice()+"");

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }



    private void checkSomething() {
        if (adapter!=null&&adapter.getItemCount()>0){

            tv_wares.setVisibility(View.GONE);
        }else {
            tv_wares.setVisibility(View.VISIBLE);
        }
    }

    private void hideDelete() {

        btn_cart.setText("完成");
        btn_cart.setTag(ACTION_EDIT);

        adapter.chechAll_None(true);
        adapter.checkAll();

        btn_delete.setVisibility(View.GONE);
        btn_order.setVisibility(View.VISIBLE);

        adapter.showTotalPrice();


    }

    private void showDelete() {

        btn_cart.setText("编辑");
        btn_cart.setTag(ACTION_COMPLETE);

        adapter.chechAll_None(false);
        adapter.checkAll();

        btn_delete.setVisibility(View.VISIBLE);
        btn_order.setVisibility(View.GONE);

        adapter.showTotalPrice();

    }

    private void showData() {

        data = cartProvider.getData();
        LogUtil.e("-=-=-=-=-=-="+data);


        if (data!=null&&data.size()>0){

           tv_wares.setVisibility(View.GONE);
            adapter = new GovaffairPagerAdapter(context,data,ck_chechall,tv_price_total);
            recyclerview.setAdapter(adapter);
            recyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

        }else {

            tv_wares.setVisibility(View.VISIBLE);

        }
    }
}
