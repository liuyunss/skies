package skies.bean;

import org.junit.Test;

/**
 * Created by SKIES on 2019/3/20.
 */
public class EnumDemo {
    enum Day{a,b,c;}

    @Test
    public void test(){
        for (Day day : Day.values()) {
            System.out.println(day.getClass().getCanonicalName());
            System.out.println(day.ordinal());

        }
    }
}
