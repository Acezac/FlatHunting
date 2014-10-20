package com.example.AndroidMVC;

import static com.example.AndroidMVC.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.AndroidMVC.CommonUtilities.EXTRA_MESSAGE;
import static com.example.AndroidMVC.CommonUtilities.SERVER_URL; 
import static com.example.AndroidMVC.CommonUtilities.SENDER_ID;
import static com.example.AndroidMVC.CommonUtilities.TAG_EID;
import static com.example.AndroidMVC.CommonUtilities.TAG_LATITUDE;
import static com.example.AndroidMVC.CommonUtilities.TAG_LONGITUDE;
import static com.example.AndroidMVC.CommonUtilities.TAG_SUCCESS;
import static com.example.AndroidMVC.CommonUtilities.TAG_EVENTS;
import static com.example.AndroidMVC.CommonUtilities.TAG_MARKED;
import static com.example.AndroidMVC.CommonUtilities.TAG_NAME;
import java.util.ArrayList;
import java.util.List;
import menuListeners.itemSelectedListenerFragAct;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.AndroidMVC.JSONParser;
import com.example.AndroidMVC.R;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// This file contains the activity that shows the flats on the map

@SuppressLint("NewApi") public class MapActivity extends itemSelectedListenerFragAct implements OnInfoWindowClickListener {
	
	public GoogleMap map;
	
    // Creating JSON Parser object
    // JSON parser class
    JSONParser jsonParser = new JSONParser();	
   
