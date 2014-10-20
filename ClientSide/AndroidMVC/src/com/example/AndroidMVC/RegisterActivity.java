
package com.example.AndroidMVC;

//get the kind of receiver, the team name and start the relevant Activity

import static com.example.AndroidMVC.CommonUtilities.SENDER_ID;
import static com.example.AndroidMVC.CommonUtilities.SERVER_URL;
import static com.example.AndroidMVC.CommonUtilities.EXTRA_MESSAGE;
import static com.example.AndroidMVC.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import java.util.ArrayList;
import java.util.List;
import menuListeners.itemSelectedListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.AndroidMVC.R;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class RegisterActivity extends itemSelectedListener {
    // alert dialog manager
    private AlertDialogManager alert = new AlertDialogManager();
    
    private String display_message = "";
     
    // Internet detector
    private ConnectionDetector cd;
     
    // Register button
    Button btnRegister;
    
    // Register button
    Button btnSignin;
    
    // Select device Spinner
    private Spinner spinner1;
    
    public static String TAG_TEAMNAME;
    private JSONObject json;
    
    EditText groupName;
    private ProgressDialog pDialog = null;
    
    // create a JSONObject to make HTTP requests
    private JSONParser jsonParser = new JSONParser();
    
    private String url_register = SERVER_URL + "register.php";   
    
    public static String teamName;
    public static Class<?> activity;
    public static String kind_of_receiver;
    public static String login;
    public static boolean active = true; 
 
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
         
        cd = new ConnectionDetector(getApplicationContext());
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        groupName = (EditText) findViewById(R.id.groupNameInput);
        
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignin = (Button) findViewById(R.id.btnSignin);
 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present, show alert dialog
            alert.showAlertDialog(RegisterActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
 
        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
             return;
        }        
             
       registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
         
        /*
         * Click event on Register button
         * */
        btnRegister.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View arg0) {
            	//get the selected element           	
                teamName = groupName.getText().toString(); 
                TAG_TEAMNAME = teamName;
                new CreateNewTeam().execute();
            }
        });
        
        /*
         * Click event on signin button
         * */
        btnSignin.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View arg0) {
            	//get the selected element
            	
            	kind_of_receiver = spinner1.getSelectedItem().toString();
            	teamName = groupName.getText().toString(); 
            	login = "signin";
            	TAG_TEAMNAME = teamName;
            	new signInUser().execute();    
            }
        });
    }
    
    // set the parameters to the intent
    public Intent setIntent2(String group, String log, String kind){
    	
    	  if(kind_of_receiver.equals("Map")){
          	activity = MapActivity.class;
          }
          else if(kind_of_receiver.equals("List")){
          	activity = AllEventsActivity.class;
          }
          else{
          	activity = AdvertDetailsActivity.class;
          }
    	  Intent i = new Intent(getApplicationContext(), activity);
          i.putExtra("kind", kind);
          i.putExtra("login", log);
          i.putExtra("teamName", group);
		return i;
    	
    }
    
    
    
    /**
     * Background Async Task to Create new product
     * */
    class CreateNewTeam extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating Team..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
             
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("groupName", teamName.toString()));
            params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));

            // getting JSON Object
            // Note that create product url accepts POST method
            json = jsonParser.makeHttpRequest(url_register, "POST", params);
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            try {
				Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            pDialog.dismiss();
        }
 
    }
    
    class signInUser extends AsyncTask<String, String, String> {
    	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Logging in..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
             
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("groupName", teamName.toString()));

            // getting JSON Object
            // Note that create product url accepts POST method
            String url_check_Team = SERVER_URL + "checkTeam.php";
            json = jsonParser.makeHttpRequest(url_check_Team, "GET", params);
            
            String flag = "";
			try {
				flag = json.getString("flag");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(flag.equals("exists")){
            	Intent i = setIntent2(teamName, login, kind_of_receiver);
            	unregisterReceiver(mHandleMessageReceiver);
            	active = false;
            	startActivity(i);
                finish();           
            }
            else{
            	display_message = "yes";
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(display_message.equals("yes")){
            try {
				Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        }
 
    }
    
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
             
            // Showing received message
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_SHORT).show();
             
            // Releasing wake lock
            WakeLocker.release();
        }
    };
    
 
}