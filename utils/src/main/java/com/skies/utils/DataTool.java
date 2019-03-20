package com.skies.utils;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SKIES on 2018/7/20.
 */
public class DataTool {

           public static String returnType(int num, String flag, String str) {
            JSONObject jsonObject = new JSONObject();
            if (num > 0 && "200".equals(flag)) {
                jsonObject.put("message", str + "成功！");
            } else if (num == 0) {
                jsonObject.put("message", "数据不存在！");
            } else if ("500".equals(flag)) {
                jsonObject.put("message", str + "异常！");
            } else if ("401".equals(flag)) {
                jsonObject.put("message", str);
            } else {
                jsonObject.put("message", str + "失败!");

        }
               jsonObject.put("code", flag);
        return jsonObject.toString();
    }

    /**
     * 生成数据ID17位
     * 然后加上本身对象ID1位
     *
     * @return
     */
    public static String getPointID() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String str = sdf.format(date).toString();
        int num = (int) (Math.random() * 10000);
        if (num < 1000) {
            num = num + 1000;
        }
        str = str + num;
        return str;
    }

    //添加数据默认进草稿箱
    public static String ispublish(String rolename) {

//        if(rolename.indexOf("管理员")>-1){
//            return "1";
//        }else{
//            return "0";
//        }
        return "0";
    }

    public static String jsonFormate(String json) {
        json = json.replace("\\\\", "/")
                .replace("\\", "").replace("\"{", "{")
                .replace("}\"", "}").replace("]\"", "]")
                .replace("\"[", "[").replace("//", "/")
                .replace("/\"","\"").replace("\\\"","\"");
        return json;
    }

    public static double doubleFormate(double number) {
        BigDecimal bg = new BigDecimal(number);
        double f1 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static int timeFormate2(double number) {
        int i = (int) (number * 60 * 60);
        return i;
    }

    public static String timeFormate(int time) {
        String str = "";
        int minAll = (int)time/60;
        int s = time - minAll * 60;
        int h = (int)minAll/60;
        int m = minAll - h *60;
        if (h == 0) {
            if (m == 0) {
                str = s + "s";
            } else {
                str = m + "m" + s + "s";
            }
        } else {
            str = h + "h" + m + "m" + s + "s";
        }
        return str;
    }

    public static String distanceFormare(double diatance) {
        return diatance + "NM";
    }

    public  static double StringToDouble(String str) {
        if (str.indexOf("NM") > -1) {
            str = str.replace("NM", "");
            double v = Double.parseDouble(str);
            return v;
        }
        if (str.indexOf("s") > -1) {
            double v = 0;
            String[] split = str.split("[a-z]");
            if(split.length==1){
                v = Double.parseDouble(split[0]);
            }else  if(split.length==2){
                v = Double.parseDouble(split[0])*60+ Double.parseDouble(split[1]);
            }else  if(split.length==3){
                v = Double.parseDouble(split[0])*60*60+ Double.parseDouble(split[1])*60+ Double.parseDouble(split[2]);
            }
            return v;
        }
        return Double.parseDouble(str);
    }

    public static String degreeFormate(String longitude, String latitude) {
        double d = Double.parseDouble(longitude);
        int a = (int)d;
        double b1 = (d - a)* 60;
        int b = (int)b1;
        int c = (int)((b1-b)*60);
        String str1 = "E"+a+"°"+b+"′"+c+"″";
        double e = Double.parseDouble(longitude);
        int f = (int)d;
        double g1 = (e - f)* 60;
        int g = (int)g1;
        int h = (int)((g1-g)*60);
        String str2 = "N"+f+"°"+g+"′"+h+"″";
        return str2+" \r\n "+str1;
    }

    public static String dataRounding(String data){
        if(StringUtils.isBlank(data)){
            data = "0";
        }
        double v = Double.parseDouble(data);
        return Math.round(v)+"";
    }
}
