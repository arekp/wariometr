package wario.arekp.pl;

import java.util.Date;

public class Krok {
	
	public Krok() {
		super();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getLot_id() {
		return lot_id;
	}
	public void setLot_id(long lot_id) {
		this.lot_id = lot_id;
	}
	public String getNazwa() {
		return nazwa;
	}
	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}
	public String getOpis() {
		return opis;
	}
	public void setOpis(String opis) {
		this.opis = opis;
	}
	public Date getCzas() {
		return czas;
	}
	public void setCzas(Date czas) {
		this.czas = czas;
	}
	public double getGps_lat() {
		return gps_lat;
	}
	public void setGps_lat(double gps_lat) {
		this.gps_lat = gps_lat;
	}
	public double getGps_long() {
		return gps_long;
	}
	public void setGps_long(double gps_long) {
		this.gps_long = gps_long;
	}
	public double getGps_predkosc() {
		return gps_predkosc;
	}
	public void setGps_predkosc(double gps_predkosc) {
		this.gps_predkosc = gps_predkosc;
	}
	public double getGps_wysokosc() {
		return gps_wysokosc;
	}
	public void setGps_wysokosc(double gps_wysokosc) {
		this.gps_wysokosc = gps_wysokosc;
	}
	public double getWario_cisn() {
		return wario_cisn;
	}
	public void setWario_cisn(double wario_cisn) {
		this.wario_cisn = wario_cisn;
	}
	public double getWario_wysokosc() {
		return wario_wysokosc;
	}
	public void setWario_wysokosc(double wario_wysokosc) {
		this.wario_wysokosc = wario_wysokosc;
	}
	public double getWario_wysokosc1() {
		return wario_wysokosc1;
	}
	public void setWario_wysokosc1(double wario_wysokosc1) {
		this.wario_wysokosc1 = wario_wysokosc1;
	}
	public double getWario_temper() {
		return wario_temper;
	}
	public void setWario_temper(double wario_temper) {
		this.wario_temper = wario_temper;
	}
	public double getWario_predkosc() {
		return wario_predkosc;
	}
	public void setWario_predkosc(double wario_predkosc) {
		this.wario_predkosc = wario_predkosc;
	}
	public Krok(long lot_id, String nazwa, String opis, Date czas,
			double gps_lat, double gps_long, double gps_predkosc,
			double gps_wysokosc, double wario_cisn, double wario_wysokosc,double wario_wysokosc1,
			double wario_temper, double wario_predkosc) {
		super();
		//this.id = id;
		this.lot_id = lot_id;
		this.nazwa = nazwa;
		this.opis = opis;
		this.czas = czas;
		this.gps_lat = gps_lat;
		this.gps_long = gps_long;
		this.gps_predkosc = gps_predkosc;
		this.gps_wysokosc = gps_wysokosc;
		this.wario_cisn = wario_cisn;
		this.wario_wysokosc = wario_wysokosc;
		this.wario_wysokosc1 = wario_wysokosc1;
		this.wario_temper = wario_temper;
		this.wario_predkosc = wario_predkosc;
	}
	
	public long id;
	public long lot_id;
	public String nazwa;
	public String opis;
	public Date czas;
	public double gps_lat;
	public double gps_long;
	public double gps_predkosc;
	public double gps_wysokosc;
	public double wario_cisn;
	public double wario_wysokosc;
	public double wario_wysokosc1;
	public double wario_temper;
	public double wario_predkosc;
	

}
