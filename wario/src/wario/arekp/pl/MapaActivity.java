package wario.arekp.pl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//import wario.arekp.pl.WarioService.MyLocationListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MapaActivity extends MapActivity {
	private MapView mapView;
	private  MapController mc;
    private  GeoPoint p;
	private LocationManager lm;
	private LocationListener locationListener;
	private List<GeoPoint> path = new ArrayList<GeoPoint>();
	private GeoPoint gp;
	private MyLocationOverlay myLocationOverlay; 
	private DecimalFormat threeDec;
	
	public static final String NEW_STEP = "wario.arekp.pl.NEW_STEP";
	private static final String DEVICE_ADDRESS = "00:11:12:14:00:93";
	private ArduinoReceiver arduinoReceiver1 = new ArduinoReceiver();
	
	protected PowerManager.WakeLock mWakeLock;
	
	private TextView cisnienie;
	private TextView  txtTemp;
	private TextView TextAltWario;
	private TextView TextVWario;
	private TextView TextVGPS;
	private TextView TextAlt2;
	private TextView TextAltGPS;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		  final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
          this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
          this.mWakeLock.acquire();
		
		mapView = (MapView) findViewById(R.id.mapView);
		
		//cisnienie = (TextView) findViewById(R.id.TextCisnienie);
		//txtTemp = (TextView) findViewById(R.id.txtTemp);
		TextAltWario = (TextView) findViewById(R.id.TextAltWario);
		TextVWario = (TextView) findViewById(R.id.TextVWario);
		TextVGPS = (TextView) findViewById(R.id.TextVGPS);
		//TextAlt2= (TextView) findViewById(R.id.TextAlt2);
		TextAlt2= (TextView) findViewById(R.id.TextWarioAlt2);
		threeDec = new DecimalFormat("0.0");
		
		
		 mc = mapView.getController();
		 setupMap();
		 lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!enabled) {
				Toast.makeText(this, "GPS nie jest w³aczony !!!!", Toast.LENGTH_SHORT).show();
				///txtVeiew.setText("GPS nie jest w³aczony !!!!");
			}else Toast.makeText(this, "GPS W³¹czony !!!!", Toast.LENGTH_SHORT).show();
		locationListener = new MyLocationListener();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0,
					locationListener);
			TextVGPS.setText("Predkosc");TextVWario.setText("Predkosc wario");
					registerReceiver(arduinoReceiver1, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));
		Amarino.connect(this, DEVICE_ADDRESS);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
//	@Override
//	public void onStart(Intent intent, int startid) {
//		//serviceAlive = true;
//
//	}
//	
	private void setupMap() {
		mapView.setBuiltInZoomControls(true);
		//mapView.setTraffic(false);
		mapView.setStreetView(true);
		mapView.setSatellite(false);
		myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mc.animateTo(myLocationOverlay.getMyLocation());
            }
        });
	}
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if (loc != null) {
				TextVGPS.setText("" + threeDec.format((loc.getSpeed()*3.6)));
			//	TextAltGPS.setText("" + threeDec.format(loc.getAltitude()));
			//	p = new GeoPoint((int) (loc.getLatitude()*1e6),   (int) (loc.getLongitude()*1e6));
			//	mc.animateTo(p);
				//Toast.makeText(getBaseContext()," Lat : "+loc.getLatitude(), Toast.LENGTH_SHORT).show();
				Log.d("MapActiv", "zmiana lok "+loc.getLatitude());
				gp = new GeoPoint((int)(loc.getLatitude()* 1E6),(int) (loc.getLongitude()* 1E6));
				path.add(gp);
				Log.d("MapActiv", "Dodanie punktu mamy punktów "+path.size());
				//mapView.getOverlays().clear();
				//Log.d("MapActiv", "czysczenie ");
				mapView.getOverlays().add(new RoutePathOverlay(path));
				Log.d("MapActiv", "dodano warstwe ");
				//mapView.invalidate();
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
		//	TextAltWario.setText("Wysokoœæ wario");
			String data = null;
			// double w = wys - wysbazowa;
			// wysAndro.setText(threeDec.format(w));
			// Log.d("wario_wysok", Double.toString(wys));
			// Log.d("wario_wysokBazowa", Double.toString(wysbazowa));
			// Log.d("wario_wysokdelta", Double.toString(wys - wysbazowa));

			final String address = intent
					.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			Log.d("AMARINO_ADRESS", address);

			final int dataType = intent.getIntExtra(
					AmarinoIntent.EXTRA_DATA_TYPE, -1);
			Log.d("AMARINO_DATATYPE", "" + dataType);

			if (dataType == AmarinoIntent.STRING_EXTRA) {
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);

				if (data != null) {
					try {
						String[] temp = data.split(";");
						if (temp[0].equals("wario")) {
						//	mProgress.setProgress(Integer.parseInt(temp[2]));
							//cisnienie.setText((Integer.parseInt(temp[2])/100)+" hPa");
							//txtTemp.setText(temp[1]+" *C");
							TextAltWario.setText(temp[3]);
						    TextVWario.setText(temp[5]);
							TextAlt2.setText(temp[4]);
						//	textAltGPS.setText(temp[3]);
							// temperatura.setText(temp[1]);
							// cisnienie.setText(temp[2]);
							// wysokosc.setText(temp[3]);
							// wys = Double.parseDouble(temp[3]);
							// wysokosc1.setText(temp[4]);
							// predkoscWario.setText(temp[5]);
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
}
