package com.example.AndroidMVC;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
  
// it checks whether the device is connected to internet. Otherwise it shows a message

public class ConnectionDetector {
  
    private Context _context;
  
    public ConnectionDetector(Context context){
        this._context = context;
    }
  
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
  
          }
          return false;
    }
}