    private ArrayList<House_Advert> ArrayHouses = new ArrayList<House_Advert>();
    private static ArrayList<Marker> markers;
    private JSONParser jParser = new JSONParser();
    private String eid = "0";
    private String url_all_events = SERVER_URL + "get_all_events.php";
    private static String url_details_event = SERVER_URL + "get_event_details.php";
    private ProgressDialog pDialog;
    private JSONArray events = null;
    private AsyncTask<Void, Void, Void> mRegisterTask;
    private String teamName;
    private String kind_of_receiver;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);      
        
        context = this;
        Bundle b = getIntent().getExtras();

        kind_of_receiver = b.getString("kind");
        teamName = b.getString("teamName");        
        
        if(getIntent().hasExtra("saved_url")){
        	url_all_events =  b.getString("saved_url");
        }
        
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
                   // Skips registration.
            	 if(!getIntent().hasExtra("saved_url") && !getIntent().hasExtra("noR")){
            		 ServerUtilities.register(context, kind_of_receiver, teamName, regId);
                     Toast.makeText(getApplicationContext(), "Already registered with GCM ", Toast.LENGTH_SHORT).show(); 
            	 }
               } else {
                   // Try to register again, but not in the UI thread.
                   // It's also necessary to cancel the thread onDestroy(),
                   // hence the use of AsyncTask instead of a raw thread.
                   //final Context context = this;
                   mRegisterTask = new AsyncTask<Void, Void, Void>() {

                       @Override
                       protected Void doInBackground(Void... params) {
                          // boolean registered =
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
        
        if(eid.equals("0")){
        	new GetAllEventsDetails().execute();
        } 
    }
    
    /**
     * Background Async Task to Load all events by making HTTP Request
     * */
    class GetAllEventsDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapActivity.this);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All events from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters

            // getting JSON string from URL
            List<NameValuePair> params = new ArrayList<NameValuePair>();      
        	params.add(new BasicNameValuePair("teamName", teamName));
        	params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));
            JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Events: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // events found
                    // Getting Array of Events
                    events = json.getJSONArray(TAG_EVENTS);
                    markers = new ArrayList<Marker>();
                    // looping through All Events
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject c = events.getJSONObject(i);
                        String marked = c.getString(TAG_MARKED);
                        double longi = Double.parseDouble(c.getString(TAG_LATITUDE));
                        double lati = Double.parseDouble(c.getString(TAG_LONGITUDE));
                        
                        House_Advert sample = new House_Advert();
                        sample.setID(c.getString(TAG_EID));
                        sample.setName(c.getString(TAG_NAME));
                        sample.setLongitude(longi);
                        sample.setLatitude(lati);
                        sample.setMarked(Integer.parseInt(marked));
                        ArrayHouses.add(sample);         
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
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all events
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                	setUpMapIfNeeded(ArrayHouses);
                }
            }); 
        } 
    }   
    
    private void setUpMapIfNeeded(ArrayList<House_Advert> houses) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap(houses);
            }
        }
    }
    
    private void setUpMap(ArrayList<House_Advert> housesOnMap) {
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        for(int i=0;i<ArrayHouses.size();i++){
        	if(housesOnMap.get(i).getMarked() == 1){
           	 	LatLng point = new LatLng(housesOnMap.get(i).getLongitude(), housesOnMap.get(i).getLatitude());
           	 	Marker m = map.addMarker(new MarkerOptions()
           	 		.position(point)
           	 		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
           	 		.title(housesOnMap.get(i).getName()));  	 
           	 	markers.add(m);
           	 	map.setOnInfoWindowClickListener(this);
        	}
        	else if(housesOnMap.get(i).getMarked() == 0){
        		LatLng point = new LatLng(housesOnMap.get(i).getLongitude(), housesOnMap.get(i).getLatitude());
        		Marker m = map.addMarker(new MarkerOptions()
        			.position(point)
        			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        			.title(housesOnMap.get(i).getName()));  
        		markers.add(m);
        		map.setOnInfoWindowClickListener(this);
        	}
        }
    }
 
    public void highlightMarker(int id, int realID){
    	
    	 List<NameValuePair> params = new ArrayList<NameValuePair>();
         JSONArray events = null;
         params.add(new BasicNameValuePair("eid", String.valueOf(realID)));
         params.add(new BasicNameValuePair("teamName", teamName));
         
         try {
       	  	JSONObject json = jParser.makeHttpRequest(url_details_event, "POST", params);
			events = json.getJSONArray(TAG_EVENTS);
         } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
         }
         String mark = "";

        
         try {
         	JSONObject c = events.getJSONObject(0);
			mark = c.getString(TAG_MARKED);
         } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
         }                                        
               
         String k="";
         // get the name of the flat
         for(int j=0;j<ArrayHouses.size();j++){
        	 if(ArrayHouses.get(j).getID().equals(String.valueOf(realID))){
        		 k=ArrayHouses.get(j).getName();
        	 }
         }
         
		  
			  for(int i =0;i<markers.size();i++){
				  if(markers.get(i).getTitle().equals(k)){
					  if(mark.equals("1")){
						  markers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					  }
					  else if(mark.equals("0")){
						  markers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
					  }	  					

					markers.get(i).showInfoWindow();
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(markers.get(i).getPosition(), 15);        
					map.animateCamera(cameraUpdate);
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
                 String marking = "";
                 House_Advert a = new House_Advert();
                 
                 // find the house into the Array of Houses
                 for(int g1=0;g1<ArrayHouses.size();g1++){
                	 if(ArrayHouses.get(g1).getID().equals(newMessage)){
                		 a=ArrayHouses.get(g1);            
                	 }
                 }
                 // if the name of the object that found is not null check if there is a marker with the same name
                 // if there is a marker call the highlightMarker method, else return
                 // we do that instead of checking directly the a.getID() because maybe there is not this marked on the map
                 // therefore if the marking = "" return
                 if( a.getName() != null){
                	 for(int g2=0;g2<markers.size();g2++){
                		 if(a.getName().equals(markers.get(g2).getTitle())){
                			 marking = a.getID();			 
                		 }
                	 }
                 }
                 if(!marking.equals("")){
                	 highlightMarker(Integer.parseInt(a.getID()), Integer.parseInt(marking));
                 }
                 else{
                	 return;
                 }
              }
             WakeLocker.release();           
        }
    };
     
    @Override
    protected void onDestroy() {
        
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

	@Override
	public void onInfoWindowClick(Marker marker) {
		String id="";
		
		// search for the id of the flat that was clicked
		for(int i=0;i<ArrayHouses.size();i++){
			if(marker.getTitle().equals(ArrayHouses.get(i).getName())){
				id = String.valueOf(ArrayHouses.get(i).getID());
			}
		}
        final String regId = GCMRegistrar.getRegistrationId(this);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("regId", regId));
	    params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));
	    params.add(new BasicNameValuePair("message", id));

		jParser.makeHttpRequest(SERVER_URL + "send_message.php", "GET", params);
		
	}
}