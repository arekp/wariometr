package wario.arekp.pl;

import java.util.Date;

public class Lot {

	public Lot(long _id, String name, Date czas,String gora_address, double gora_lat,
			double gora_long) {
		super();
		this._id = _id;
		this.name = name;
		this.czas=czas;
		this.gora_address = gora_address;
		this.gora_lat = gora_lat;
		this.gora_long = gora_long;
	}
	
	public long _id;
	public String name;
	public Date czas;
	public String gora_address;
	public double gora_lat;
	public double gora_long;
	
}
