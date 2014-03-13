package com.pappl.mambiances.db;

import java.util.Date;
import java.util.List;

public class Marqueur extends DAO{
	
	
	//Attributes
	/*
	 * long id for Marqueur
	 */
	private long marqueur_id;
	
	/*
	 * date of creation of Marqueur
	 */
	private Date marqueur_date_creation;
	
	/*
	 * date of modification of Marqueur
	 */
	private Date marqueur_date_derniere_edition;
	
	/*
	 * long id of Places associated to Marqueur
	 */
	private String places_id;
	
	/*
	 * long id of Utilisateurs associated to Marqueur
	 */
	private long utilisateur_id;
	
	
	//Constructor
		public Marqueur (){}
		
		public Marqueur (long monCompteUtilisateur, String monLieu, Date maDate){
			this.setUtilisateur_id(monCompteUtilisateur);
			this.setPlaces_id(monLieu);
			this.setMarqueur_date_creation(maDate);
			this.setMarqueur_date_derniere_edition(maDate);
		}
	
	
	
		/**
		 * Boolean attribute to know if the tag is already created in the local database or not
		 */
		protected Boolean registredInLocal = false;
	
	//Getters
		/**
		 * getter for the attribute registredInLocal
		 * @return Boolean
		 */
		public Boolean getRegistredInLocal() {
			return registredInLocal;
		}
		
		public long getMarqueur_id() {
			return marqueur_id;
		}


		public Date getMarqueur_date_creation() {
			return marqueur_date_creation;
		}
		
		public Date getMarqueur_date_derniere_edition() {
			return marqueur_date_derniere_edition;
		}
		
		public String getPlaces_id() {
			return places_id;
		}

		public long getUtilisateur_id() {
			return utilisateur_id;
		}
		//Setters
		/**
		 * setter for the attribute registredInLocal
		 * @param the value to insert
		 */
		public void setRegistredInLocal(Boolean registredInLocal) {
			this.registredInLocal = registredInLocal;
		}

		

		public void setMarqueur_id(long marqueur_id) {
			this.marqueur_id = marqueur_id;
		}

		public void setMarqueur_date_creation(Date marqueur_date_creation) {
			this.marqueur_date_creation = marqueur_date_creation;
		}

		public void setMarqueur_date_derniere_edition(
				Date marqueur_date_derniere_edition) {
			this.marqueur_date_derniere_edition = marqueur_date_derniere_edition;
		}
	
		public void setPlaces_id(String places_id) {
			this.places_id = places_id;
		}

		public void setUtilisateur_id(long utilisateur_id) {
			this.utilisateur_id = utilisateur_id;
		}

		@Override
		public void saveToLocal(LocalDataSource datasource) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return null;
		}
		
}
