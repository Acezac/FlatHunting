package ImagePager;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.example.AndroidMVC.CommonUtilities.SERVER_URL; 
import com.example.AndroidMVC.ImageLoader;
import com.example.AndroidMVC.JSONParser;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
 
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> imgs = new ArrayList<String>();
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private String HouseID;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private String url_all_events = SERVER_URL + "get_Images.php";
 
    public ImageAdapter(Context c) {
        mContext = c;
    }
 
    public int getCount() {
        return getImgs().size();
    }
 
    public Object getItem(int position) {
        return getImgs().get(position);
    }
 
    public long getItemId(int position) {
        return 0;
    }
    
    ImageLoader imageLoader;
    // Create a new ImageView for each item referenced by the Adapter
    @SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
        
        ImageView imageView;
        if (convertView == null) {  // If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
       
        imageLoader.DisplayImage(String.valueOf(getImgs().get(position)), imageView, 0);
        return imageView;
    }
 
   
    
    public void downloadImages(String eid){
    	HouseID = eid;
    	StrictMode.setThreadPolicy(policy);
    	params.add(new BasicNameValuePair("eid", HouseID));
    	params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));
    	JSONParser jParser = new JSONParser();
    	JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);
    	
    	JSONArray events = null;
        
        imageLoader=new ImageLoader(mContext);
        
        try {
			events = json.getJSONArray("images");
			
			 for (int i = 0; i < events.length(); i++) {
				
		            JSONObject c = events.getJSONObject(i);
		            getImgs().add(c.getString("directory"));
		            
		        }
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

	public ArrayList<String> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<String> imgs) {
		this.imgs = imgs;
	}
 
}