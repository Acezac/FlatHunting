package com.example.AndroidMVC;

import static com.example.AndroidMVC.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.AndroidMVC.CommonUtilities.EXTRA_MESSAGE;
import static com.example.AndroidMVC.CommonUtilities.SERVER_URL; 
import static com.example.AndroidMVC.CommonUtilities.SENDER_ID;
import static com.example.AndroidMVC.CommonUtilities.TAG_AREA;
import static com.example.AndroidMVC.CommonUtilities.TAG_NAME;
import static com.example.AndroidMVC.CommonUtilities.TAG_DATE_POSTED;
import static com.example.AndroidMVC.CommonUtilities.TAG_EVENTS;
import static com.example.AndroidMVC.CommonUtilities.TAG_IMAGE;
import static com.example.AndroidMVC.CommonUtilities.TAG_SUCCESS;
import static com.example.AndroidMVC.CommonUtilities.TAG_EID;
import static com.example.AndroidMVC.CommonUtilities.TAG_PRICE;
import static com.example.AndroidMVC.CommonUtilities.TAG_MARKED;
import java.util.ArrayList;
import java.util.List;
import menuListeners.itemSelectedListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 
import com.example.AndroidMVC.R;
import com.google.android.gcm.GCMRegistrar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//This file contains the activity that shows the list of flats

@SuppressLint("NewApi")
public class AllEventsActivity extends itemSelectedListener {
	
	 // get the list element
	 private ListView list;
	 
	 // instantiate a LazyAdapter object
	 private LazyAdapter adapter;
	 private JSONParser jParser = new JSONParser();
	 private ArrayList<House_Advert> flatList = new ArrayList<House_Advert>();
	 private String url_all_events = SERVER_URL + "get_all_events.php";
	 private AsyncTask<Void, Void, Void> mRegisterTask;
	 private JSONArray events = null;  
	 private ProgressDialog pDialog_loadList = null;
	 private String id;
	 final String model = Build.MODEL.toString();
	 private String kind_of_receiver;
	 private String flat_position;
	 private String teamName;
	 private Context cxt;
	 private Activity act;
 
    @Override 
    public void onCreate(Bundle savedInstanceState) {
    	    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);
        // context
        cxt = this;
        // activity
        act = this;
        
        Bundle b = getIntent().getExtras();
        kind_of_receiver = b.getString("kind");
        teamName = b.getString("teamName");
       
        if(getIntent().hasExtra("saved_url")){
        	url_all_events =  b.getString("saved_url");
        }      
        
        list=(ListView)findViewById(R.id.list);
        // set the choice mode to single. therefore when the user tap on an list item it is displayed as selected
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set 
        GCMRegistrar.checkManifest(this);
        registerReceiver(mHandleMessageReceiver,new IntentFilter(DISPLAY_MESSAGE_ACTION));
     
        final String regId = GCMRegistrar.getRegistrationId(this);
     
