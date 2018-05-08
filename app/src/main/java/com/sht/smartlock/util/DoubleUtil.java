package com.sht.smartlock.util;

import java.text.DecimalFormat;

/**
 * Created by chenjl on 2015/6/18.
 */
public class DoubleUtil {
    public static String getDoubleDecimal(double value){
        DecimalFormat df = new DecimalFormat("#0.##");
        return df.format(value);
    }

    public static String getDoubleDecimal1(double value){
        DecimalFormat df = new DecimalFormat("######0.0");
        return df.format(value);
    }

    public static String getDoubleDecimal2(double value){
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(value);
    }
}
