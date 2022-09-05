package com.shunyank.split_kar.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.shunyank.split_kar.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static HashMap<String,Boolean> validatePhoneNumber(Context context, String phone) {
        if(phone.isEmpty()){
            Toast.makeText(context, R.string.please_enter_phone_number, Toast.LENGTH_SHORT).show();

            return null;
        }
        else {


            String only10 = "^[6-9]\\d{9}$";
            String withPlus91 = "^(\\+91)[6-9]\\d{9}$";
            String with91 = "^(91)\\d{10}$";

            Pattern only10pattern = Pattern.compile(only10);
            Pattern withPlus91pattern = Pattern.compile(withPlus91);
            Pattern with91pattern = Pattern.compile(with91);
            Matcher only10matcher = only10pattern.matcher(phone);
            Matcher withPlus91matcher = withPlus91pattern.matcher(phone);
            Matcher with91matcher = with91pattern.matcher(phone);

            Map<String,String> printVlaues = new HashMap<String, String>();
            printVlaues.put("only10",""+only10matcher.matches());
            printVlaues.put("withPlus91",""+withPlus91matcher.matches());
            printVlaues.put("with91",""+with91matcher.matches());

            HashMap<String,Boolean> values = new HashMap<String, Boolean>();
            values.put("only10",only10matcher.matches());
            values.put("withPlus91",withPlus91matcher.matches());
            values.put("with91",with91matcher.matches());

//            Log.e(printVlaues);
            return values;
        }

    }
    public static String getRightPhoneNumber(Context context,String number)
    {
         String phone = number;
        HashMap<String,Boolean> phNumber = validatePhoneNumber(context,number);
        if(phNumber!=null) {

            boolean only10 = phNumber.get("only10");
            boolean withPlus91 = phNumber.get("withPlus91");
            boolean with91 = phNumber.get("with91");
            if(only10){

                phone = "91"+phone;

            }
            if (withPlus91) {
                phone = phone.replace("+", "");

            } else if (with91) {
            } else if (!only10 && !withPlus91 && !with91) {
                Log.e("phone out of context",phone);
            }


        }
        return phone;
    }
    public static String ConvertTimeStampToDate(int createdAt) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);


        // we are getting UNIX timestamp and to use in android we have to multiply by 1000
        calendar.setTimeInMillis(createdAt* 1000L);

        //dd=day, MM=month, yyyy=year, hh=hour, mm=minute, ss=second.

        String date = DateFormat.format("dd MMM yy",calendar).toString();
        return date;

    }
   public static String  getFloatValue(float value){
        DecimalFormat df = new DecimalFormat("#.00");

        return  df.format(value);

    }
}
