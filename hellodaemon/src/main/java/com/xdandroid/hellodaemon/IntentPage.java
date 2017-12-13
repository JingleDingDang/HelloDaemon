package com.xdandroid.hellodaemon;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cx on 2017/12/12.
 */

public class IntentPage implements Parcelable {

    protected Intent intent;
    protected int type;

    private String mTitle;
    private String mMsg;
    private ArrayList<String> mImgList = new ArrayList<>();

    private boolean isDialog = true;

    public IntentPage(Intent intent, int type) {
        this.intent = intent;
        this.type = type;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmMsg() {
        return mMsg;
    }

    public void setmMsg(String mMsg) {
        this.mMsg = mMsg;
    }

    public ArrayList<String> getmImgList() {
        return mImgList;
    }

    public void setmImgList(ArrayList<String> mImgList) {
        this.mImgList = mImgList;
    }

    public void addmImgList(ArrayList<String> mImgList) {
        this.mImgList.addAll(mImgList);
    }

    public boolean isDialog() {
        return isDialog;
    }

    public void setDialog(boolean dialog) {
        isDialog = dialog;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.intent, flags);
        dest.writeInt(this.type);
        dest.writeString(this.mTitle);
        dest.writeString(this.mMsg);
        dest.writeStringList(this.mImgList);
        dest.writeByte(this.isDialog ? (byte) 1 : (byte) 0);
    }

    protected IntentPage(Parcel in) {
        this.intent = in.readParcelable(Intent.class.getClassLoader());
        this.type = in.readInt();
        this.mTitle = in.readString();
        this.mMsg = in.readString();
        this.mImgList = in.createStringArrayList();
        this.isDialog = in.readByte() != 0;
    }

    public static final Creator<IntentPage> CREATOR = new Creator<IntentPage>() {
        @Override
        public IntentPage createFromParcel(Parcel source) {
            return new IntentPage(source);
        }

        @Override
        public IntentPage[] newArray(int size) {
            return new IntentPage[size];
        }
    };
}
