package com.skies.utils;

import com.alibaba.fastjson.JSONObject;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import org.geotools.geojson.geom.GeometryJSON;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 2017-6-23.
 */
public class GeomtryUtils {

    private GeometryFactory factory = GeometryFactory.getInstance();
    private final static double EARTH_RADIUS = 6378.137;// 地球半径


    public Geometry buildGeo(String str) {
        return factory.buildGeo(str);
    }


    /*public static void main(String[] args) {
        String ss = "POLYGON ((108.92243546095574 34.27648724109814, 108.92230531949508 34.27648158111033, 108.91230531949508 34.23648158111033, 108.92430531949508 34.25648158111033, 108.92243546095574 34.27648724109814))";
//        String ss = "LINESTRING (108.92243546095574 34.27648724109814, 108.92230531949508 34.27648158111033)";
//        String ss = "{\"type\":\"LineString\",\"coordinates\":[[108.92243546095574,34.27648724109814],[108.92230531949508,34.27648158111033]]}";
//        String ss = "{\"type\":\"Polygon\",\"coordinates\":[[[108.92243546095574,34.27648724109814],[108.92230531949508,34.27648158111033],[108.91230531949508,34.23648158111033],[108.92430531949508,34.25648158111033],[108.92243546095574,34.27648724109814]]]}";
//        System.out.println(geoJson2Wkt(ss));
        System.out.println(wkt2GeoJson(ss));
       *//* BuffersTestData bs = new BuffersTestData();
        String line1 = "LINESTRING (108.92230531949508 34.27653818095176,108.92242861140272 34.27653252096741)";
        Geometry g1 = bs.buildGeo(line1);
        //方式(一)
//        Geometry g = g1.buffer(0.00002);
//        System.out.println(g.toString());
        ////方式(二) BufferOP
        BufferOp bufOp = new BufferOp(g1);
        bufOp.setEndCapStyle(BufferOp.CAP_BUTT);
        Geometry bg = bufOp.getResultGeometry(0.00002);
        System.out.println(bg.toString());*//*
    }*/

    /**
     * 将point转化为geomtry格式
     * @param string
     * @return JSONObject
     */
    public static JSONObject getGeomtryByPoint(String string) {
        List<Double> list = new ArrayList<>();
        String[] split = string.split(" ");
        list.add(Double.parseDouble(split[0]));
        list.add(Double.parseDouble(split[1]));
        JSONObject json = new JSONObject();
        json.put( "type", "Point");
        json.put("coordinates",list);
        return json;
    }

    /**
     * 计算两点之间实际距离
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 米
     */
    public static double GetDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        if (a == 0 && b == 0)
            return 0;
        double s = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        // s = Math.round(s * 1) / 1;
        s = s * 1000;
        return Math.floor(s);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 将geometry转化为point格式
     * @param string
     * @return String
     */
    public static String getPointByGeomtry(String string) {
        String str = null;
        JSONObject jsonObject = JSONObject.parseObject(string);
        List<Double> list = (List<Double>)jsonObject.get("coordinates");
        str = list.get(0)+" "+list.get(1);
        return str;
    }

