package com.xdandroid.hellodaemon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class IntentWrapperPager {

    //Android 7.0+ Doze 模式
    protected static final int DOZE = 98;
    //华为 自启管理
    protected static final int HUAWEI = 99;
    //华为 锁屏清理
    protected static final int HUAWEI_GOD = 100;
    //小米 自启动管理
    protected static final int XIAOMI = 101;
    //小米 神隐模式
    protected static final int XIAOMI_GOD = 102;
    //三星 5.0/5.1 自启动应用程序管理
    protected static final int SAMSUNG_L = 103;
    //魅族 自启动管理
    protected static final int MEIZU = 104;
    //魅族 待机耗电管理
    protected static final int MEIZU_GOD = 105;
    //Oppo 自启动管理
    protected static final int OPPO = 106;
    //三星 6.0+ 未监视的应用程序管理
    protected static final int SAMSUNG_M = 107;
    //Oppo 自启动管理(旧版本系统)
    protected static final int OPPO_OLD = 108;
    //Vivo 后台高耗电
    protected static final int VIVO_GOD = 109;
    //金立 应用自启
    protected static final int GIONEE = 110;
    //乐视 自启动管理
    protected static final int LETV = 111;
    //乐视 应用保护
    protected static final int LETV_GOD = 112;
    //酷派 自启动管理
    protected static final int COOLPAD = 113;
    //联想 后台管理
    protected static final int LENOVO = 114;
    //联想 后台耗电优化
    protected static final int LENOVO_GOD = 115;
    //中兴 自启管理
    protected static final int ZTE = 116;
    //中兴 锁屏加速受保护应用
    protected static final int ZTE_GOD = 117;

    protected static List<IntentWrapperPager> sIntentWrapperList;
    protected static List<IntentPage> sIntentPageList;

    private static String mTitle;
    private static String mMsg;
    private static boolean isDialog = true;

    public static List<IntentPage> getIntentPageList() {
        if (sIntentPageList == null) {

            if (!DaemonEnv.sInitialized) return new ArrayList<>();

            sIntentPageList = new ArrayList<>();

            //Android 7.0+ Doze 模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PowerManager pm = (PowerManager) DaemonEnv.sApp.getSystemService(Context.POWER_SERVICE);
                boolean ignoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(DaemonEnv.sApp.getPackageName());
                if (!ignoringBatteryOptimizations) {
                    Intent dozeIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    dozeIntent.setData(Uri.parse("package:" + DaemonEnv.sApp.getPackageName()));
                    sIntentPageList.add(new IntentPage(dozeIntent, DOZE));
                }
            }

            //华为 自启管理
            Intent huaweiIntent = new Intent();
            huaweiIntent.setAction("huawei.intent.action.HSM_BOOTAPP_MANAGER");
            sIntentPageList.add(new IntentPage(huaweiIntent, HUAWEI));

            //华为 锁屏清理
            Intent huaweiGodIntent = new Intent();
            huaweiGodIntent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            sIntentPageList.add(new IntentPage(huaweiGodIntent, HUAWEI_GOD));

            //小米 自启动管理
            Intent xiaomiIntent = new Intent();
            xiaomiIntent.setAction("miui.intent.action.OP_AUTO_START");
            xiaomiIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sIntentPageList.add(new IntentPage(xiaomiIntent, XIAOMI));

            //小米 神隐模式
            Intent xiaomiGodIntent = new Intent();
            xiaomiGodIntent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
            xiaomiGodIntent.putExtra("package_name", DaemonEnv.sApp.getPackageName());
            xiaomiGodIntent.putExtra("package_label", sApplicationName);
            sIntentPageList.add(new IntentPage(xiaomiGodIntent, XIAOMI_GOD));

            //三星 5.0/5.1 自启动应用程序管理
            Intent samsungLIntent = DaemonEnv.sApp.getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
            if (samsungLIntent != null)
                sIntentPageList.add(new IntentPage(samsungLIntent, SAMSUNG_L));

            //三星 6.0+ 未监视的应用程序管理
            Intent samsungMIntent = new Intent();
            samsungMIntent.setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity"));
            sIntentPageList.add(new IntentPage(samsungMIntent, SAMSUNG_M));

            //魅族 自启动管理
            Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
            meizuIntent.putExtra("packageName", DaemonEnv.sApp.getPackageName());
            sIntentPageList.add(new IntentPage(meizuIntent, MEIZU));

            //魅族 待机耗电管理
            Intent meizuGodIntent = new Intent();
            meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.powerui.PowerAppPermissionActivity"));
            sIntentPageList.add(new IntentPage(meizuGodIntent, MEIZU_GOD));

            //Oppo 自启动管理
            Intent oppoIntent = new Intent();
            oppoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
            oppoIntent.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
            if (DaemonEnv.sApp.getPackageManager().resolveActivity(oppoIntent, 0) == null) {
                componentName = ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
            }
            oppoIntent.setComponent(componentName);
            sIntentPageList.add(new IntentPage(oppoIntent, OPPO));

            //Oppo 自启动管理(旧版本系统)
            Intent oppoOldIntent = new Intent();
            oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.permission.startup.StartupAppListActivity"));
            sIntentPageList.add(new IntentPage(oppoOldIntent, OPPO_OLD));

            // Intent { act=com.coloros.safecenter.permission.startup.StartupAppListActivity cmp=com.coloros.safecenter/.startupapp.StartupAppListActivity }
            //Vivo 后台高耗电
            Intent vivoGodIntent = new Intent();
            vivoGodIntent.setComponent(new ComponentName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"));
            sIntentPageList.add(new IntentPage(vivoGodIntent, VIVO_GOD));

            //金立 应用自启
            Intent gioneeIntent = new Intent();
            gioneeIntent.setComponent(new ComponentName("com.gionee.softmanager", "com.gionee.softmanager.MainActivity"));
            sIntentPageList.add(new IntentPage(gioneeIntent, GIONEE));

            //乐视 自启动管理
            Intent letvIntent = new Intent();
            letvIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            sIntentPageList.add(new IntentPage(letvIntent, LETV));

            //乐视 应用保护
            Intent letvGodIntent = new Intent();
            letvGodIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.BackgroundAppManageActivity"));
            sIntentPageList.add(new IntentPage(letvGodIntent, LETV_GOD));

            //酷派 自启动管理
            Intent coolpadIntent = new Intent();
            coolpadIntent.setComponent(new ComponentName("com.yulong.android.security", "com.yulong.android.seccenter.tabbarmain"));
            sIntentPageList.add(new IntentPage(coolpadIntent, COOLPAD));

            //联想 后台管理
            Intent lenovoIntent = new Intent();
            lenovoIntent.setComponent(new ComponentName("com.lenovo.security", "com.lenovo.security.purebackground.PureBackgroundActivity"));
            sIntentPageList.add(new IntentPage(lenovoIntent, LENOVO));

            //联想 后台耗电优化
            Intent lenovoGodIntent = new Intent();
            lenovoGodIntent.setComponent(new ComponentName("com.lenovo.powersetting", "com.lenovo.powersetting.ui.Settings$HighPowerApplicationsActivity"));
            sIntentPageList.add(new IntentPage(lenovoGodIntent, LENOVO_GOD));

            //中兴 自启管理
            Intent zteIntent = new Intent();
            zteIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.autorun.AppAutoRunManager"));
            sIntentPageList.add(new IntentPage(zteIntent, ZTE));

            //中兴 锁屏加速受保护应用
            Intent zteGodIntent = new Intent();
            zteGodIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.setting.ClearAppSettingsActivity"));
            sIntentPageList.add(new IntentPage(zteGodIntent, ZTE_GOD));
        }
        return sIntentPageList;
    }

    protected static String sApplicationName;

    public static String getApplicationName() {
        if (sApplicationName == null) {
            if (!DaemonEnv.sInitialized) return "";
            PackageManager pm;
            ApplicationInfo ai;
            try {
                pm = DaemonEnv.sApp.getPackageManager();
                ai = pm.getApplicationInfo(DaemonEnv.sApp.getPackageName(), 0);
                sApplicationName = pm.getApplicationLabel(ai).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                sApplicationName = DaemonEnv.sApp.getPackageName();
            }
        }
        return sApplicationName;
    }

    /**
     * 处理白名单.
     *
     * @return 弹过框的 IntentWrapper.
     */
    @NonNull
    public static List<IntentPage> whiteListMatters(final Activity a, String reason) {
        sApplicationName = getApplicationName();
        ArrayList<String> mImgList;
        List<IntentPage> showed = new ArrayList<>();
        if (reason == null) reason = "核心服务的持续运行";
        List<IntentPage> intentPageList = getIntentPageList();
        for (final IntentPage iw : intentPageList) {
            mTitle = "";
            mMsg = "";
            mImgList = new ArrayList<>();
            //如果本机上没有能处理这个Intent的Activity，说明不是对应的机型，直接忽略进入下一次循环。
            if (!doesActivityExists(iw)) continue;
            switch (iw.type) {
                case DOZE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        PowerManager pm = (PowerManager) a.getSystemService(Context.POWER_SERVICE);
                        if (pm.isIgnoringBatteryOptimizations(a.getPackageName())) break;
                        new AlertDialog.Builder(a)
                                .setCancelable(false)
                                .setTitle("需要忽略 " + sApplicationName + " 的电池优化")
                                .setMessage(reason + "需要 " + sApplicationName + " 加入到电池优化的忽略名单。\n\n" +
                                        "请点击『确定』，在弹出的『忽略电池优化』对话框中，选择『是』。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int w) {
                                        startActivitySafely(a, iw);
                                    }
                                })
                                .show();
//                        showed.add(iw);
                    }
                    break;
                case VIVO_GOD:
                    mTitle = "需要允许 " + sApplicationName + " 的后台运行";
                    mMsg = reason + "需要允许 " + sApplicationName + " 在后台高耗电时运行。\n" +
                            "请点击『去设置』，在弹出的『后台高耗电』中，将 " + sApplicationName + " 对应的开关打开。";
//                    new AlertDialog.Builder(a)
//                            .setCancelable(false)
//                            .setTitle(mTitle)
//                            .setMessage(mMsg)
//                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface d, int w) {
//                                    iw.startActivitySafely(a);
//                                }
//                            })
//                            .show();
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/Vivo/%E5%90%8E%E5%8F%B0%E9%AB%98%E8%80%97%E7%94%B5/%E6%88%AA%E5%B1%8F_20171212_181025.jpg");
                    isDialog = false;
                    showed.add(iw);
                    break;
                case HUAWEI:
                    mTitle = "需要允许 " + sApplicationName + " 自动启动";
                    mMsg = reason + "需要允许 " + sApplicationName + " 的自动启动。\n" +
                            "请点击『去设置』，在弹出的『自启管理』中，将 " + sApplicationName + " 对应的开关打开。";
                    isDialog = false;
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E5%8D%8E%E4%B8%BA/%E8%87%AA%E5%90%AF%E5%8A%A8/1.png");
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E5%8D%8E%E4%B8%BA/%E8%87%AA%E5%90%AF%E5%8A%A8/2.png");
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E5%8D%8E%E4%B8%BA/%E8%87%AA%E5%90%AF%E5%8A%A8/3.png");
                    showed.add(iw);
                    break;
                case HUAWEI_GOD:
                    mTitle = sApplicationName + " 需要加入锁屏清理白名单";
                    mMsg = reason + "需要 " + sApplicationName + " 加入到锁屏清理白名单。\n" +
                            "请点击『去设置』，在弹出的『锁屏清理』列表中，将 " + sApplicationName + " 对应的开关打开。";
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E5%8D%8E%E4%B8%BA/%E9%94%81%E5%B1%8F%E6%B8%85%E7%90%86/Screenshot_20171211-190707.png");
                    isDialog = false;
                    showed.add(iw);
                    break;
                case XIAOMI:
                    mTitle = "需要允许 " + sApplicationName + " 的自启动";
                    mMsg = reason + "需要 " + sApplicationName + " 加入到自启动白名单。\n" +
                            "请点击『去设置』，在弹出的『自启动管理』中，将 " + sApplicationName + " 对应的开关打开。";
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E5%B0%8F%E7%B1%B3/%E8%87%AA%E5%90%AF%E5%8A%A8/Screenshot_2017-12-12-17-54-11-708_com.miui.secur.png");
                    isDialog = false;
                    showed.add(iw);
                    break;
                case XIAOMI_GOD:
                    mTitle = "需要关闭 " + sApplicationName + " 的神隐模式";
                    mMsg = reason + "需要关闭 " + sApplicationName + " 的神隐模式。\n" +
                            "请点击『去设置』，在弹出的 " + sApplicationName + " 神隐模式设置中，选择『无限制』，然后选择『允许定位』。";
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E5%B0%8F%E7%B1%B3/%E7%A5%9E%E9%9A%90%E6%A8%A1%E5%BC%8F/Screenshot_2017-12-12-17-56-29-400_com.miui.power.png");
                    isDialog = false;
                    showed.add(iw);
                    break;
                case MEIZU:
                    mTitle = "需要允许 " + sApplicationName + " 保持后台运行";
                    mMsg = reason + "需要允许 " + sApplicationName + " 保持后台运行。\n" +
                            "请点击『去设置』，在弹出的应用信息界面中，将『通知栏消息』开关更改为『打开』状态，将『桌面悬浮窗』选项更改为『打开』状态，将『后台管理』选项更改为『保持后台运行』。";
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E9%AD%85%E6%97%8F/1.jpg");
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E9%AD%85%E6%97%8F/2.jpg");
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E9%AD%85%E6%97%8F/3.jpg");
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/%E9%AD%85%E6%97%8F/4.jpg");
                    isDialog = false;
                    showed.add(iw);
                    break;
                case OPPO:
                    mTitle = "需要允许 " + sApplicationName + " 的自启动";
                    mMsg = reason + "需要 " + sApplicationName + " 加入到自启动白名单。\n" +
                            "请点击『去设置』，在弹出的『自启动管理』中，将 " + sApplicationName + " 对应的开关打开。";
                    mImgList.add("http://lj-download.oss-cn-hangzhou.aliyuncs.com/Whitelist/OPPO/device-2017-12-13-115615.png");
                    isDialog = false;
                    showed.add(iw);
                    break;

                // 下面都是弹窗的
                case MEIZU_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(getApplicationName() + " 需要在待机时保持运行")
                            .setMessage(reason + "需要 " + getApplicationName() + " 在待机时保持运行。\n\n" +
                                    "请点击『确定』，在弹出的『待机耗电管理』中，将 " + getApplicationName() + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case ZTE_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(getApplicationName() + " 需要加入锁屏清理白名单")
                            .setMessage(reason + "需要 " + getApplicationName() + " 加入到锁屏清理白名单。\n\n" +
                                    "请点击『确定』，在弹出的『锁屏清理』列表中，将 " + getApplicationName() + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case ZTE:
                case OPPO_OLD:
                case LETV:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + sApplicationName + " 的自启动")
                            .setMessage(reason + "需要 " + sApplicationName + " 加入到自启动白名单。\n\n" +
                                    "请点击『确定』，在弹出的『自启动管理』中，将 " + sApplicationName + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case SAMSUNG_L:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + sApplicationName + " 的自启动")
                            .setMessage(reason + "需要 " + sApplicationName + " 在屏幕关闭时继续运行。\n\n" +
                                    "请点击『确定』，在弹出的『智能管理器』中，点击『内存』，选择『自启动应用程序』选项卡，将 " + sApplicationName + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case SAMSUNG_M:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + sApplicationName + " 的自启动")
                            .setMessage(reason + "需要 " + sApplicationName + " 在屏幕关闭时继续运行。\n\n" +
                                    "请点击『确定』，在弹出的『电池』页面中，点击『未监视的应用程序』->『添加应用程序』，勾选 " + sApplicationName + "，然后点击『完成』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case COOLPAD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + sApplicationName + " 的自启动")
                            .setMessage(reason + "需要允许 " + sApplicationName + " 的自启动。\n\n" +
                                    "请点击『确定』，在弹出的『酷管家』中，找到『软件管理』->『自启动管理』，取消勾选 " + sApplicationName + "，将 " + sApplicationName + " 的状态改为『已允许』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case GIONEE:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(sApplicationName + " 需要加入应用自启和绿色后台白名单")
                            .setMessage(reason + "需要允许 " + sApplicationName + " 的自启动和后台运行。\n\n" +
                                    "请点击『确定』，在弹出的『系统管家』中，分别找到『应用管理』->『应用自启』和『绿色后台』->『清理白名单』，将 " + sApplicationName + " 添加到白名单。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case LETV_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要禁止 " + sApplicationName + " 被自动清理")
                            .setMessage(reason + "需要禁止 " + sApplicationName + " 被自动清理。\n\n" +
                                    "请点击『确定』，在弹出的『应用保护』中，将 " + sApplicationName + " 对应的开关关闭。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case LENOVO:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要允许 " + sApplicationName + " 的后台运行")
                            .setMessage(reason + "需要允许 " + sApplicationName + " 的后台自启、后台 GPS 和后台运行。\n\n" +
                                    "请点击『确定』，在弹出的『后台管理』中，分别找到『后台自启』、『后台 GPS』和『后台运行』，将 " + sApplicationName + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
                case LENOVO_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle("需要关闭 " + sApplicationName + " 的后台耗电优化")
                            .setMessage(reason + "需要关闭 " + sApplicationName + " 的后台耗电优化。\n\n" +
                                    "请点击『确定』，在弹出的『后台耗电优化』中，将 " + sApplicationName + " 对应的开关关闭。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    startActivitySafely(a, iw);
                                }
                            })
                            .show();
                    break;
            }
            iw.setmTitle(mTitle);
            iw.setmMsg(mMsg);
            iw.setmImgList(mImgList);
        }
        return showed;
    }

    /**
     * 防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
     */
    public static void onBackPressed(Activity a) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        a.startActivity(launcherIntent);
    }

    /**
     * 判断本机上是否有能处理当前Intent的Activity
     */
    protected static boolean doesActivityExists(IntentPage intentPage) {
        if (!DaemonEnv.sInitialized) return false;
        PackageManager pm = DaemonEnv.sApp.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intentPage.getIntent(), PackageManager.MATCH_DEFAULT_ONLY);
        return (Build.MANUFACTURER.equals("OPPO") && intentPage.getType() == OPPO) || (list != null && list.size() > 0);
    }

    /**
     * 安全地启动一个Activity
     */
    public static void startActivitySafely(Activity activityContext, IntentPage intentPage) {
        try {
            activityContext.startActivity(intentPage.getIntent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Intent intent;
    protected int type;

    protected IntentWrapperPager(Intent intent, int type) {
        this.intent = intent;
        this.type = type;
    }

}
