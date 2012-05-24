package wario.arekp.pl;

//import edu.mit.media.hlt.sensorgraph.SensorGraph.ArduinoReceiver;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class WarioActivity extends Activity {

	private Button btnName;
	private TextView txtVeiew;
	private Button zeruj;
	private Button zmienLCD;

	private LocationManager lm;
	private LocationListener locationListener;
	static Log logi;

	private static final String DEVICE_ADDRESS = "00:11:12:14:00:93";
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	private TextView cisnienie;
	private TextView temperatura;
	private TextView wysokosc;
	private TextView wysokosc1;
	private TextView predkoscWario;
	private TextView latGPS;
	private TextView longGPS;
	private TextView wysAndro;
	private TextView spedGPS;
	private TextView wysokoscGPS;
	private TextView predAndr;
	private ImageView imgWysDOW;
	private ImageView imgWysUP;
	
	private double wysbazowa;
	private double wys;
	private double wys1;
	private long t1;
	private DecimalFormat threeDec;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnName = (Button) findViewById(R.id.btnName);
		zeruj = (Button) findViewById(R.id.zeruj);
		zmienLCD = (Button) findViewById(R.id.zmienlcd);
		txtVeiew = (TextView) findViewById(R.id.txtview);
		cisnienie = (TextView) findViewById(R.id.cisnienie);
		temperatura = (TextView) findViewById(R.id.temp);
		wysokosc = (TextView) findViewById(R.id.wys);
		wysokosc1 = (TextView) findViewById(R.id.wys1);
		predkoscWario = (TextView) findViewById(R.id.predWario);
		wysAndro = (TextView) findViewById(R.id.wysAndro);
		predAndr = (TextView) findViewById(R.id.predAndr);
		latGPS = (TextView) findViewById(R.id.latView);
		longGPS = (TextView) findViewById(R.id.longView);
		spedGPS = (TextView) findViewById(R.id.predkoscGPS);
		wysokoscGPS = (TextView) findViewById(R.id.wysokoscGPS);
		imgWysDOW= (ImageView) findViewById(R.id.imageView1);
		//imgWysUP =(ImageView) findViewById(R.id.imageView2);
		
		btnName.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BIPOFF();
			}
		});

		zmienLCD.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				zmienLCD(v);
			}
		});
		zeruj.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				zerujWysok(v);
			}
		});
		// ---u¿ywamy klasy LocationManager w celu odczytu lokacji GPS---
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			txtVeiew.setText("GPS nie jest w³aczony !!!!");
		}
		Log.d("nawigacja", "pobralismy dan¹" + lm.getAllProviders());
		locationListener = new MyLocationListener();
		logi.d("nawigacja", "pobrano dane z GPS");
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0,
				locationListener);

		threeDec = new DecimalFormat("0.0");
	}

	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our
		// receiver
		registerReceiver(arduinoReceiver, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));

		// this is how you tell Amarino to connect to a specific BT device from
		// within your own code
		Amarino.connect(this, DEVICE_ADDRESS);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!WarioService.isServiceAlive()){
		Amarino.disconnect(this, DEVICE_ADDRESS);
		unregisterReceiver(arduinoReceiver);
		}
	}

	private void BIPOFF() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'q', 1);
		// Amarino.sendDataToArduino(this, DEVICE_ ADDRESS,'w', 1);
		// Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'c', 1);
		// Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 't', 1);
		// Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'v', 1);
	}

	public void LCDOFFView(View v) {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'x', 1);
	}

	private void zmienLCD(View v) {
		// wyslanie do arduino zmiany wyswietlanego ekranu
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'Z', 1);
	}

	private void zerujWysok(View v) {
		// wyslanie do arduino zerowania wysokosci
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'Y', 1);
		wysbazowa = wys;
	}

	/*
	 * private double predkosc(double v1){ double v; v= return v; }
	 */

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if (loc != null) {
				/**
				 * Toast.makeText( getBaseContext(),
				 * "Lokalizacja uleg³a zmianie : Szerokoœæ geograficzna: " +
				 * loc.getLatitude() + " D³ugoœæ geograficzna: " +
				 * loc.getLongitude(), Toast.LENGTH_SHORT) .show();
				 **/
			
				latGPS.setText("" + loc.getLatitude());
				longGPS.setText("" + loc.getLongitude());
				spedGPS.setText("" + (loc.getSpeed()*3.6));
				wysokoscGPS.setText("" + threeDec.format(loc.getAltitude()));
				txtVeiew.setText("zmiana lokalizacji na nastêpuj¹ce wspó³rzêdne (szerokoœæ, d³ugoœæ):"
						+ loc.getLatitude() + ", " + loc.getLongitude());
				// txtVeiew.setText("Lat " + loc.getLatitude());
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
			double w = wys - wysbazowa;
			wysAndro.setText(threeDec.format(w));
			Log.d("wario_wysok", Double.toString(wys));
			Log.d("wario_wysokBazowa", Double.toString(wysbazowa));
			Log.d("wario_wysokdelta", Double.toString(wys - wysbazowa));

			// the device address from which the data was sent, we don't need it
			// here but to demonstrate how you retrieve it
			final String address = intent
					.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			Log.d("AMARINO_ADRESS", address);
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(
					AmarinoIntent.EXTRA_DATA_TYPE, -1);
			Log.d("AMARINO_DATATYPE", ""+dataType);
			// we only expect String data though, but it is better to check if
			// really string was sent
			// later Amarino will support differnt data types, so far data comes
			// always as string and
			// you have to parse the data to the type you have sent from
			// Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA) {
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				// Log.d("AMARINO IN", data);
				if (data != null) {
					// cisnienie.setText(data);
					try {

						// since we know that our string value is an int number
						// we can parse it to an integer

						String[] temp = data.split(";");
						if (temp[0].equals("wario")) {
							temperatura.setText(temp[1]);
							cisnienie.setText(temp[2]);
							wysokosc.setText(temp[3]);
							wys = Double.parseDouble(temp[3]);
							wysokosc1.setText(temp[4]);
							predkoscWario.setText(temp[5]);
						}

						//Log.d("AMARINO_CALOSC", data);

						// final double aDouble = Double.parseDouble(data);
						// mGraph.addDataPoint(sensorReading);
					
						long t0 = System.currentTimeMillis();
						float dt = (t0-t1)/1000;
						if (dt >1){
						double predAndroid=(wys-wys1)/dt;
						Log.d("ANDROID_PRED",dt+" - "+(wys-wys1)+" -> "+predAndroid);
						predAndr.setText(""+threeDec.format(predAndroid));
					/**		if (predAndroid>0){
								imgWysUP.setVisibility(0);
								imgWysDOW.setVisibility(4);
							}else
							{
								imgWysUP.setVisibility(4);
								imgWysDOW.setVisibility(0);
							}**/
					     wys1=wys;
						 t1=t0;
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