package thuglife.myweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by avirudhtheraja on 8/22/15.
 */
public class CustomAdapter extends BaseAdapter implements Filterable {

    private ArrayList<String> locations = new ArrayList<>();
    private Context con;

    public CustomAdapter(Context context){
        con = context;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint != null) {
                    if(locations.size() != 0)
                        locations.clear();
                    locations = getList(constraint.toString());
                    results.count = locations.size();
                    results.values = locations;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    locations = (ArrayList<String>)results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    private ArrayList<String> getList(String input){
        try {
            String link = "http://autocomplete.wunderground.com/aq?query="+URLEncoder.encode(input,"UTF-8");
            JSONObject obj = new JSONObject(JSONParser.parse(link));
            JSONArray arr = obj.getJSONArray("RESULTS");
            for(int i = 0; i != arr.length();i++){
                locations.add(arr.getJSONObject(i).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) con
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list, parent, false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.textview_list);
        tv.setText(getItem(position).toString());

        return convertView;
    }

}
