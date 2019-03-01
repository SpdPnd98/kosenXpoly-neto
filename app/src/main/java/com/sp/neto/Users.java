package com.sp.neto;

import java.util.HashMap;
import java.util.Map;

public class Users {
    Map<String, Object> userInfo = new HashMap<>();
    String id;
    String displayName;
    String mobileNo;
    String email;
    String pass;

    public Users(){}

    public Users(String id, String displayName, String email, String mobileNo, String pass){
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.pass = pass;
        this.userInfo.put("UID",this.id);
        this.userInfo.put("displayName",this.displayName);
        this.userInfo.put("MobileNo",this.mobileNo);
        //this.userInfo.put("Email",this.email);
        this.userInfo.put("Password",this.pass);
    }

    public String getid() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

}
