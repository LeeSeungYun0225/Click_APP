package com.espressif.iot_esptouch_demo;

import org.apache.commons.codec.binary.Hex;

public class Device { // 가전기기 클래스임
    private String devName;
    private String devModel;
    private String devType;
    private String devManufact;
    private boolean isEmpty;
    private int devID; // DB 테이블에 있는 데이터
    private String comment;


    public Device getDevice()
    {
        return this;
    }

    public Device() {
        devName = "";
        devType = "";
        devManufact = "";
        isEmpty = true; // 비어있으면 true 차있으면 false
        devID = -99;
        devModel = "";
        comment = "";
    }

    public void initDv()
    {
        devName = "";
        devType = "";
        devManufact = "";
        isEmpty = true; // 비어있으면 true 차있으면 false
        devID = -99;
        devModel = "";
        comment = "";
    }

    public void setdevName(String dname)
    {
        devName = dname;
    }

    public void setdevModel(String model)
    {
        devModel = model;
    }
    public String getdevModel()
    {
        return devModel;
    }
    public void setComment(String cmt)
    {
        comment = cmt;
    }
    public String getComment()
    {
        return comment;
    }
    public void setdevType(String devtype)
    {
        devType = devtype;
    }
    public void setdevManufact(String dmanu)
    {
        devManufact = dmanu;
    }
    public void setisEmpty(boolean isemp)
    {
        isEmpty = isemp;
    }
    public void setdevID(int dID)
    {
        devID = dID;
    }
    public int getdevID()
    {
        return devID;
    }
    public String getdevName() {

        return devName;
    }

    public String getdevType()
    {
        return devType;
    }
    public String getdevManufact()
    {
        return devManufact;
    }
    public boolean getisEmpty()
    {
        return isEmpty;
    }
}
