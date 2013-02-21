package wario.arekp.pl;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter; 
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;


public class WarioService extends Service {
	private long id;
	private long lot_id;
	private String nazwa;
	private String opis;
	private Date czas_lot;
	private double gps_lat;
	private double gps_long;
	private double gps_predkosc;
	private double gps_wysokosc;
	private double wario_cisn;
	private double wario_wysokosc;
	private double wario_wysokosc1;
	private double wario_temper;
	private double wario_predkosc;
	public long new_lot=0;
	
	/**
	 * context.startService() | ->onCreate() - >onStartCommand() [service running]
	 * context.stopService() | ->onDestroy() [service stops]
	 * context.onBindService() | ->onCreate() [service created]
	 */
	private LocationManager lm;
	private LocationListener locationListener;
	static Log logi;
	public Krok krok;
	private static boolean serviceAlive = false;
	
	public static final String NEW_STEP = "wario.arekp.pl.NEW_STEP";
	private static final String DEVICE_ADDRESS = "00:11:12:14:00:93";
	private ArduinoReceiver arduinoReceiver1 = new ArduinoReceiver();
	private WarioDbAdapter myDBadapter;
	
	private static final String TAG = "WarioService";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		serviceAlive = true;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			Toast.makeText(this, "GPS nie jest w³aczony !!!!", Toast.LENGTH_SHORT).show();
			///txtVeiew.setText("GPS nie jest w³aczony !!!!");
		}
		Log.d(TAG, "pobralismy dan¹" + lm.getAllProviders());
		locationListener = new MyLocationListener();
		
		logi.d(TAG, "pobrano dane z GPS");
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,locationListener);

		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		myDBadapter = new WarioDbAdapter(this);
		//myDBadapter.open();
		//krok = new Krok();

	}

	@Override
	public void onDestroy() {
		Amarino.disconnect(this, DEVICE_ADDRESS);
		unregisterReceiver(arduinoReceiver1);
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		serviceAlive = false;
		new_lot=0;
		super.onDestroy();
	}
	

	@Override
	public void onStart(Intent intent, int startid) {
		serviceAlive = true;
				registerReceiver(arduinoReceiver1, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));
		Amarino.connect(this, DEVICE_ADDRESS);
	
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
	}
	private void sendMsg(String msg,double data) {
	    Intent intent = new Intent(WarioService.NEW_STEP);
	    
	    intent.putExtra(msg, data);
	    Log.d(TAG, "Wyslano "+msg);
	    sendBroadcast(intent);
	}
	private void sendMsg(String msg,String data) {
	    Intent intent = new Intent(WarioService.NEW_STEP);  
	    intent.putExtra(msg, data);
	    Log.d(TAG, "Wyslano "+msg);
	    sendBroadcast(intent);
	}
	
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if (loc != null) {
		    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		    	Date date = new Date();
				/**
				 * Toast.makeText( getBaseContext(),
				 * "Lokalizacja uleg³a zmianie : Szerokoœæ geograficzna: " +
				 * loc.getLatitude() + " D³ugoœæ geograficzna: " +
				 * loc.getLongitude(), Toast.LENGTH_SHORT) .show();
				 **/
			
				gps_predkosc=loc.getSpeed();
				gps_wysokosc=loc.getAltitude();
				gps_lat=loc.getLatitude();
				gps_long=loc.getLongitude();
				
				if (serviceAlive) {
					Log.d("Serwise", "mamy lot nr "+new_lot);
					myDBadapter.open();
				krok=new Krok(new_lot,"Lot-"+dateFormat.format(date),"",date,gps_lat,gps_long,gps_predkosc,gps_wysokosc,wario_cisn,wario_wysokosc,wario_wysokosc1,wario_temper,wario_predkosc);
				if (new_lot==0){
					new_lot=myDBadapter.createLOT(krok);
				}
				krok.setLot_id(new_lot);
				myDBadapter.createKROK(krok);
				myDBadapter.close();
				}
		//	krok.setGps_lat(loc.getLatitude());
		//	krok.setGps_long(loc.getLongitude());

			}
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getBaseContext(), "Disabled provider " + provider,
					Toast.LENGTH_SHORT).show();

		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(getBaseContext(),
					"Enabled new provider " + provider, Toast.LENGTH_SHORT)
					.show();

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public class ArduinoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String data = null;

			final String address = intent
					.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);

			final int dataType = intent.getIntExtra(
					AmarinoIntent.EXTRA_DATA_TYPE, -1);

			if (dataType == AmarinoIntent.STRING_EXTRA) {
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				// Log.d("AMARINO IN", data);
				if (data != null) {
					try {
						String[] temp = data.split(";");
						if (temp[0].equals("wario")) {
							wario_cisn=(Integer.parseInt(temp[2]));
							wario_temper=(Integer.parseInt(temp[1]));
							wario_wysokosc=	(Integer.parseInt(temp[3]));
							wario_predkosc=(Integer.parseInt(temp[5]));
							wario_wysokosc1=(Integer.parseInt(temp[4]));
						}
					} catch (NumberFormatException e) { /*
														 * oh data was not an
														 * integer
														 */
					}
				}
			}
		}
	}
	public static boolean isServiceAlive() {
		return serviceAlive;
		}
}
