package com.givon.plugin;//package com.givon.quickpay;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.media.TransportMediator;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.givon.quickpay.utils.AbSharedUtil;
//import com.givon.quickpay.utils.DBManager;
//import com.givon.quickpay.utils.MD5;
//import com.givon.quickpay.utils.OrderBean;
//import com.givon.quickpay.utils.PayHelperUtils;
//import com.givon.quickpay.utils.QrCodeBean;
//import com.lidroid.xutils.HttpUtils;
//import com.lidroid.xutils.exception.HttpException;
//import com.lidroid.xutils.http.RequestParams;
//import com.lidroid.xutils.http.ResponseInfo;
//import com.lidroid.xutils.http.callback.RequestCallBack;
//import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
//
//import java.io.File;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import org.apache.http.cookie.ClientCookie;
//import org.apache.http.cookie.SM;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//
//public class MainActivity extends Activity {
//    public static String BILLRECEIVED_ACTION = "com.tools.payhelper.billreceived";
//    public static String GETTRADEINFO_ACTION = "com.tools.payhelper.gettradeinfo";
//    public static String LOGINIDRECEIVED_ACTION = "com.tools.payhelper.loginidreceived";
//    public static String MSGRECEIVED_ACTION = "com.tools.payhelper.msgreceived";
//    public static String NOTIFY_ACTION = "com.tools.payhelper.notify";
//    public static String QRCODERECEIVED_ACTION = "com.tools.payhelper.qrcodereceived";
//    public static String SAVEALIPAYCOOKIE_ACTION = "com.tools.payhelper.savealipaycookie";
//    public static String TRADENORECEIVED_ACTION = "com.tools.payhelper.tradenoreceived";
//    public static String UPDATE_URL = "https://dev.yykayou.com:444/test/update/index.php";
//    public static String VERSION2 = "hh";
//    public static String VERSIONV0 = (VERSIONV1 + "@" + VERSIONV2);
//    public static String VERSIONV1 = "3";
//    public static String VERSIONV2 = (VERSION2 + "1.0.5");
//    public static String VERSIONVKEY = "njCoV5596tH4BQ73yy77";
//    public static int WEBSEERVER_PORT = 8080;
//    public static TextView console;
//    public static Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            String txt = msg.getData().getString("log");
//            if (MainActivity.console != null) {
//                if (MainActivity.console.getText() == null) {
//                    MainActivity.console.setText(txt);
//                } else if (MainActivity.console.getText().toString().length() > 7500) {
//                    MainActivity.console.setText("日志定时清理完成...\n\n" + txt);
//                } else {
//                    MainActivity.console.setText(new StringBuilder(String.valueOf(MainActivity.console.getText().toString())).append("\n\n").append(txt).toString());
//                }
//                MainActivity.scrollView.post(new Runnable() {
//                    public void run() {
//                        MainActivity.scrollView.fullScroll(TransportMediator.KEYCODE_MEDIA_RECORD);
//                    }
//                });
//            }
//            super.handleMessage(msg);
//        }
//    };
//    private static ScrollView scrollView;
//    private AlarmReceiver alarmReceiver;
//    private BillReceived billReceived;
//    private String currentAlipay = "";
//    private String currentQQ = "";
//    private String currentWechat = "";
//    private WebServer mVideoServer;
//
//    class BillReceived extends BroadcastReceiver {
//        BillReceived() {
//        }
//
//        public void onReceive(Context context, Intent intent) {
//            try {
//                String money;
//                String mark;
//                String type;
//                DBManager dBManager;
//                String dt;
//                if (intent.getAction().contentEquals(MainActivity.BILLRECEIVED_ACTION)) {
//                    String no = intent.getStringExtra("bill_no");
//                    money = intent.getStringExtra("bill_money");
//                    mark = intent.getStringExtra("bill_mark");
//                    type = intent.getStringExtra("bill_type");
//                    dBManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
//                    dt = new StringBuilder(String.valueOf(System.currentTimeMillis())).toString();
//                    dBManager.addOrder(new OrderBean(money, mark, type, no, dt, "", 0));
//                    String typestr = "";
//                    if (type.equals("alipay")) {
//                        typestr = "支付宝";
//                    } else if (type.equals("wechat")) {
//                        typestr = "微信";
//                    } else if (type.equals("qq")) {
//                        typestr = "QQ";
//                    } else if (type.equals("alipay_dy")) {
//                        typestr = "支付宝店员";
//                        dt = intent.getStringExtra("time");
//                    }
//                    MainActivity.sendmsg("收到" + typestr + "订单,订单号：" + no + "金额：" + money + "备注：" + mark);
//                    notifyapi(type, no, money, mark, dt);
//                } else if (intent.getAction().contentEquals(MainActivity.QRCODERECEIVED_ACTION)) {
//                    money = intent.getStringExtra("money");
//                    mark = intent.getStringExtra("mark");
//                    type = intent.getStringExtra("type");
//                    String payurl = intent.getStringExtra("payurl");
//                    dBManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
//                    dt = new StringBuilder(String.valueOf(System.currentTimeMillis())).toString();
//                    money = new DecimalFormat("0.00").format(Double.parseDouble(money));
//                    dBManager.addQrCode(new QrCodeBean(money, mark, type, payurl, dt));
//                    MainActivity.sendmsg("生成成功,金额:" + money + "备注:" + mark + "二维码:" + payurl);
//                } else if (intent.getAction().contentEquals(MainActivity.MSGRECEIVED_ACTION)) {
//                    MainActivity.sendmsg(intent.getStringExtra("msg"));
//                } else if (intent.getAction().contentEquals(MainActivity.SAVEALIPAYCOOKIE_ACTION)) {
//                    PayHelperUtils.updateAlipayCookie(MainActivity.this, intent.getStringExtra("alipaycookie"));
//                } else if (intent.getAction().contentEquals(MainActivity.LOGINIDRECEIVED_ACTION)) {
//                    String loginid = intent.getStringExtra("loginid");
//                    type = intent.getStringExtra("type");
//                    if (!TextUtils.isEmpty(loginid)) {
//                        if (type.equals("wechat")) {
//                            if (!loginid.equals(MainActivity.this.currentWechat)) {
//                                MainActivity.sendmsg("当前登录微信账号：" + loginid);
//                                MainActivity.this.currentWechat = loginid;
//                                AbSharedUtil.putString(MainActivity.this.getApplicationContext(), type, loginid);
//                                return;
//                            }
//                        }
//                        if (type.equals("alipay")) {
//                            if (!loginid.equals(MainActivity.this.currentAlipay)) {
//                                MainActivity.sendmsg("当前登录支付宝账号：" + loginid);
//                                MainActivity.this.currentAlipay = loginid;
//                                AbSharedUtil.putString(MainActivity.this.getApplicationContext(), type, loginid);
//                                return;
//                            }
//                        }
//                        if (type.equals("qq")) {
//                            if (!loginid.equals(MainActivity.this.currentQQ)) {
//                                MainActivity.sendmsg("当前登QQ账号：" + loginid);
//                                MainActivity.this.currentQQ = loginid;
//                                AbSharedUtil.putString(MainActivity.this.getApplicationContext(), type, loginid);
//                            }
//                        }
//                    }
//                } else if (intent.getAction().contentEquals(MainActivity.TRADENORECEIVED_ACTION)) {
//                    String tradeno = intent.getStringExtra("tradeno");
//                    String cookie = intent.getStringExtra("cookie");
//                    dBManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
//                    if (dBManager.isExistTradeNo(tradeno)) {
//                        MainActivity.sendmsg("出现重复流水号，疑似掉单，5秒后启动补单");
//                        r1 = context;
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                PayHelperUtils.getTradeInfo2(r1);
//                            }
//                        }, 5000);
//                        return;
//                    }
//                    dBManager.addTradeNo(tradeno, "0");
//                    String url = "https://tradeeportlet.alipay.com/wireless/tradeDetail.htm?tradeNo=" + tradeno + "&source=channel&_from_url=https%3A%2F%2Frender.alipay.com%2Fp%2Fz%2Fmerchant-mgnt%2Fsimple-order._h_t_m_l_%3Fsource%3Dmdb_card";
//                    try {
//                        HttpUtils httpUtils = new HttpUtils(15000);
//                        httpUtils.configResponseTextCharset("GBK");
//                        RequestParams params = new RequestParams();
//                        params.addHeader(SM.COOKIE, cookie);
//                        r1 = context;
//                        final DBManager dBManager2 = dBManager;
//                        final String str = tradeno;
//                        httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {
//                            public void onFailure(HttpException arg0, String arg1) {
//                                PayHelperUtils.sendmsg(r1, "服务器异常" + arg1);
//                            }
//
//                            public void onSuccess(ResponseInfo<String> arg0) {
//                                try {
//                                    Document document = Jsoup.parse(arg0.result);
//                                    Elements elements = document.getElementsByClass("trade-info-value");
//                                    if (elements.size() >= 5) {
//                                        dBManager2.updateTradeNo(str, "1");
//                                        String money = document.getElementsByClass("amount").get(0).ownText().replace("+", "").replace("-", "");
//                                        String mark = elements.get(3).ownText();
//                                        String dt = new StringBuilder(String.valueOf(System.currentTimeMillis())).toString();
//                                        dBManager2.addOrder(new OrderBean(money, mark, "alipay", str, dt, "", 0));
//                                        MainActivity.sendmsg("收到支付宝订单,订单号：" + str + "金额：" + money + "备注：" + mark);
//                                        BillReceived.this.notifyapi("alipay", str, money, mark, dt);
//                                    }
//                                } catch (Exception e) {
//                                    PayHelperUtils.sendmsg(r1, "TRADENORECEIVED_ACTION-->>onSuccess异常" + e.getMessage());
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        PayHelperUtils.sendmsg(context, "TRADENORECEIVED_ACTION异常" + e.getMessage());
//                    }
//                } else if (intent.getAction().equals(MainActivity.GETTRADEINFO_ACTION)) {
//                    r1 = context;
//                    new Handler().postDelayed(new Runnable() {
//                        public void run() {
//                            PayHelperUtils.getTradeInfo2(r1);
//                        }
//                    }, 5000);
//                }
//            } catch (Exception e2) {
//                PayHelperUtils.sendmsg(context, "BillReceived异常" + e2.getMessage());
//            }
//        }
//
//        public void notifyapi(String type, final String no, String money, String mark, String dt) {
//            try {
//                String notifyurl = AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "notifyurl");
//                String signkey = AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "signkey");
//                if (TextUtils.isEmpty(notifyurl) || TextUtils.isEmpty(signkey)) {
//                    MainActivity.sendmsg("发送异步通知(" + MainActivity.VERSIONV2 + ")异常，异步通知地址或密钥为空");
//                    update(no, "异步通知(" + MainActivity.VERSIONV2 + ")地址或密钥为空");
//                    return;
//                }
//                signkey = new StringBuilder(String.valueOf(signkey)).append(MainActivity.VERSIONVKEY).toString();
//                String account = "";
//                if (type.equals("alipay")) {
//                    account = AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "alipay");
//                } else if (type.equals("wechat")) {
//                    account = AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "wechat");
//                } else if (type.equals("qq")) {
//                    account = AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "qq");
//                }
//                HttpUtils httpUtils = new HttpUtils(15000);
//                String sign = MD5.md5(new StringBuilder(String.valueOf(dt)).append(mark).append(money).append(no).append(type).append(signkey).append(AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "userids")).append(MainActivity.VERSIONV0).toString());
//                RequestParams params = new RequestParams();
//                params.addBodyParameter("type", type);
//                params.addBodyParameter("no", no);
//                params.addBodyParameter(ClientCookie.VERSION_ATTR, MainActivity.VERSIONV0);
//                params.addBodyParameter("userids", AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "userids"));
//                params.addBodyParameter("money", money);
//                params.addBodyParameter("mark", mark);
//                params.addBodyParameter("dt", dt);
//                MainActivity.sendmsg("服务器针对（" + dt + mark + money + no + type + "****" + AbSharedUtil.getString(MainActivity.this.getApplicationContext(), "userids") + "****" + "）进行签名,签名结果是：" + sign);
//                if (!TextUtils.isEmpty(account)) {
//                    params.addBodyParameter("account", account);
//                }
//                params.addBodyParameter("sign", sign);
//                httpUtils.send(HttpMethod.POST, notifyurl, params, new RequestCallBack<String>() {
//                    public void onFailure(HttpException arg0, String arg1) {
//                        MainActivity.sendmsg("发送异步通知(" + MainActivity.VERSIONV2 + ")异常，服务器异常" + arg1);
//                        BillReceived.this.update(no, arg1);
//                    }
//
//                    public void onSuccess(ResponseInfo<String> arg0) {
//                        String result = arg0.result;
//                        if (result.contains("success")) {
//                            MainActivity.sendmsg("发送异步通知(" + MainActivity.VERSIONV2 + ")成功，服务器返回" + result);
//                        } else {
//                            MainActivity.sendmsg("发送异步通知(" + MainActivity.VERSIONV2 + ")失败，服务器返回" + result);
//                        }
//                        BillReceived.this.update(no, result);
//                    }
//                });
//            } catch (Exception e) {
//                MainActivity.sendmsg("notifyapi异常" + e.getMessage());
//            }
//        }
//
//        private void update(String no, String result) {
//            new DBManager(CustomApplcation.getInstance().getApplicationContext()).updateOrder(no, result);
//        }
//    }
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(128);
//        setContentView(R.layout.activity_main);
//        console = (TextView) findViewById(R.id.console);
//        scrollView = (ScrollView) findViewById(R.id.scrollview);
//        try {
//            this.mVideoServer = new WebServer(this, WEBSEERVER_PORT);
//            this.mVideoServer.start();
//            sendmsg("web服务器启动成功，端口:" + WEBSEERVER_PORT);
//        } catch (Exception e) {
//            sendmsg("web服务器启动失败，错误:" + e.getMessage());
//        }
//        findViewById(R.id.start_alipay).setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                Intent broadCastIntent = new Intent();
//                broadCastIntent.setAction("com.payhelper.alipay.start");
//                broadCastIntent.putExtra("mark", "test" + new StringBuilder(String.valueOf(System.currentTimeMillis() / 10000)).toString());
//                broadCastIntent.putExtra("money", "0.01");
//                MainActivity.this.sendBroadcast(broadCastIntent);
//            }
//        });
//        findViewById(R.id.start_wechat).setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                Intent broadCastIntent = new Intent();
//                broadCastIntent.setAction("com.payhelper.wechat.start");
//                broadCastIntent.putExtra("mark", "test" + new StringBuilder(String.valueOf(System.currentTimeMillis() / 10000)).toString());
//                broadCastIntent.putExtra("money", "0.01");
//                MainActivity.this.sendBroadcast(broadCastIntent);
//            }
//        });
//        findViewById(R.id.start_qq).setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                Intent broadCastIntent = new Intent();
//                broadCastIntent.setAction("com.payhelper.qq.start");
//                broadCastIntent.putExtra("mark", "test" + new StringBuilder(String.valueOf(System.currentTimeMillis() / 10000)).toString());
//                broadCastIntent.putExtra("money", "0.01");
//                MainActivity.this.sendBroadcast(broadCastIntent);
//            }
//        });
//        findViewById(R.id.setting).setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                MainActivity.this.startActivity(new Intent(MainActivity.this, SettingActivity.class));
//            }
//        });
//        this.billReceived = new BillReceived();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BILLRECEIVED_ACTION);
//        intentFilter.addAction(MSGRECEIVED_ACTION);
//        intentFilter.addAction(QRCODERECEIVED_ACTION);
//        intentFilter.addAction(TRADENORECEIVED_ACTION);
//        intentFilter.addAction(LOGINIDRECEIVED_ACTION);
//        intentFilter.addAction(SAVEALIPAYCOOKIE_ACTION);
//        intentFilter.addAction(GETTRADEINFO_ACTION);
//        registerReceiver(this.billReceived, intentFilter);
//        this.alarmReceiver = new AlarmReceiver();
//        IntentFilter alarmIntentFilter = new IntentFilter();
//        alarmIntentFilter.addAction(NOTIFY_ACTION);
//        registerReceiver(this.alarmReceiver, alarmIntentFilter);
//        startService(new Intent(this, DaemonService.class));
//        PayHelperUtils.startAlipayMonitor(this);
//        sendmsg("当前软件版本:" + PayHelperUtils.getVerName(getApplicationContext()));
//        checkVersion();
//    }
//
//    protected void onDestroy() {
//        unregisterReceiver(this.alarmReceiver);
//        unregisterReceiver(this.billReceived);
//        this.mVideoServer.stop();
//        super.onDestroy();
//    }
//
//    protected void onResume() {
//        super.onResume();
//    }
//
//    public static void sendmsg(String txt) {
//        LogToFile.i("payhelper", txt);
//        Message msg = new Message();
//        msg.what = 1;
//        Bundle data = new Bundle();
//        data.putString("log", new StringBuilder(String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())))).append(":").append("  结果:").append(txt).toString());
//        msg.setData(data);
//        try {
//            handler.sendMessage(msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == 4) {
//            moveTaskToBack(true);
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    public void checkVersion() {
//        final int currentVersionCode = PayHelperUtils.getVersionCode(this);
//        String currentVersionName = PayHelperUtils.getVerName(this);
//        RequestParams requestParams = new RequestParams();
//        requestParams.addBodyParameter(ClientCookie.VERSION_ATTR, currentVersionName);
//        requestParams.addBodyParameter("version2", VERSION2);
//        requestParams.addBodyParameter("version0", VERSIONV0);
//        String version_time = Long.toString(new Date().getTime());
//        String version_sign = MD5.md5(new StringBuilder(String.valueOf(currentVersionName)).append(VERSION2).append(VERSIONV0).append(version_time).append(VERSIONVKEY).toString());
//        requestParams.addBodyParameter("time", version_time);
//        requestParams.addBodyParameter("sign", version_sign);
//        new HttpUtils().send(HttpMethod.POST, UPDATE_URL, requestParams, new RequestCallBack<String>() {
//            public void onFailure(HttpException arg0, String arg1) {
//                PayHelperUtils.sendmsg(MainActivity.this, "APP检查更新失败");
//            }
//
//            public void onSuccess(ResponseInfo<String> arg0) {
//                try {
//                    JSONObject jsonObject = new JSONObject(arg0.result);
//                    if (jsonObject.getInt("code") == 1 && Integer.parseInt(jsonObject.getString("version_code")) > currentVersionCode) {
//                        String download_url = jsonObject.getString("url");
//                        String msg = jsonObject.getString("msg");
//                        MainActivity.sendmsg("发现新版本，正在下载更新");
//                        MainActivity.sendmsg("更新内容：" + msg);
//                        MainActivity.this.download(download_url);
//                    }
//                } catch (Exception e) {
//                }
//            }
//        });
//    }
//
//    public void download(String url) {
//        new HttpUtils().download(url, "/sdcard/download/payhelper.apk", false, true, new RequestCallBack<File>() {
//            public void onSuccess(ResponseInfo<File> response) {
//                File file = response.result;
//                MainActivity.sendmsg("下载完成，开始安装");
//                MainActivity.this.installApk(file);
//            }
//
//            public void onFailure(HttpException arg0, String arg1) {
//                MainActivity.sendmsg("下载失败");
//            }
//
//            public void onLoading(long total, long current, boolean isUploading) {
//                MainActivity.sendmsg("下载进度" + ((int) ((((double) current) / ((double) total)) * 100.0d)) + "%");
//                super.onLoading(total, current, isUploading);
//            }
//
//            public void onStart() {
//                MainActivity.sendmsg("开始下载...");
//                super.onStart();
//            }
//        });
//    }
//
//    public void installApk(File file) {
//        File apkFile = file;
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(268435456);
//        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
//        startActivity(intent);
//    }
//}
