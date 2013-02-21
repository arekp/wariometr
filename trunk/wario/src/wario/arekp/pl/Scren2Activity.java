package wario.arekp.pl;

// parent="@android:style/Widget.RatingBar.Indicator"
//parent="@android:style/Widget.RatingBar.Small" 
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

//import wario.arekp.pl.WarioService.ArduinoReceiver;
import wario.arekp.pl.visualizer.GraphView;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

import com.flotandroidchart.flot.FlotChartContainer;
import com.flotandroidchart.flot.FlotDraw;
import com.flotandroidchart.flot.data.PointData;
import com.flotandroidchart.flot.data.SeriesData;

public class Scren2Activity extends Activity {
	private static final String DEVICE_ADDRESS = "00:11:12:14:00:93";
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	private LocationManager lm; 
	private LocationListener locationListener;
	private DecimalFormat threeDec;
	
	private GraphView mGraph;
	private TextView cisnienie;
	private TextView  txtTemp;
	private TextView TextAltWario;
	private TextView TextVWario;
	private TextView TextVGPS;
	private TextView TextAlt2;
	private TextView TextAltGPS;
	
	private RatingBar  RatingBarUP;
	private RatingBar  RatingBarDOWN;
	private RatingBar  RatingBar2;
	
	private FlotChartContainer tv;
	private FlotDraw fd;
	private Vector<SeriesData> sds;
	private SeriesData SeriaVwario;
	private Vector<PointData> pdsVwario;
	
	private ProgressBar mProgress;

	
	private Intent incomingIntent;
//	private SampleReceiver myReceiver= new SampleReceiver();
	public static final String NEW_STEP = "wario.arekp.pl.NEW_STEP";
	private static final String TAG = "Scren2Activity";
	protected PowerManager.WakeLock mWakeLock;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scren2);
		
		  final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
          this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
          this.mWakeLock.acquire();
	   
//		 mGraph = (GraphView)findViewById(R.id.graph);
//		 mGraph.setMaxValue(1024);
//		 Random generator = new Random( 1024 );
//			for (int i = 0; i < 2000; i++) {
//				mGraph.addDataPoint(generator.nextInt());
//			}
			
			cisnienie = (TextView) findViewById(R.id.TextCisnienie);
			txtTemp = (TextView) findViewById(R.id.txtTemp);
			TextAltWario = (TextView) findViewById(R.id.TextAltWario);
			TextVWario = (TextView) findViewById(R.id.TextVWario);
			TextVGPS = (TextView) findViewById(R.id.TextVGPS);
			TextAlt2= (TextView) findViewById(R.id.TextAlt2);
			TextAltGPS= (TextView) findViewById(R.id.TextAltGPS);
			
			RatingBarUP = (RatingBar) findViewById(R.id.ratingBarUP);
			RatingBarDOWN = (RatingBar) findViewById(R.id.ratingBarDown);
			RatingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
			RatingBarUP.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					ratingBar.playSoundEffect(0);						
		
					}
				});
			RatingBarDOWN.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					ratingBar.playSoundEffect(0);						
		
					}
				});
	    //    tv = (FlotChartContainer)this.findViewById(R.id.flotdraw);
		//	sds = new Vector<SeriesData>();
			 
		//    SeriaVwario = new SeriesData();
		//	pdsVwario = new Vector<PointData>();

			threeDec = new DecimalFormat("0.0");
			// ---u¿ywamy klasy LocationManager w celu odczytu lokacji GPS---
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!enabled) {
				// txtVeiew.setText("GPS nie jest w³aczony !!!!");
			}
			Log.d("nawigacja", "pobralismy dan¹" + lm.getAllProviders());
			locationListener = new MyLocationListener();
			Log.d("nawigacja", "pobrano dane z GPS");
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0,locationListener);

	}

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}

	@Override
	protected void onStart() {
		
//		registerReceiver(myReceiver, new IntentFilter(NEW_STEP));
		Log.d(TAG, "Start Screen2");
		super.onStart();
		registerReceiver(arduinoReceiver, new IntentFilter(
		AmarinoIntent.ACTION_RECEIVED));
	
       Amarino.connect(this, DEVICE_ADDRESS);
	}
	@Override
	protected void onStop() {
		Log.d(TAG, "Stop Screen2");
//		unregisterReceiver(myReceiver);
		super.onStop();
		if (!WarioService.isServiceAlive()){
		Amarino.disconnect(this, DEVICE_ADDRESS);
		unregisterReceiver(arduinoReceiver);
		}
	}
    @Override
    public void onDestroy() {
            this.mWakeLock.release();
            super.onDestroy();
    }
//	private class SampleReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Log.d("Screen2 - dane", intent.toString());
//			//cisnienie.setText(intent.getStringExtra("cisnienie"));
//			//txtTemp.setText(intent.getStringExtra("txtTemp"));
//		//	TextAltWario.setText(intent.getStringExtra("TextAltWario"));
//			TextVWario.setText(intent.getStringExtra("TextVWario"));
////			TextVGPS.setText(""+intent.getDoubleExtra("TextVGPS",0));
////			TextAlt2.setText(intent.getStringExtra("TextAlt2"));
//		//	mGraph.addDataPoint(Integer.parseInt(intent.getStringExtra("krok")));
//		}
//	}
	
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if (loc != null) {

				// latGPS.setText("" + loc.getLatitude());
				// longGPS.setText("" + loc.getLongitude());
				TextVGPS.setText("" + threeDec.format((loc.getSpeed()*3.6)));
				RatingBar2.setRating(loc.getSpeed());
				TextAltGPS.setText("" + threeDec.format(loc.getAltitude()*3.6));
				// txtVeiew.setText("zmiana lokalizacji na nastêpuj¹ce wspó³rzêdne (szerokoœæ, d³ugoœæ):"
				// + loc.getLatitude() + ", " + loc.getLongitude());

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
			int i=1;
			String data = null;
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
							Float v = new Float(temp[5]);
							if (v>0){
								RatingBarDOWN.setRating(0);
							RatingBarUP.setRating(v);
							}else
							{v=v*(-1);
							RatingBarUP.setRating(0);
							RatingBarDOWN.setRating(v);
							}
							cisnienie.setText((Integer.parseInt(temp[2]) / 100)+ " hPa");
							txtTemp.setText(temp[1] + " *C");
							TextAltWario.setText(temp[3]);
							TextVWario.setText(temp[5]);
							TextAlt2.setText(temp[4]);
						//	pdsVwario.add(new PointData(i++,Double.parseDouble(temp[3])));
						//	Log.d("Screen2","dodano punkt "+i);
							// textAltGPS.setText(temp[3]);
							// temperatura.setText(temp[1]);
							// cisnienie.setText(temp[2]);
							// wysokosc.setText(temp[3]);
							// wys = Double.parseDouble(temp[3]);
							// wysokosc1.setText(temp[4]);
							// predkoscWario.setText(temp[5]);
						//		        SeriaVwario.setData(pdsVwario);
			
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
