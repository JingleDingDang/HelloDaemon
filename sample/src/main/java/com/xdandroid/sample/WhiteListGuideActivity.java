package com.xdandroid.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdandroid.hellodaemon.IntentPage;
import com.xdandroid.hellodaemon.IntentWrapperPager;

import java.util.ArrayList;

public class WhiteListGuideActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvHint;
    private TextView mTvHintTitle;
    private RecyclerView mRecyclerView;
    ImageView imageView;
    private Button mBtn;
    private Button mBtn1;

    private static final String INTENT_KEY_PAGE = "INTENT_KEY_PAGE";
    private ArrayList<IntentPage> mPageList;
    private IntentPage mIntentPage;

    public static void start(Context context, ArrayList<IntentPage> pagers) {
        Intent intent = new Intent(context, WhiteListGuideActivity.class);
        intent.putExtra(INTENT_KEY_PAGE, pagers);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageList = getIntent().getParcelableArrayListExtra(INTENT_KEY_PAGE);
        mIntentPage = mPageList.get(0);
        mPageList.remove(mIntentPage);
        setContentView(R.layout.activity_white_list_guide);
        init();
    }

    private void init() {
        mTvHintTitle = (TextView) findViewById(R.id.tv_hint_title);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        ImageAdapter mAdapter = new ImageAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mBtn = (Button) findViewById(R.id.btn);
        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn.setOnClickListener(this);
        mBtn1.setOnClickListener(this);

        if (mPageList.size() != 0)
            mBtn1.setVisibility(View.VISIBLE);

        mTvHintTitle.setText(mIntentPage.getmTitle());
        mTvHint.setText(mIntentPage.getmMsg());

        mAdapter.setmData(mIntentPage.getmImgList());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn:
                IntentWrapperPager.startActivitySafely(this, mIntentPage);
                break;
            case R.id.btn1:
                WhiteListGuideActivity.start(this, mPageList);
                finish();
                break;
        }
    }

}