    /**
     * 将WKT转换为geojson
     * @param wkt
     * @return string(geojson格式)
     */
    public static String wkt2GeoJson(String wkt){
        WKTReader wktReader = new WKTReader();
        JSONObject geometry = new JSONObject();
        if(wkt.contains("LINESTRING")){
            LineString lineString = null;
            List<List<Double>> coordinates = new ArrayList<>();
            try {
                lineString = (LineString) wktReader.read(wkt);
                Coordinate[] lineStringCoordinates = lineString.getCoordinates();
                for (Coordinate coor: lineStringCoordinates) {
                    List<Double> c = new ArrayList<>();
                    c.add(coor.x);
                    c.add(coor.y);
                    coordinates.add(c);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            geometry.put("type", "LineString");
            geometry.put("coordinates", coordinates);
        }else if(wkt.contains("POINT")){
            Point point = null;
            try {
                point = (Point) wktReader.read(wkt);
                List<Double> coordinates = new ArrayList<>();
                Coordinate[] pointCoordinates = point.getCoordinates();
                for (Coordinate coor: pointCoordinates) {
                    coordinates.add(coor.x);
                    coordinates.add(coor.y);
                }
                geometry.put("type", "Point");
                geometry.put("coordinates", coordinates);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(wkt.contains("POLYGON")&&!wkt.contains("MULTIPOLYGON")){
            Polygon lineString = null;
            List<List<Double[]>> coordinates = new ArrayList<>();
            try {
                lineString = (Polygon) wktReader.read(wkt);
                Coordinate[] lineStringCoordinates = lineString.getCoordinates();
                List<Double[]> c = new ArrayList<>();
                for (Coordinate coor: lineStringCoordinates) {
                    c.add(new Double[]{coor.x,coor.y});
                }
                coordinates.add(c);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            geometry.put("type", "Polygon");
            geometry.put("coordinates", coordinates);
        } else {
            try {
                WKTReader reader = new WKTReader();
                Geometry geometrys = reader.read(wkt);
                StringWriter writer = new StringWriter();
                GeometryJSON g = new GeometryJSON();
                try {
                    g.write(geometrys,writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                geometry = JSONObject.parseObject(writer.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return geometry.toJSONString();
    }

    /**
     * 获取中心点
     * @param wkt
     * @return string(lon,lat)
     */
    public static String getPointByWkt(String wkt){
        String center = "";
        try {
            WKTReader reader = new WKTReader();
            Geometry geometrys = reader.read(wkt);
            Point centroid = geometrys.getCentroid();
            center =centroid.getX()+","+centroid.getY();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return center;
    }


    /**
     * 将geojson转化为WKE
     * @param geoJson
     * @return String(WKT格式)
     */
    public static String geoJson2Wkt(String geoJson) {
        JSONObject jsonObject = JSONObject.parseObject(geoJson);
        String wkt = null;
        Reader reader = new StringReader(geoJson);
        GeometryJSON gjson = new GeometryJSON();
        try {
            if("LineString".equals(jsonObject.get("type").toString())){
                LineString lineString = gjson.readLine(reader);
                wkt = lineString.toText();
            }else if("Polygon".equals(jsonObject.get("type").toString())){
                Polygon geometry = gjson.readPolygon(reader);
                wkt = geometry.toText();
            }else{
                Point point = gjson.readPoint(reader);
                wkt = point.toText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wkt;
    }

    /**
     * 将线转化为面
     * @param line LINESTRING (108.92230531949508 34.27653818095176,108.92242861140272 34.27653252096741)
     * @param degree 扩充的度数 （1米= 1/100000 度）
     * @return String(WKT格式)
     */
    public static String line2Polygon(String line,double degree){
        GeomtryUtils bs = new GeomtryUtils();
        Geometry g1 = bs.buildGeo(line);
        //方式(一)
//        Geometry g = g1.buffer(0.00002);
//        System.out.println(g.toString());
        ////方式(二) BufferOP
        BufferOp bufOp = new BufferOp(g1);
        bufOp.setEndCapStyle(BufferOp.CAP_BUTT);
        Geometry bg = bufOp.getResultGeometry(degree);
        return bg.toString();
    }

    /**
     * 判断点是否在面内
     * @param  point 点
     * @param geometry  面
     * @return boolean
     */
    public static boolean pointInPolygon(String point,String geometry){
        WKTReader reader = new WKTReader();
        try {
            Geometry points = reader.read(point);
            Geometry poly = reader.read(geometry);
            poly.contains(points); //返回true或false
            return poly.contains(points);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除重复数据
     * @param wkt
     * @return String
     */
    public static String removeDataByWkt(String wkt) {
        WKTReader wktReader = new WKTReader();
        if(wkt.contains("LINESTRING")){
            LineString lineString = null;
            try {
                lineString = (LineString) wktReader.read(wkt);
                Coordinate[] lineStringCoordinates = lineString.getCoordinates();
                List<Coordinate> asList = Arrays.asList(lineStringCoordinates);
                List<Coordinate> list = new ArrayList<>(asList);
                for (int i=0;i<list.size()-1;i++){
                    Coordinate coor = list.get(i);
                    Coordinate coor1 = list.get(i+1);
                    if(coor.x==coor1.x&&coor.y==coor1.y){
                        list.remove(i);
                        i--;
                    }
                }
                String line = "LINESTRING (";
                for (int i=0;i<list.size();i++) {
                    line+=list.get(i).x+" "+list.get(i).y;
                    if(i<list.size()-1){
                        line+=",";
                    }
                }
                line+=")";
                lineString = (LineString)wktReader.read(line);
                wkt = lineString.toText();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return wkt;
    }
}
