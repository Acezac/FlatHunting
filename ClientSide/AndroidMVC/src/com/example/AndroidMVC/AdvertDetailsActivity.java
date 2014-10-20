package com.example.AndroidMVC;

import static com.example.AndroidMVC.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.AndroidMVC.CommonUtilities.EXTRA_MESSAGE;
import static com.example.AndroidMVC.CommonUtilities.SERVER_URL; 
import static com.example.AndroidMVC.CommonUtilities.SENDER_ID;
import static com.example.AndroidMVC.CommonUtilities.TAG_AREA;
import static com.example.AndroidMVC.CommonUtilities.TAG_DATE_AVAILABLE;
import static com.example.AndroidMVC.CommonUtilities.TAG_NAME;
import static com.example.AndroidMVC.CommonUtilities.TAG_DATE_POSTED;
import static com.example.AndroidMVC.CommonUtilities.TAG_EVENTS;
import static com.example.AndroidMVC.CommonUtilities.TAG_IMAGE;
import static com.example.AndroidMVC.CommonUtilities.TAG_PRICE;
import static com.example.AndroidMVC.CommonUtilities.TAG_TEAMNAME;
import static com.example.AndroidMVC.CommonUtilities.TAG_COUPLES;
import static com.example.AndroidMVC.CommonUtilities.TAG_ROOM_TYPE;
import static com.example.AndroidMVC.CommonUtilities.TAG_DESCRIPTION;
import static com.example.AndroidMVC.CommonUtilities.TAG_SELLERTYPE;
import java.util.ArrayList;
import java.util.List;
import menuListeners.itemSelectedListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gcm.GCMRegistrar;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//This file contains the activity that shows the details of each flat

@SuppressLint("NewApi")
public class AdvertDetailsActivity extends itemSelectedListener  {
	
	ImageView image;
	TextView title;
	TextView price;
	TextView location;
	TextView datePosted;
	TextView roomType;
	TextView couples;
	TextView dateAvailable;
	TextView description;
	TextView sellerType;
	
	// object of JSONParser class
	private JSONParser jParser = new JSONParser();
	
	// url to get the details of a flat
	private static String url_all_events = SERVER_URL + "get_event_details.php";
	
	// A dense indexed sequence of values
	private JSONArray events = null;
	
	// Instance of ImageLoader to load the main image of details View
	private ImageLoader imageLoader;
	
	// This object will be used to save the details of the flat
	private ArrayList<House_Advert> house = new ArrayList<House_Advert>();
	
	// An asynchronous task is defined by a computation that runs on 
	// a background thread and whose result is published on the UI thread
	private AsyncTask<Void, Void, Void> mRegisterTask;
	
