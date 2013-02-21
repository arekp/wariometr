package wario.arekp.pl;

//import wario.arekp.pl.WarioActivity.MyLocationListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class TabActiv extends TabActivity {
	private Button start;
	private Button stop;
	private Button buttonExportlot;
	
	//private ListView LotListView;
	private static final int DIALOG_ABOUT = 1;
	public static final String NEW_STEP = "wario.arekp.pl.NEW_STEP";
	private static final String TAG = "TABACTIV";
	private WarioDbAdapter myDBadapter;
	
//	private static final String DEVICE_ADDRESS = "00:11:12:14:00:93";
//	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
//	private LocationManager lm;
//	private LocationListener locationListener;
//	private DecimalFormat threeDec;

	private TextView komunikat;

//	 private TextView textAltGPS;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabactiv);

		komunikat = (TextView) findViewById(R.id.txtinfo);
//		 textAltGPS =(TextView) findViewById(R.id.TextAltWario);

		TabHost tabHost = getTabHost();
		//TabHost tabHost=(TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Start servis");

		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Wario 2");
		Intent in2 = new Intent(this, Scren2Activity.class);
		spec2.setContent(in2);

		TabSpec spec3 = tabHost.newTabSpec("Tab 3");
		// spec3.setIndicator("Tab 2",getResources().getDrawable(R.drawable.chart));
		Intent in3 = new Intent(this, WarioActivity.class);
		spec3.setContent(in3);
		spec3.setIndicator("Tablica");

		TabSpec spec4 = tabHost.newTabSpec("MAPA");
		// spec3.setIndicator("Tab 2",getResources().getDrawable(R.drawable.chart));
		Intent in4 = new Intent(this, MapaActivity.class);
		spec4.setContent(in4);
		spec4.setIndicator("MAPA");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);
		tabHost.setCurrentTab(0);

		//LotListView =(ListView)findViewById(R.id.listViewLot);
		myDBadapter = new WarioDbAdapter(this);
		myDBadapter.open();
		int ilo = myDBadapter.fetchAllLot().size();
		komunikat.setText("Masz zapisane  "+ilo+" lotów");
	      myDBadapter.close();
		
		start = (Button) findViewById(R.id.buttonStart);
		stop = (Button) findViewById(R.id.buttonStop);
		buttonExportlot = (Button) findViewById(R.id.buttonExportlot);
		
		buttonExportlot.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				copyDatabase2();
			}
		});
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startService(new Intent(TabActiv.this, WarioService.class));
				checkSerwiceWario();
			}
		});

		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("STOP", "Nacisnieto STOP");
				// Toast.makeText(this, "Nacisniety STOP",
				// Toast.LENGTH_LONG).show();
				stopService(new Intent(TabActiv.this, WarioService.class));
				checkSerwiceWario();
			}
		});

	}
	 
	private void checkSerwiceWario() {
		if (WarioService.isServiceAlive())
			komunikat.setText("Service started Us³uga w³¹czona? - W³¹czone ");
		else
			komunikat.setText("Service started (static field)? - Wy³¹czone");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			showDialog(DIALOG_ABOUT);
			Toast.makeText(getApplicationContext(), "Klikniêto ABOUT1",
					Toast.LENGTH_LONG).show();
			break;
		case R.id.prefer:
			startPreferencesActivity();
			Toast.makeText(getApplicationContext(), "Klikniêto prefer",
					Toast.LENGTH_LONG).show();
			break;
		case R.id.test_menu:
			startActivity(new Intent(this, TabActiv.class));
			Toast.makeText(getApplicationContext(), "Klikniêto MENU 2",
					Toast.LENGTH_LONG).show();
		default:
			break;
		}
		return true;
	}

	private void startPreferencesActivity() {
		Intent intent = new Intent(TabActiv.this, MyPreferences.class);
		startActivity(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_ABOUT:
			dialog = getAboutBox();
			break;

		default:
			dialog = null;
		}
		return dialog;
	}

	private AlertDialog getAboutBox() {

		String title = getString(R.string.app_name) + " ver 00.1 b";
		// getString(R.string.app_name) + " build " + getVersion(this);

		return new AlertDialog.Builder(this).setTitle(title)
				.setView(View.inflate(this, R.layout.about, null))
				.setIcon(R.drawable.logo).setPositiveButton("OK", null)
				.create();

	}

	@Override
	protected void onStart() {
		// registerReceiver(myReceiver, new IntentFilter(NEW_STEP));
		Log.d(TAG, "Start Screen2");
		super.onStart();

//		registerReceiver(arduinoReceiver, new IntentFilter(
//				AmarinoIntent.ACTION_RECEIVED));
//		Amarino.connect(this, DEVICE_ADDRESS);
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stop Screen2");
		myDBadapter.close();
		super.onStop();


	}

	@Override
	protected void onDestroy() {
		myDBadapter.close();
		super.onDestroy();
	}
//////////// Kopia bezpieczenstwa bazy ver 1
	public void copyDatabase2(){
	    try {
	    	Log.d("Export", "Zaczynamy");
	        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
	        {
	            Toast.makeText(getBaseContext(),"External Sd card not mounted", Toast.LENGTH_LONG).show();
	        }
	        
	        File sd = Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();
	        Log.d("Export", "Dane wejsciowe sd i data: "+sd.getCanonicalPath()+" data: "+data.getCanonicalPath());
	        if (sd.canWrite()) {
	        	Log.d("Export", "Mo¿emy zapisywaæ");
	            String currentDBPath = "/data/wario.arekp.pl/databases/wario_1.db";
	            String backupDBPath = "wario_1Export.db";
	            File currentDB = new File(data, currentDBPath);
	            File backupDB = new File(sd, backupDBPath);
	            Log.d("Export", "Przed sprawdzeniem istnienia bazy");
	            if (currentDB.exists()) {
	            	Log.d("Export", "Baza istnieje");
	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                Log.d("Export", "Przed kopiowanie,");
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                Log.d("Export", "Zakonczono kopiowanie");
	            }
	        }
	    } catch (Exception e) {
	    	Log.d("Export", "BLAD: "+e.getMessage());
	    }
	}
	public void copyDatabase() throws IOException 
    {
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            Toast.makeText(getBaseContext(),"External Sd card not mounted", Toast.LENGTH_LONG).show();
        }
        try
        {
        	Context contekst = getBaseContext();
            InputStream in= contekst.getAssets().open("Demo.db");
            File outFile =new File(Environment.getExternalStorageDirectory()+File.separator+ "Demo.db");
            outFile.createNewFile();
            OutputStream out= new FileOutputStream(outFile);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf))> 0)
            {
                out.write(buf,0,len);
            }
            out.close();
            in.close();
            Log.i("copyDatabase","Database Has been transferred");

        }
        catch(IOException e)
        {
            Log.i("CopyDatabase","could not copy database");
        }

    }
    public  boolean checkDatabase() 
    {   
        SQLiteDatabase checkdb=null;
    try
    {
        String myPath="/sdcard/demo.db";
        checkdb=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        Log.i("checkDatabase database path",checkdb.getPath());
    }
    catch(SQLiteException e)
    {
        Log.e("Database doesn`t exist",e.toString());
    }
    if(checkdb!=null)
    {
        checkdb.close();
    }
    return checkdb != null ? true : false;


    }
    public void openDatabase() throws SQLException
    {
        String myPath="/sdcard/demo.db";
        //myDatabase=SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
