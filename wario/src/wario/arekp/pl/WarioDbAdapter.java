package wario.arekp.pl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import at.abraxas.amarino.log.Logger;

public class WarioDbAdapter {
	public static final String KEY_LOT_ID = "_id";
	public static final String KEY_LOT_NAME = "name";
	public static final String KEY_LOT_ADDRESS = "lot_address";
	public static final String KEY_LOT_DATE = "lot_date";
	public static final String KEY_LOT_LAT = "lot_lat";
	public static final String KEY_LOT_LONG = "lot_long";
	
	
	public static final String KEY_KROK_ID = "_id";
	public static final String KEY_KROK_LOT_ID = "lot_id";
	public static final String KEY_KROK_NAME = "nazwa";
	public static final String KEY_KROK_DESC = "opis";
	public static final String KEY_KROK_TIME = "czas";
	public static final String KEY_KROK_GPS_LAT = "gps_lat";
	public static final String KEY_KROK_GPS_LONG = "gps_long";
	public static final String KEY_KROK_GPS_SPEED = "gps_predkosc";
	public static final String KEY_KROK_GPS_WYS = "gps_wysokosc";
	public static final String KEY_KROK_WARIO_CISN = "wario_cisn";
	public static final String KEY_KROK_WARIO_WYS = "wario_wysokosc";
	public static final String KEY_KROK_WARIO_WYS1 = "wario_wysokosc1";
	public static final String KEY_KROK_WARIO_TEMP = "wario_temper";
	public static final String KEY_KROK_WARIO_SPEED = "wario_predkosc";
	
	
	private static final boolean DEBUG = true;
	private static final String TAG = "WarioDbAdapter";
	private static final int DATABASE_VERSION = 2;
	
	
	private static final String DATABASE_NAME = "wario_1.db";
	private static final String LOT_TABLE_NAME = "LOT_tbl";
	private static final String KROK_TABLE_NAME = "KROK_tbl";
	

	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
	static Log logi;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.d(TAG, "create database tables");
        	/* Create Devices Table */
        	db.execSQL("CREATE TABLE " + LOT_TABLE_NAME + " ("
                    + KEY_LOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_LOT_ADDRESS + " TEXT,"
                    + KEY_LOT_DATE+ " DATE,"
                    + KEY_LOT_NAME  + " TEXT,"
                    + KEY_LOT_LAT  + " REAL,"
                    + KEY_LOT_LONG  + " REAL"
                    + ");");
        	
