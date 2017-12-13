package com.xdandroid.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xdandroid.hellodaemon.DaemonEnv;
import com.xdandroid.hellodaemon.IntentPage;
import com.xdandroid.hellodaemon.IntentWrapper;
import com.xdandroid.hellodaemon.IntentWrapperPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                TraceServiceImpl.sShouldStopService = false;
                DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
                break;
            case R.id.btn_white:
                IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行");
                break;
            case R.id.btn_stop:
                TraceServiceImpl.stopService();
                break;

            case R.id.btn_guide:
//                MobileInfoUtils.jumpStartInterface(this);

                List<IntentPage> intentPagers = IntentWrapperPager.whiteListMatters(this, "派单服务的持续运行");
                if (intentPagers.size() == 0) {
                    Toast.makeText(this, "没有能打开的页面", Toast.LENGTH_LONG).show();
                } else {
                    WhiteListGuideActivity.start(this, new ArrayList<IntentPage>(intentPagers));
                }
                break;
        }
    }

    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    public void onBackPressed() {
        IntentWrapper.onBackPressed(this);
    }
}
