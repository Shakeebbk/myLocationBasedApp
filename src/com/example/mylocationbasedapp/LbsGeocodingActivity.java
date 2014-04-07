package com.example.mylocationbasedapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LbsGeocodingActivity extends Activity {
    
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
 
    protected LocationManager locationManager;
    protected SmsManager smsManager;
    protected Geocoder geocoder;
    
    protected Button retrieveLocationButton;
    protected Button stopRetrieveLocationButton;
    protected EditText displayBox;
    public  LocationListener gpsListener;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveLocationButton = (Button) findViewById(R.id.retrieve_location_button);
        stopRetrieveLocationButton = (Button) findViewById(R.id.stop_retrieve_location_button);
        displayBox = (EditText) findViewById(R.id.displayView);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        smsManager = SmsManager.getDefault();
        geocoder = new Geocoder(this, Locale.getDefault());
       
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 
                MINIMUM_TIME_BETWEEN_UPDATES, 
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                gpsListener = new MyLocationListener()
        );
        
        retrieveLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentLocation();
                }
    });      
        stopRetrieveLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				locationManager.removeUpdates(gpsListener);
			}
        	
    });
    }    

    public void onStop(Bundle savedInstanceState) {
    	super.onStop();
    	locationManager.removeUpdates(gpsListener);
    }
    
    public void onRestart() {
    	super.onRestart();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 
                MINIMUM_TIME_BETWEEN_UPDATES, 
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                gpsListener = new MyLocationListener()
        );
    }
    @SuppressLint("NewApi")
	protected void showCurrentLocation() {

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
        	String locMessage = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );

        	try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String message = null;
            if (addresses != null && addresses.size() > 0) {
	            Address address = addresses.get(0);
	            message = address.getLocality();
	            Toast.makeText(LbsGeocodingActivity.this,locMessage + "message sent -" + message,
	                   Toast.LENGTH_LONG).show();
	            SystemClock.sleep(1000);
	            displayBox.setText(locMessage +" "+ message);
            }
            else {
            	Toast.makeText(LbsGeocodingActivity.this, "geo coder retuned Null!",
	                    Toast.LENGTH_LONG).show();
            	SystemClock.sleep(1000);
            }
            try {
            	smsManager.sendTextMessage("5554".toString(), null, message, null, null);
                } catch(Exception ex) {
                	Toast.makeText(LbsGeocodingActivity.this, "SMS send failed!!",
                            Toast.LENGTH_LONG).show();
                	SystemClock.sleep(1000);
                	ex.printStackTrace();
                }
            
        	} catch (IOException e) {
        		Toast.makeText(LbsGeocodingActivity.this, "Geo Coder retrieve failed!!",
                        Toast.LENGTH_LONG).show();
        	}
            
        }
        else {
        	Toast.makeText(LbsGeocodingActivity.this, "Location Null!",
                    Toast.LENGTH_LONG).show();
        }

    }   

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            Toast.makeText(LbsGeocodingActivity.this, message, Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(LbsGeocodingActivity.this, "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(LbsGeocodingActivity.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(LbsGeocodingActivity.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }
    
}