	// the id of the flat that will send to the server
	private String eid="0";
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.advert_details);
        
	        image = (ImageView) findViewById(R.id.image_house);
	        title = (TextView) findViewById(R.id.title);
	        price = (TextView) findViewById(R.id.price_num);
	        location = (TextView) findViewById(R.id.location);
	        datePosted = (TextView) findViewById(R.id.datePosted);
	        roomType = (TextView) findViewById(R.id.roomType);
	        couples = (TextView) findViewById(R.id.couples);
	        dateAvailable = (TextView) findViewById(R.id.dateAvailable);
	        description = (TextView) findViewById(R.id.description);
	        sellerType = (TextView) findViewById(R.id.sellerType);
	        imageLoader=new ImageLoader(getApplicationContext());
	        
	        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
	        
	        final String kind_of_receiver;
	        final String teamName;
	        
	        // Return the parameters of the intent that started this activity.
	        Bundle b = getIntent().getExtras();
	        
	        // get the arguments from bundle
	        kind_of_receiver = b.getString("kind");
	        teamName = b.getString("teamName");

	        // Make sure the device has the proper dependencies.
	        GCMRegistrar.checkDevice(this);
	        // Make sure the manifest was properly set - comment out this line
	        // while developing the app, then uncomment it when it's ready.
	        GCMRegistrar.checkManifest(this);
	        
	        // Register a BroadcastReceiver to be run in the main activity thread.
	        registerReceiver(mHandleMessageReceiver,new IntentFilter(DISPLAY_MESSAGE_ACTION));
	        
	        // get the registration id from GCM server
	        final String regId = GCMRegistrar.getRegistrationId(this);
	        
	        if (regId.equals("")) {
	             // Automatically registers application on startup.
	             GCMRegistrar.register(this, SENDER_ID);
	        } else {
	        	final Context context = this;
	             // Device is already registered on GCM, check server.
	             if (GCMRegistrar.isRegisteredOnServer(this)) {
	                 // Skips registration.
	            	 if(!getIntent().hasExtra("saved_url") && !getIntent().hasExtra("noR")){
	            		 ServerUtilities.register(context, kind_of_receiver, teamName, regId);
		                 Toast.makeText(getApplicationContext(), "Already registered with GCM ", Toast.LENGTH_SHORT).show();
	            	 }
	               } else {
	                   mRegisterTask = new AsyncTask<Void, Void, Void>() {
	                       @Override
	                       protected Void doInBackground(Void... params) {                        
	                           ServerUtilities.register(context, kind_of_receiver, teamName, regId);
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
	        
	           // check if it the activity didn't not start by another one.
	           if(eid.equals("0")){
	        	   new GetEventDetails().execute();
	           }        
	        
	           // Image pager listener
	           image.setOnClickListener(new View.OnClickListener() {
	        	   public void onClick(View v) {
	        		   Intent intent = new Intent(AdvertDetailsActivity.this,ImageViewPager.class);
	        		   intent.putExtra("eid", eid);
	        		   startActivity(intent);
	        	   }
	           });
	 }	
	 
	 class GetEventDetails extends AsyncTask<String, String, String> {

	        @Override
	        protected void onPreExecute() {      
	        }
	 
	        /**
	         * getting flat details from url
	         * */
	        protected String doInBackground(String... args) {
	        	fillDetails();
	            return null;
	        }
	 
	        /**
	         * After completing background task Dismiss the progress dialog
	         * **/
	        protected void onPostExecute(String file_url) {
	            // updating UI from Background Thread              
	            for(int i=0;i<house.size();i++){
	            	imageLoader.DisplayImage(house.get(i).getImage(), image, 0);
	 	            location.setText(house.get(i).getArea());
	 	            couples.setText(house.get(i).getCouples());
	 	            datePosted.setText(house.get(i).getDatePosted());
	 	            dateAvailable.setText(house.get(i).getDateAvailable());
	 	            roomType.setText(house.get(i).getRoomType());
	 	            title.setText(house.get(i).getName());
	 	            price.setText(house.get(i).getPrice());
	 	            description.setText(house.get(i).getDescription());
	 	            sellerType.setText(house.get(i).getSellerType());
	            }
	        }	 
	    }   
	 
	 
	 	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context2, Intent intent) {
	        	 String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	             WakeLocker.acquire(getApplicationContext());         
	             
	             Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_SHORT).show();       
	           
	             if(isNumeric(newMessage)){                 
	                 eid = newMessage;
	                 new GetEventDetails().execute();
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
	    
	    // download the details of the flat and store them into a House_Advert object
	    public void fillDetails(){
	    	 List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	 params.add(new BasicNameValuePair("eid", eid));
	    	 JSONObject json = new JSONObject();
	    	 params.add(new BasicNameValuePair("teamName", TAG_TEAMNAME));
		     House_Advert houseobj = new House_Advert();

	    	 json = jParser.makeHttpRequest(url_all_events, "POST", params);	    	   		     

		     try {
				events = json.getJSONArray(TAG_EVENTS);
				
				// date posted
				String dateP;
				// date available
				String dateA;
				 
				for (int i = 0; i < events.length(); i++) {
					
	                JSONObject c = events.getJSONObject(i);
	                
	                dateA = c.getString(TAG_DATE_AVAILABLE);
	                dateP = c.getString(TAG_DATE_POSTED);
	                		
	                houseobj.setArea(c.getString(TAG_AREA));
	                houseobj.setCouples(c.getString(TAG_COUPLES));
	                houseobj.setRoomType(c.getString(TAG_ROOM_TYPE));
	                houseobj.setImage(c.getString(TAG_IMAGE));
	                houseobj.setName(c.getString(TAG_NAME));
	                houseobj.setPrice(c.getString(TAG_PRICE));
	                houseobj.setDescription(c.getString(TAG_DESCRIPTION));
	                houseobj.setSellerType(c.getString(TAG_SELLERTYPE));

	                // convert date posted and date available into a more readable ones
	                String date_c_year = dateA.substring(0, 4);
                    String date_c_month = dateA.substring(5, 7);
                    String date_c_day = dateA.substring(8, 10);
                    
                    String dateA_c = date_c_day + "-" + date_c_month + "-" + date_c_year;
                    
                    String dateP_year = dateP.substring(0, 4);
                    String dateP_month = dateP.substring(5, 7);
                    String dateP_day = dateP.substring(8, 10);
                    
                    String dateP_c = dateP_day + "-" + dateP_month + "-" + dateP_year;

                    houseobj.setDateAvailable(dateA_c);
	                houseobj.setDatePosted(dateP_c);
	             
	                house.add(houseobj);
				}          
	              
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  	
	    }	   
}