package com.example.handsgivingt;

import org.json.JSONObject;

public class UserInfo {
    private JSONObject datas;

    public UserInfo() {
    }

    public void setDatas(JSONObject datas) {
        this.datas = datas;
    }

    public JSONObject getDatas() {
        return datas;
    }
}
