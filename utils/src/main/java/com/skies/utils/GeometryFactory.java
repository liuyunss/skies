package com.skies.utils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Created by Admin on 2017-6-5.
 */
public class GeometryFactory {

    private WKTReader reader;

    private static GeometryFactory instance = null;

    public static synchronized GeometryFactory getInstance(){
        if(instance==null){
            instance = new GeometryFactory();
        }
        return instance;
    }

    public void getReader(){
        reader = new WKTReader();
    }

    public Geometry buildGeo(String str){
        try {
            if(reader==null){
                reader = new WKTReader();
            }
            return reader.read(str);
        } catch (ParseException e) {
            throw new RuntimeException("buildGeometry Error",e);
        }
    }
}
