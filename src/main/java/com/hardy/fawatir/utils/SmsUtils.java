package com.hardy.fawatir.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;

public class SmsUtils {

    public static final String FROM_NUMBER = "+212612345678";
    public static final String SID_KEY = ""; // id from the paid service twilio
    public static final String TOKEN_KEY = "";// token from the paid service twilio

    public static void sendSMS(String to, String message){
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message msg = creator(new PhoneNumber("+212" + to), new PhoneNumber(FROM_NUMBER), message).create();
        System.out.println(msg);
    }


}
