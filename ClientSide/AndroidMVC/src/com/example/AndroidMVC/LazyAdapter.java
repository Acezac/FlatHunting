package com.example.AndroidMVC;

import static com.example.AndroidMVC.CommonUtilities.SERVER_URL; 
import static com.example.AndroidMVC.CommonUtilities.TAG_EVENTS;
import static com.example.AndroidMVC.CommonUtilities.TAG_MARKED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 
import com.google.android.gcm.GCMRegistrar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

 // Display images in Android ListView. Images are being downloaded asynchronously in the background. 
 // Images are being cached in memory.

@SuppressLint("NewApi")
public class LazyAdapter extends BaseAdapter {
	
	  static class ViewHolder
	    {
	    ImageView iv;
	    }
 
	House_Advert song = new House_Advert();
    private Activity activity;
    //private ArrayList<HashMap<String, String>> data;
    private ArrayList<House_Advert> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private Context context;
    private View vi;
    private JSONParser jParser = new JSONParser();
    private static String url_all_events = SERVER_URL + "get_event_details.php";
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    // getting JSON string from URL
    private String teamName;
	private ProgressDialog pDialog = null;
    
 
    public LazyAdapter(Context c, Activity a, ArrayList<House_Advert> d) {
    	context = c;
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }
 
    public LazyAdapter() {
		// TODO Auto-generated constructor stub
	}

	public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
    	//final ViewHolder holder;
        final ViewHolder holder;
        holder = new ViewHolder();
        vi=convertView;
        if(convertView==null)
        	vi = inflater.inflate(R.layout.list_row, null);
       
        
        TextView title = (TextView)vi.findViewById(R.id.name); // title
        TextView price = (TextView)vi.findViewById(R.id.price); // artist name
        TextView date = (TextView)vi.findViewById(R.id.date); 
        TextView area = (TextView)vi.findViewById(R.id.area);
        TextView textview_id = (TextView)vi.findViewById(R.id.flat_id);
        holder.iv = (ImageView)vi.findViewById(R.id.marked);
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
       
        

        song = data.get(position);
        imageLoader.DisplayImage(song.getImage(), thumb_image, 1);
        // Setting all values in listview
        title.setText(song.getName());
        date.setText(song.getDatePosted());
        price.setText(song.getPrice());
        area.setText(song.getArea());
        teamName = song.getRoomType();
        textview_id.setText(song.getID());
        holder.iv.setTag(song.getID());
        

        if(data.get(position).getMarked() ==1  ){
        	holder.iv.setImageResource(R.drawable.marked);
        }
        else if(data.get(position).getMarked() == 0 ){
        	holder.iv.setImageResource(R.drawable.unmarked);
        }  
        
        holder.iv.setOnClickListener(new OnClickListener() {
        	
          @Override
          public void onClick(View v) {
        	  House_Advert s2 = new House_Advert();
              String s =holder.iv.getTag().toString();
              for(int i =0; i<data.size();i++){
            	  if(data.get(i).getID().equals(s)){
            		  s2 = data.get(i);
            	  }
              }
        	  
              String eid = s2.getID();
        	  String flag = UpdateMarker(1, v, eid);
              new saveFlat(flag, eid).execute();
        	  
              }
      });

        return vi;
    }
    
    public String UpdateMarker(int positionn, View holder, String eid){
         String flat_id = "";
         flat_id = eid;
   
         String flag = "";
         JSONArray events = null;
         params.add(new BasicNameValuePair("eid", flat_id));
         params.add(new BasicNameValuePair("teamName", teamName));
         
         try {
       	  JSONObject json = jParser.makeHttpRequest(url_all_events, "POST", params);
       	 
			events = json.getJSONArray(TAG_EVENTS);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         String mark = "";

         // looping through All Events
         for (int i = 0; i < events.length(); i++) {              
             try {
           	JSONObject c = events.getJSONObject(i);
				mark = c.getString(TAG_MARKED);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                                
        
         }
         
		  if(mark.equals("1") && positionn == 1){

			  ImageView view = (ImageView) holder.findViewById(R.id.marked);
			  view.setImageResource(R.drawable.unmarked);
			  flag = "set_Unmarked";
			  
			  for(int i =0; i<data.size();i++){
            	  if(data.get(i).getID().equals(eid)){
            		  data.get(i).setMarked(0);
            	  }
              }

		  	}	  
		  else if(mark.equals("0") && positionn == 1){

			  ImageView view = (ImageView) holder.findViewById(R.id.marked);
			  view.setImageResource(R.drawable.marked);	
			  flag = "set_marked";
			  
			  for(int i1 =0; i1<data.size();i1++){
            	  if(data.get(i1).getID().equals(eid)){
            		  data.get(i1).setMarked(1);
            	  }
              }
		  }
		  else if(mark.equals("0") && positionn == 0){
			  ImageView view = (ImageView) holder.findViewById(R.id.marked);
		
				  view.setImageResource(R.drawable.unmarked);	
				  
				  for(int i1 =0; i1<data.size();i1++){
	            	  if(data.get(i1).getID().equals(eid)){
	            		  data.get(i1).setMarked(0);
	            	  }
	              }
			  
		  }
		  else if(mark.equals("1") && positionn == 0){
			  ImageView view = (ImageView) holder.findViewById(R.id.marked);
			  
				  view.setImageResource(R.drawable.marked);	
				  
				  for(int i1 =0; i1<data.size();i1++){
	            	  if(data.get(i1).getID().equals(eid)){
	            		  data.get(i1).setMarked(1);
	            	  }
	              }
			  
		  }
		  return flag;
    
    }
    
    class saveFlat extends AsyncTask<String, String, String> {
    	String flag;
    	String eid;

    	
    	public saveFlat(String f, String e){
    		flag = f;
    		eid = e;

    	}
      	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Sending notification..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
        	
        	
      	
      	 
      	  final String regId2 = GCMRegistrar.getRegistrationId(context);
      	  
      	  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		      List<NameValuePair> params = new ArrayList<NameValuePair>();
		    	 params.add(new BasicNameValuePair("eid", eid));
		    	 params.add(new BasicNameValuePair("regId", regId2));
		    	 params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));

		      String url = SERVER_URL + flag + ".php"; 
		      DefaultHttpClient httpClient = new DefaultHttpClient();
		      
		      String paramString = URLEncodedUtils.format(params, "utf-8");
              url += "?" + paramString;
              HttpGet httpGet = new HttpGet(url);

		      try {
		    	  httpClient.execute(httpGet);
		      } catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      }     
        	
        	
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
        }

    }

    
}