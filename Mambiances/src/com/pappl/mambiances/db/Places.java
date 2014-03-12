package com.pappl.mambiances.db;

public class Places {

		//Attributes
		private long places_id;
		
		private String places_nom;
		
		private double places_latitude;
		
		private double places_longitude;
		
		private long adresse_id;
		
	//Constructor
		public Places() {}
		
		public Places(long places_id, String places_nom, double places_latitude,
				double places_longitude, long adresse_id) {
			super();
			this.places_id = places_id;
			this.places_nom = places_nom;
			this.places_latitude = places_latitude;
			this.places_longitude = places_longitude;
			this.adresse_id = adresse_id;
		}
	
	//Getters and setters
		public long getPlaces_id() {
			return places_id;
		}

		public void setPlaces_id(long places_id) {
			this.places_id = places_id;
		}

		public String getPlaces_nom() {
			return places_nom;
		}

		public void setPlaces_nom(String places_nom) {
			this.places_nom = places_nom;
		}

		public double getPlaces_latitude() {
			return places_latitude;
		}

		public void setPlaces_latitude(double places_latitude) {
			this.places_latitude = places_latitude;
		}

		public double getPlaces_longitude() {
			return places_longitude;
		}

		public void setPlaces_longitude(double places_longitude) {
			this.places_longitude = places_longitude;
		}

		public long getAdresse_id() {
			return adresse_id;
		}

		public void setAdresse_id(long adresse_id) {
			this.adresse_id = adresse_id;
		}

}
