package com.sobey.cloud.webtv.sdk.web;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 按钮实体<br/>
 * Created by Carlton on 2014/7/10.
 */
public class ButtonEntity implements Parcelable
{
    public static final String IMAGE = "1";
    public static final String TEXT  = "2";
    /**
     * 按钮的类型（1代表图片，2 代表文字）
     */
    private             String type  = "1";
    /**
     * 按钮的文字
     */
    private String text;
    /**
     * 按钮的事件监听
     */
    private String clickListener;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getClickListener()
    {
        return clickListener;
    }

    public void setClickListener(String clickListener)
    {
        this.clickListener = clickListener;
    }

    public ButtonEntity() {}
    public ButtonEntity(String text)
    {
        this.text = text;
    }
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.type);
        dest.writeString(this.text);
        dest.writeString(this.clickListener);
    }

    public ButtonEntity(Parcel in)
    {
        this.type = in.readString();
        this.text = in.readString();
        this.clickListener = in.readString();
    }

    public static Creator<ButtonEntity> CREATOR = new Creator<ButtonEntity>()
    {
        public ButtonEntity createFromParcel(Parcel source) {return new ButtonEntity(source);}

        public ButtonEntity[] newArray(int size) {return new ButtonEntity[size];}
    };
}