        	db.execSQL("CREATE TABLE " + KROK_TABLE_NAME + " ("
        			+ KEY_KROK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        			+ KEY_KROK_NAME + " TEXT NOT NULL,"
        			+ KEY_KROK_DESC + " TEXT ,"
        			+ KEY_KROK_TIME + " DATE," // ciekawe tutaj wstawiamy DATE
        			+ KEY_KROK_GPS_LAT + " REAL ,"
        			+ KEY_KROK_GPS_LONG + " REAL ,"
        			+ KEY_KROK_GPS_SPEED + " REAL ,"
        			+ KEY_KROK_GPS_WYS + " REAL ,"
        			+ KEY_KROK_WARIO_CISN + " INTEGER ,"
        			+ KEY_KROK_WARIO_TEMP + " REAL ,"
        			+ KEY_KROK_WARIO_WYS + " REAL ,"
        			+ KEY_KROK_WARIO_WYS1 + " REAL ,"        			
        			+ KEY_KROK_WARIO_SPEED + " REAL ,"
                    + KEY_KROK_LOT_ID  + " INTEGER REFERENCES " + LOT_TABLE_NAME + "(_id) "
                    + ");");
        	
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            
            db.execSQL("DROP TABLE IF EXISTS " + LOT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + KROK_TABLE_NAME);
            onCreate(db);
            Log.d(TAG, "upgrade db");
        }
        
	}

	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public WarioDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public WarioDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    
    /**
     * Create a new device using the address and name provided. If the device is
     * successfully created return the new rowId for that device, otherwise return
     * a -1 to indicate failure.
     * 
     * @param address the address of the device
     * @param name the name of the device
     * @return rowId or -1 if failed
     */
    public long createLOT(Krok krok) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	Date date = new Date();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LOT_ADDRESS, "");
        initialValues.put(KEY_LOT_NAME, (krok.nazwa == null) ? "Przypadkowa" : krok.nazwa);
        initialValues.put(KEY_LOT_DATE,dateFormat.format(date));
        initialValues.put(KEY_LOT_LAT, krok.gps_lat);
        initialValues.put(KEY_LOT_LONG, krok.gps_long);
        Log.d("BAZA","zapisujemy LOT");
        return mDb.insert(LOT_TABLE_NAME, null, initialValues);
    }
    
    /**
     * Delete the device with the given rowId
     * 
     * @param rowId id of device to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLOT(long LOTId) {
    	boolean numKROK = deleteKROK(LOTId); 
    	if (DEBUG) Log.d(TAG, "delete device with id " + LOTId + ": " + numKROK + " associated events removed");
        return mDb.delete(LOT_TABLE_NAME, KEY_LOT_ID + "=" + LOTId, null) > 0;
    }
    
    public Lot getLot(String address){
    	Lot lot = null;
    	Cursor c = mDb.query(LOT_TABLE_NAME, null, KEY_LOT_ADDRESS + " like ?", new String[]{address}, null, null, null);

        if (c == null){
        	return null;
        }
        if (c.moveToFirst()){	
    		String name = c.getString(c.getColumnIndex(KEY_LOT_NAME));
    		String LOT_address = c.getString(c.getColumnIndex(KEY_LOT_ADDRESS));
    		//Date date = c.get getLong(c.getColumnIndex(KEY_LOT_DATE));
    		long id = c.getLong(c.getColumnIndex(KEY_LOT_ID));
    		double LOT_lat = c.getDouble(c.getColumnIndex(KEY_LOT_LAT));
    		double LOT_long = c.getDouble(c.getColumnIndex(KEY_LOT_LONG));
    		lot = new Lot(id, name, null,LOT_address, LOT_lat, LOT_long);
        }
        c.close();
    	return lot;
    }
    public Cursor  getAllLot() {
    	String[] columns = new String[]{KEY_LOT_NAME};
    	Cursor c = mDb.query(LOT_TABLE_NAME, columns, null, null, null, null, null);
    	return c;
    }
    
    public ArrayList<Lot> fetchAllLot() {
    	ArrayList<Lot> lot = new ArrayList<Lot>();
    	
        Cursor c = mDb.query(LOT_TABLE_NAME, null, null, null, null, null, null);

        if (c == null){
        	return lot;
        }
        if (c.moveToFirst()){	
        	do {
        		String LOT_address = c.getString(c.getColumnIndex(KEY_LOT_ADDRESS));
        		String name = c.getString(c.getColumnIndex(KEY_LOT_NAME));
        		long id = c.getLong(c.getColumnIndex(KEY_LOT_ID));
        		double LOT_lat = c.getDouble(c.getColumnIndex(KEY_LOT_LAT));
        		double LOT_long = c.getDouble(c.getColumnIndex(KEY_LOT_LONG));
        		lot.add(new Lot(id, name,null, LOT_address, LOT_lat, LOT_long));
        	}
        	while(c.moveToNext());
        }
        c.close();
        return lot;
    }
    
    
    /**
     * Create a new event associated with a specific device. If the event is
     * successfully created return the new rowId for that event, otherwise return
     * a -1 to indicate failure.
     * 
     * @param address the address of the device
     * @param name the name of the device
     * @return rowId or -1 if failed
     */
    public long createKROK(Krok KROK) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	Date date = new Date();
        ContentValues initialValues = new ContentValues();
        
        initialValues.put(KEY_KROK_NAME, KROK.nazwa);
        initialValues.put(KEY_KROK_DESC, KROK.opis);
        initialValues.put(KEY_KROK_GPS_LAT, KROK.gps_lat);
        initialValues.put(KEY_KROK_GPS_LONG, KROK.gps_long);
        initialValues.put(KEY_KROK_GPS_SPEED, KROK.gps_predkosc);
        initialValues.put(KEY_KROK_GPS_WYS, KROK.gps_wysokosc);
        initialValues.put(KEY_KROK_TIME,dateFormat.format(date));
        initialValues.put(KEY_KROK_WARIO_CISN, KROK.wario_cisn);
        initialValues.put(KEY_KROK_WARIO_SPEED, KROK.wario_predkosc);
        initialValues.put(KEY_KROK_WARIO_TEMP, KROK.wario_temper);
        initialValues.put(KEY_KROK_WARIO_WYS, KROK.wario_wysokosc);
        initialValues.put(KEY_KROK_WARIO_WYS1, KROK.wario_wysokosc1);
        
        initialValues.put(KEY_KROK_LOT_ID,KROK.lot_id);
        Log.d("BAZA","zapisujemy KROK dla lotu "+KROK.lot_id);
        return mDb.insert(KROK_TABLE_NAME, null, initialValues);
    }
    
    
    /**
     * Delete the event with the given rowId
     * 
     * @param rowId id of event to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteKROK(long rowId) {
        return mDb.delete(KROK_TABLE_NAME, KEY_KROK_ID+ "=" + rowId, null) > 0;
    }
    
    /**
     * Delete all events associated to the given device
     * @param deviceId
     * @return
     */
    public int deleteKROKs(long LOTId){
    	return mDb.delete(KROK_TABLE_NAME, KEY_KROK_LOT_ID + "=" + LOTId, null);
    }
 /**   
    public Event getEvent(long deviceId, int pluginId){
    	Event e = null;
    	Cursor c = mDb.query(EVENT_TABLE_NAME, null, 
    			KEY_EVENT_DEVICE_ID + "=? AND " + KEY_EVENT_PLUGIN_ID + "=?", 
        		new String[]{String.valueOf(deviceId), String.valueOf(pluginId)}, null, null, null);
    	
    	if (c == null){
        	if (DEBUG) Logger.d(TAG, "no event found for device with id: " + deviceId + " and pluginId:" + pluginId);
        	return null;
        }
        if (c.moveToFirst()){
 	    	long id = c.getLong(c.getColumnIndex(KEY_EVENT_ID));
			String name = c.getString(c.getColumnIndex(KEY_EVENT_NAME));
			String desc = c.getString(c.getColumnIndex(KEY_EVENT_DESC));
			int visualizer = c.getInt(c.getColumnIndex(KEY_EVENT_VISUALIZER));
			float minVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MIN));
			float maxVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MAX));
			char flag = (char) c.getInt(c.getColumnIndex(KEY_EVENT_FLAG));
			String packageName= c.getString(c.getColumnIndex(KEY_EVENT_PACKAGE_NAME));
			String editClassName= c.getString(c.getColumnIndex(KEY_EVENT_EDIT_CLASS_NAME));
			String serviceClassName= c.getString(c.getColumnIndex(KEY_EVENT_SERVICE_CLASS_NAME));

			e = new Event(id, name, desc, visualizer, flag, packageName, editClassName,
					serviceClassName, pluginId, deviceId);
			e.visualizerMinValue = minVal;
			e.visualizerMaxValue = maxVal;
        }
        
        c.close();
		return e;
    }
    **/
    /**
     * Return a list of all events for a given device
     *  
     * @return ArrayList of all events for the given device
     */
   /**
    public ArrayList<KROK> fetchEvents(long LOTId) {
    	ArrayList<KROK> KROKs = new ArrayList<KROK>();
    	
        Cursor c = mDb.query(KROK_TABLE_NAME, null, KEY_KROK_LOT_ID + "=" + LOTId , 
        		null, null, null, null);

        if (c == null){
        	if (DEBUG) Logger.d(TAG, "no events found for device with id: " + KROKs.id);
        	return KROKs;
        }
        if (c.moveToFirst()){	
        	do {
        		long id = c.getLong(c.getColumnIndex(KEY_EVENT_ID));
        		String name = c.getString(c.getColumnIndex(KEY_EVENT_NAME));
        		String desc = c.getString(c.getColumnIndex(KEY_EVENT_DESC));
        		int visualizer = c.getInt(c.getColumnIndex(KEY_EVENT_VISUALIZER));
        		float minVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MIN));
        		float maxVal = c.getFloat(c.getColumnIndex(KEY_EVENT_VISUALIZER_MAX));
        		char flag = (char) c.getInt(c.getColumnIndex(KEY_EVENT_FLAG));
        		String packageName= c.getString(c.getColumnIndex(KEY_EVENT_PACKAGE_NAME));
        		String editClassName= c.getString(c.getColumnIndex(KEY_EVENT_EDIT_CLASS_NAME));
        		String serviceClassName= c.getString(c.getColumnIndex(KEY_EVENT_SERVICE_CLASS_NAME));
        		int pluginId = c.getInt(c.getColumnIndex(KEY_EVENT_PLUGIN_ID));
        		
        		Event e = new Event(id, name, desc, visualizer, flag, packageName, editClassName,
        				serviceClassName, pluginId, deviceId);
        		e.visualizerMinValue = minVal;
        		e.visualizerMaxValue = maxVal;
        		
        		events.add(e);
        		if (DEBUG) Logger.d(TAG, "event found: " + e.name + " - id=" + e.pluginId);
        	}
        	while(c.moveToNext());
        }
        else {
        	if (DEBUG) Logger.d(TAG, "no events found for device with id: " + deviceId);
        }

        c.close();
        return events;
    }
  **/  
    
    public int updateKROK(Krok KROK){
    	
    	ContentValues values = new ContentValues();
    	
    	values.put(KEY_KROK_NAME, KROK.nazwa);
    	values.put(KEY_KROK_DESC, KROK.opis);
    	values.put(KEY_KROK_GPS_LAT, KROK.gps_lat);
    	values.put(KEY_KROK_GPS_LONG, KROK.gps_long);
    	values.put(KEY_KROK_GPS_SPEED, KROK.gps_predkosc);
    	values.put(KEY_KROK_GPS_WYS, KROK.gps_wysokosc);
    //	values.put(KEY_KROK_TIME, KROK.czas);
    	values.put(KEY_KROK_WARIO_CISN, KROK.wario_cisn);
    	values.put(KEY_KROK_WARIO_SPEED, KROK.wario_predkosc);
    	values.put(KEY_KROK_WARIO_TEMP, KROK.wario_temper);
    	values.put(KEY_KROK_WARIO_WYS, KROK.wario_wysokosc);


    	return mDb.update(KROK_TABLE_NAME, values, KEY_KROK_ID + "=" + KROK.id, null);
    }
    
}
