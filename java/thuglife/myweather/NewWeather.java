package thuglife.myweather;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class NewWeather extends Activity {

    ListView lv;
    ArrayList<Weather> wee;
    private String loc;
    String[] days = new String[5];
    long time;
    Weather curWeather; //used for info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_weather);

        loc = getIntent().getExtras().getString("location");
        time = getIntent().getExtras().getLong("time");
        curWeather = (Weather) getIntent().getExtras().get("weather");
        new getForecast().execute();

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
        cal.setTimeInMillis(time);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int[] intDays = getDays(day);
        for(int i = 0; i < intDays.length;i++)
            days[i]=getDay(intDays[i]);

        lv = (ListView)findViewById(R.id.listView);

    }

    private int[] getDays(int day){
        int[] days = new int[5];
        int j = 0;
        for(int i = 1; i != 6;i++){
            if(day+i <= 7)
                days[j]=day+i;
            else
                days[j]=day+i-7;
            j++;
        }

        return days;

    }

    private String getDay(int day){
        switch (day){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class getForecast extends AsyncTask<Void,ArrayList<Weather>,ArrayList<Weather>>{


        @Override
        protected ArrayList<Weather> doInBackground(Void... params) {

            String call = null;
            ArrayList<Weather> weather = new ArrayList<>();
            try {
                call = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+ URLEncoder.encode(loc, "UTF-8")+"&units=metric&cnt=5&APPID=723ad03e71d4dff6c7b3a4afc2190787";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                JSONObject obj = new JSONObject(JSONParser.parse(call));
                JSONArray list = obj.getJSONArray("list");
                for(int i = 0; i != list.length();i++)
                {
                    JSONObject object = list.getJSONObject(i);
                    Weather toBeAdded = new Weather();
                    toBeAdded.setMin_temp(object.getJSONObject("temp").getDouble("min"));
                    toBeAdded.setMax_temp(object.getJSONObject("temp").getDouble("max"));
                    toBeAdded.setCode(object.getJSONArray("weather").getJSONObject(0).getInt("id"));
                    weather.add(toBeAdded);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPostExecute(ArrayList<Weather> weathers) {
            wee = new ArrayList<>(weathers);
            lv.setAdapter(new CustomBaseAdapter(NewWeather.this, days, wee));

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
