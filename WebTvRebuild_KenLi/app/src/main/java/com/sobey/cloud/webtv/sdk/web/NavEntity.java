package com.sobey.cloud.webtv.sdk.web;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 导航实体<br/>
 * Created by Carlton on 2014/7/10.
 */
public class NavEntity implements Parcelable
{
    /**
     * Id
     */
    private String       id;
    /**
     * 导航的标题
     */
    private String       title;
    /**
     * 页面的url
     */
    private String       url;
    /**
     * 左边的按钮
     */
    private ButtonEntity leftButton;
    /**
     * 右边的按钮
     */
    private ButtonEntity rightButton;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public ButtonEntity getLeftButton()
    {
        return leftButton;
    }

    public void setLeftButton(ButtonEntity leftButton)
    {
        this.leftButton = leftButton;
    }

    public ButtonEntity getRightButton()
    {
        return rightButton;
    }

    public void setRightButton(ButtonEntity rightButton)
    {
        this.rightButton = rightButton;
    }

    public NavEntity()
    {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeParcelable(this.leftButton, 0);
        dest.writeParcelable(this.rightButton, 0);
    }

    private NavEntity(Parcel in)
    {
        this.id = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.leftButton = in.readParcelable(ButtonEntity.class.getClassLoader());
        this.rightButton = in.readParcelable(ButtonEntity.class.getClassLoader());
    }

    public static Creator<NavEntity> CREATOR = new Creator<NavEntity>()
    {
        public NavEntity createFromParcel(Parcel source) {return new NavEntity(source);}

        public NavEntity[] newArray(int size) {return new NavEntity[size];}
    };
}
