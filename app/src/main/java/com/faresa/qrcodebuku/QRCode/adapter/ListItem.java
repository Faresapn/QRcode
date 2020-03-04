package com.faresa.qrcodebuku.QRCode.adapter;

public class ListItem {
    private long _id;
    private String code;
    private String type;

    public ListItem(long _id, String code, String type){
        this._id = _id;
        this.code = code;
        this.type = type;
    }
    public long get_id(){
        return _id;
    }
    public String getCode(){
        return code;
    }
    public String getType(){
        return type;
    }
}
