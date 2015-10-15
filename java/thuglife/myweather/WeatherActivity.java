package thuglife.myweather;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class WeatherActivity extends TabActivity {

    Weather curWeather;
    ImageView iv;
    ImageView icon;
    double latitude;
    double longitude;
    long localTime;
    TextView tv;
    TextView temp;
    TextView var;
    TextView time;
    private String loc;
    Intent firstTab;
    Intent secondTab;
    public static String Celsius_TAG = " °C";
    public ProgressDialog pd;
    public static final String DATE_FORMAT_NOW = "hh:mm";
    public String timeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_weather);
        pd = new ProgressDialog(WeatherActivity.this);
        iv = (ImageView)findViewById(R.id.imageView4);
        icon = (ImageView)findViewById(R.id.imageView6);
        tv = (TextView)findViewById(R.id.textView_location);
        temp = (TextView)findViewById(R.id.textView_temp);
        var = (TextView)findViewById(R.id.textView_minMax);
        time = (TextView)findViewById(R.id.textView_time);

        Bundle bun = getIntent().getExtras();
        loc = bun.getString("location");

        Data check = CacheData.check(loc,getApplicationContext());
        if(check == null)
        {
            Toast.makeText(getApplicationContext(),"No cached data found, fetching data",Toast.LENGTH_LONG).show();
            new getData().execute();
        }
        else
        {
            try {
                curWeather = (Weather)check.weather.clone();
                Toast.makeText(getApplicationContext(),"Cached data found, loading cached data",Toast.LENGTH_LONG).show();
                new getLocalTime().execute();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    private void storeData(){
        try {
            Data toBeStored = new Data(loc,System.currentTimeMillis(),curWeather);
            boolean done = CacheData.storeData(toBeStored,getApplicationContext());
            Log.d("Avi","Function called from Weather Activity, returned "+done);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void setIntent(){
        firstTab = new Intent(WeatherActivity.this,NewWeather.class);
        firstTab.putExtra("location", loc);
        firstTab.putExtra("time", localTime);
        firstTab.putExtra("weather", curWeather);
        secondTab = new Intent(WeatherActivity.this,SecondTabActivity.class);
    }

    private void updateDisplay(){
        String temperature = curWeather.getTemperature() + "";
        String humid = curWeather.getHumidity() + "";
        String atmosPressure = curWeather.getPressure() + "";
        String windSpeed = curWeather.getWindSpeed() + "";
        String min = curWeather.getMin_temp() + "";
        String max = curWeather.getMax_temp() + "";

        tv.setText(loc);
        temp.setText(temperature + Celsius_TAG);
        var.setText(min + CustomBaseAdapter.CelsiusSymbol_TAG + " | " + max + CustomBaseAdapter.CelsiusSymbol_TAG);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
        time.setText("Local time is: " + sdf.format(new Date(localTime)) + timeId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
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


    public class getData extends AsyncTask<Void,Void,Weather>{

        @Override
        protected void onPreExecute() {
            pd.setMessage("Loading weather data");
            pd.show();
        }

        @Override
        protected Weather doInBackground(Void...params) {
            String call = null;
            try {
                call = "http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(loc, "UTF-8") + "&units=metric&APPID=723ad03e71d4dff6c7b3a4afc2190787";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Weather wee = new Weather();

            try {
                JSONObject obj = new JSONObject(JSONParser.parse(call));
                wee.setDescription(obj.getJSONArray("weather").getJSONObject(0).get("description").toString());
                wee.setTemperature(obj.getJSONObject("main").getDouble("temp"));
                wee.setMax_temp(obj.getJSONObject("main").getDouble("temp_max"));
                wee.setMin_temp(obj.getJSONObject("main").getDouble("temp_min"));
                wee.setHumidity(obj.getJSONObject("main").getDouble("humidity"));
                wee.setPressure(obj.getJSONObject("main").getDouble("pressure"));
                wee.setWindSpeed(obj.getJSONObject("wind").getDouble("speed"));
                wee.setCode(obj.getJSONArray("weather").getJSONObject(0).getInt("id"));
                wee.setName(obj.getString("name"));
                wee.setTime(obj.getLong("dt") * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wee;
        }



        @Override
        protected void onPostExecute(Weather wee) {
            if(wee != null){
                try {
                    curWeather = (Weather)wee.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                storeData();
                new getLocalTime().execute();
            }
        }
    }

    public int imageId(long time){
        Calendar cal = GregorianCalendar.getInstance();
        cal.clear();
        cal.setTimeInMillis(time);
        cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour < 12)
            timeId = " AM";
        else
            timeId = " PM";
        if(hour >= 20 || hour <= 5)
            return R.drawable.night_background;
        else if(hour >= 9 && hour <= 16)
            return R.drawable.sunny_background;
        else
            return R.drawable.morning_evening_background;
    }

    public int iconImageId(int code){
        switch (code){
            case 2:
                return R.drawable.thunderstorm;
            case 3:
                return R.drawable.drizzle;
            case 5:
                return R.drawable.rain;
            case 6:
                return R.drawable.snow;
            case 7:
                return R.drawable.atmoshphere;
            case 8:
                return R.drawable.clouds;
            case 9:
                return R.drawable.thunderstorm;
            default:
                return R.drawable.clouds;
        }
    }

    public class getLocalTime extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPostExecute(Void aVoid) {
            TabHost tabHost = getTabHost();
            TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
            TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
            tab1.setIndicator("Weather");
            tab2.setIndicator("Info");

            setIntent();
            tab1.setContent(firstTab);
            tab2.setContent(secondTab);

            tabHost.addTab(tab1);
            tabHost.addTab(tab2);
            iv.setImageResource(imageId(localTime));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            icon.setImageResource(iconImageId(curWeather.getCode()));
            icon.setScaleType(ImageView.ScaleType.FIT_XY);
            updateDisplay();
            pd.hide();
        }

        @Override
        protected void onPreExecute() {
            if(!pd.isShowing())
                pd.show();
        }

        @Override
        protected Void doInBackground(Void...params) {
            try {
                String locationUri = "https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(loc,"UTF-8");
                JSONObject obj = new JSONObject(JSONParser.parse(locationUri));
                JSONArray arr = obj.getJSONArray("results");
                JSONObject object = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                latitude = object.getDouble("lat");
                longitude = object.getDouble("lng");

                String timeUri = "http://api.timezonedb.com/?lat="+latitude+"&lng="+longitude+"&format=json&key=KSPSJDX2L2U2";
                JSONObject result = new JSONObject(JSONParser.parse(timeUri));
                localTime = result.getLong("timestamp")*1000;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
