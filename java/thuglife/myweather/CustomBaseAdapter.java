package thuglife.myweather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by avirudhtheraja on 8/22/15.
 */
public class CustomBaseAdapter extends BaseAdapter {

    private Context con;
    private String[] arr;
    ArrayList<Weather> weather;
    public static String CelsiusSymbol_TAG = " °";

    public CustomBaseAdapter(Context context, String[] days,ArrayList<Weather> wee){
        con = context;
        arr = days;
        weather = new ArrayList<>(wee);
        Log.d("Avi",weather.size()+"");

    }

    @Override
    public int getCount() {
        return arr.length;
    }

    @Override
    public Object getItem(int position) {
        return arr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getImageId(int code){
        switch (code/100){
            case 2:
                return R.drawable.thunderstom_list;
            case 3:
                return R.drawable.drizzle_list;
            case 5:
                return R.drawable.rain_list;
            case 6:
                return R.drawable.snow_list;
            case 7:
                return R.drawable.atmoshpere_list;
            case 8:
                return R.drawable.clouds_list;
            case 9:
                return R.drawable.extreme_list;
            default:
                return R.drawable.test_icon;

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) con
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.forecast_list, parent, false);
        }

        if(weather != null && weather.size() != 0) {
            TextView day = (TextView) convertView.findViewById(R.id.textView_day);
            if(position != 0)
                day.setText(arr[position]);
            else
                day.setText("Tomorrow");
            TextView temp = (TextView) convertView.findViewById(R.id.textView_temp);
            double min_temp = weather.get(position).getMin_temp();
            double max_temp = weather.get(position).getMax_temp();
            temp.setText(min_temp + CelsiusSymbol_TAG + " | " + max_temp + WeatherActivity.Celsius_TAG);
            ImageView iv = (ImageView)convertView.findViewById(R.id.imageView5);
            iv.setImageResource(getImageId(weather.get(position).getCode()));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        return convertView;
    }
}