        if (regId.equals("")) {
          // Automatically registers application on startup.
    	  GCMRegistrar.register(this, SENDER_ID);
        } else {
          // Device is already registered on GCM, check server.
          if (GCMRegistrar.isRegisteredOnServer(this)) {
        	  if(!getIntent().hasExtra("saved_url") && !getIntent().hasExtra("noR")){
         		 ServerUtilities.register(cxt, kind_of_receiver, teamName, regId);
                 Toast.makeText(getApplicationContext(), "Already registered with GCM ", Toast.LENGTH_SHORT).show();
         	 }
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.

                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                    	ServerUtilities.register(cxt, kind_of_receiver, teamName, regId);
                        
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }
        
        // download the details of all flats and pass them to the list adapter
     	new getAllFlatsDetails().execute();
              
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	// get the id of the flat from the text view
            	TextView tx =(TextView)view.findViewById(R.id.flat_id);
                String s = tx.getText().toString();
            	flat_position = s;
            	// this method informs the server that a flat was selected
            	new fillList().execute();  
            }
        });
    }
    
 
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context2, Intent intent) {
        	 // message contains flat id
        	 String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
             WakeLocker.acquire(getApplicationContext());         
             Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_SHORT).show(); 
             
             if(isNumeric(newMessage)){
            	 // the number of the list where is the flat
            	 int list_size =  adapter.getCount();
                 int a = list_size - Integer.parseInt((newMessage)) - 1;
            	 final int choice = a;
            	 // set this row selected
                 list.setItemChecked(choice, true);              
                 list.setSelection(choice);
                 list.setSelected(true);
     
            	 int wantedChild = 0;
            	 list.smoothScrollBy(3, 3);
            	 // get the number of items that are displayed on the screen, not the number of all lists
            	 int range = list.getLastVisiblePosition() - list.getFirstVisiblePosition();
            	 
            	 if(choice<list_size-range){
            		 wantedChild = 0;
            	 }
            	 else{
            		 wantedChild = choice - list_size +list.getLastVisiblePosition() - list.getFirstVisiblePosition();
            	 }
            	 // get the view of this row
            	 View v = list.getChildAt(wantedChild);
            	 // check if the flat bookmark was changed. Update it.
            	 adapter.UpdateMarker(0, v, newMessage);	 
             }
             WakeLocker.release();
        }
    };
     
    @Override
    protected void onDestroy() {
        Debug.stopMethodTracing();
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
    
    class getAllFlatsDetails extends AsyncTask<String, String, String> {
    	
    	 /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog_loadList = new ProgressDialog(AllEventsActivity.this);
            pDialog_loadList.setMessage("Loading houses..");
            pDialog_loadList.setIndeterminate(false);
            pDialog_loadList.setCancelable(true);
            pDialog_loadList.show();
        }
    	
    	protected String doInBackground(String... args) {
    		 try {
    			 List<NameValuePair> params = new ArrayList<NameValuePair>();      
    		     params.add(new BasicNameValuePair("teamName", teamName));
    		     params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));
    			// getting JSON string from URL
    		    JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);
    		    Log.d("All Events: ", json.toString());
    	        // Checking for SUCCESS TAG
    	        int success = json.getInt(TAG_SUCCESS);

    	            if (success == 1) {
    	                // events found
    	                // Getting Array of Events
    	                events = json.getJSONArray(TAG_EVENTS);
    	                
    	                // looping through All Events
    	                for (int i1 = 0; i1 < events.length(); i1++) {
    	                    JSONObject c = events.getJSONObject(i1);
    	                   
    	                    // Storing each json item in variable
    	                    id = c.getString(TAG_EID);

    	                    String name = c.getString(TAG_NAME);
    	                    String image = c.getString(TAG_IMAGE);
    	                    String price = String.valueOf(c.getDouble(TAG_PRICE));
    	                    String date_created_at = c.getString(TAG_DATE_POSTED);
    	                    String area = c.getString(TAG_AREA);
    	                    String marked = c.getString(TAG_MARKED);
    	                    
    	                    String date_c_year = date_created_at.substring(0, 4);
    	                    String date_c_month = date_created_at.substring(5, 7);
    	                    String date_c_day = date_created_at.substring(8, 10);
    	                    
    	                    String date_c = date_c_day + "-" + date_c_month + "-" + date_c_year;
    	                    House_Advert map = new House_Advert();

    	                    map.setID(id);
    	                    map.setCouples(String.valueOf(i1));
    	                    map.setName(name);
    	                    map.setImage(image);
    	                    map.setPrice(price);
    	                    map.setDatePosted(date_c);
    	                    map.setArea(area);
    	                    map.setMarked(Integer.parseInt(marked));
    	                    map.setRoomType(teamName);
    	                    
    	                    // adding HashList to ArrayList
    	                    flatList.add(map);
    	                }
    	            } else {
    	                // no events found
    	                // Launch Add New event Activity
    	                
    	            }
    	        } catch (JSONException e) {
    	            e.printStackTrace();
    	        }
    		return null;
    	}
    	
    	 protected void onPostExecute(String file_url) {
    		 pDialog_loadList.dismiss();
    		 adapter=new LazyAdapter(cxt, act, flatList);
    	     list.setAdapter(adapter);
         }
    	
    }
    
    class fillList extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
	        	
            final String regId2 = GCMRegistrar.getRegistrationId(getApplicationContext());
        	
            List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	params.add(new BasicNameValuePair("regId", regId2));
	    	params.add(new BasicNameValuePair("kindOfDevice", model));
	    	params.add(new BasicNameValuePair("message", flat_position));

		    jParser.makeHttpRequest(SERVER_URL + "send_message.php", "GET", params);
 	
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
           
        }
    }
}