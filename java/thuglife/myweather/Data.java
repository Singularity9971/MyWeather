package thuglife.myweather;

import java.io.Serializable;


public class Data implements Serializable{
    String location;
    long timestamp;
    Weather weather;

    Data(String loc, long time, Weather wee) throws CloneNotSupportedException {
        location = loc;
        timestamp = time;
        weather = (Weather) wee.clone();
    }

}
