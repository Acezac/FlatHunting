package menuListeners;

import static com.example.AndroidMVC.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.example.AndroidMVC.AllEventsActivity;
import com.example.AndroidMVC.GCMIntentService;
import com.example.AndroidMVC.R;
import com.example.AndroidMVC.RegisterActivity;
import com.google.android.gcm.GCMRegistrar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

// options menu for all activities expect the mapActivity

public class itemSelectedListener extends Activity{
	
	private String status;
	private String id;
	private String url_default = SERVER_URL + "changeNotificationSettings.php?";	
	private String url_all_events = SERVER_URL + "sortHousesBySaved.php";
	public static Menu m;
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	
	public boolean onPrepareOptionsMenu(Menu menu){
		
		menu.getItem(2).setEnabled(false);
		menu.getItem(3).setEnabled(false);
		if(RegisterActivity.active){
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setEnabled(false);
			menu.getItem(3).setEnabled(false);
		}		
		if(RegisterActivity.kind_of_receiver != null){
			if(RegisterActivity.kind_of_receiver.equals("List")){
			menu.getItem(2).setEnabled(true);
			menu.getItem(1).setEnabled(false);
			menu.getItem(3).setEnabled(true);
			}
		}
		return true;
	}
		
	 public boolean onCreateOptionsMenu(Menu menu) {
		    if(GCMIntentService.idd == ""){		  
		    	final String regId = GCMRegistrar.getRegistrationId(this);
		    	UpdateStatus("1", regId);		 	
		    }
		    else{
		    	UpdateStatus("1", GCMIntentService.idd);
		    }
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.options_menu, menu);
	        m = menu; 
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	String regId;
	    	if(GCMIntentService.idd == ""){
	    		regId = GCMRegistrar.getRegistrationId(this);
	    	}
	    	else{
	    		regId = GCMIntentService.idd;
	    	}
	        switch(item.getItemId()) {
	            case R.id.options_unregister:
	                GCMRegistrar.unregister(getApplicationContext());
	                Intent i = new Intent(itemSelectedListener.this, RegisterActivity.class);
	                startActivity(i);
	                return true;     
	           case R.id.options_status:	            
	            	if(item.getTitle().equals("Disable Notifications")){
	            		item.setTitle("Enable Notifications");
	            		UpdateStatus("0", regId);	  		
	            	}
	            	else if(item.getTitle().equals("Enable Notifications")){
	            		item.setTitle("Disable Notifications");
	            		UpdateStatus("1", regId);
	            	}
	            	return true;
	            case R.id.options_sortByMark:
	            	//UpdateStatus("0", regId);
	            		displaySaved(regId);  			     
	            		return true;
	            case R.id.options_showAll:
	            	//UpdateStatus("1", regId);
	            	ShowAll();
	            	return true;
	            case R.id.options_exit:
	            	Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
	            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            	startActivity(intent);
	                finish();
	                return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    }
	    
	    
	    public void UpdateStatus(String s, String regId){
	    	status = s;
	    	id = regId;
			StrictMode.setThreadPolicy(policy);
	    	DefaultHttpClient httpClient = new DefaultHttpClient();
			
			String url = url_default; 
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	 params.add(new BasicNameValuePair("status", status));
	    	 params.add(new BasicNameValuePair("regId", id));
	    	 params.add(new BasicNameValuePair("kindOfDevice", Build.MODEL.toString()));

		      
		     String paramString = URLEncodedUtils.format(params, "utf-8");
             url += paramString;
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
	    }
	    
	    public void displaySaved(String regId){
			      
			 Intent intent = new Intent(itemSelectedListener.this, AllEventsActivity.class);
			 intent.putExtra("kind", "List");
			 intent.putExtra("teamName", RegisterActivity.TAG_TEAMNAME);
			 intent.putExtra("saved_url", url_all_events);
			 
		     startActivity(intent);
	    }
	    
	    public void ShowAll(){
	    	 Intent intent = new Intent(itemSelectedListener.this, AllEventsActivity.class);
			 intent.putExtra("kind", "List");
			 if(intent.hasExtra("saved_url")){
			 intent.removeExtra("saved_url");
			 }
			 intent.putExtra("noR", "noR");
			 intent.putExtra("teamName", RegisterActivity.TAG_TEAMNAME);
			 
		     startActivity(intent);
	    }
	    
	    // check if the string can be converted into integer
	    public static boolean isNumeric(String str)
	    {
	      NumberFormat formatter = NumberFormat.getInstance();
	      ParsePosition pos = new ParsePosition(0);
	      formatter.parse(str, pos);
	      return str.length() == pos.getIndex();
	    }

}
