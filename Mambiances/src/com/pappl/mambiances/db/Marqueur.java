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
	 * long id of the Curseur associated to Marqueur
	 */
	private long curseur_id;
	
	/*
	 * List of Curseurs associated to Marqueur
	 */
	private List<Long> lesCurseurs;
	/*
	 * long id of the Image associated to Marqueur
	 */
	private long image_id;
	
	/*
	 * long id of a Mot associated to Marqueur
	 */
	private long mot_id;
	
	/*
	 * List of Mots associated to Marqueur
	 */
	private List<Long> lesMots;
	
	/*
	 * long id of Places associated to Marqueur
	 */
	private long places_id;
	
	/*
	 * long id of Utilisateurs associated to Marqueur
	 */
	private long utilisateur_id;
	
	
	//Constructor
		public Marqueur (){}
		
		public Marqueur (long monCompteUtilisateur, List<Long> mesMots, List<Long> mesCurseurs, long monImage, long monLieu, Date maDate){
			this.setUtilisateur_id(monCompteUtilisateur);
			this.setLesMots(mesMots);
			this.setLesCurseurs(mesCurseurs);
			this.setImage_id(monImage);
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
		
		public long getCurseur_id() {
			return curseur_id;
		}
		
		public List<Long> getLesCurseurs() {
			return lesCurseurs;
		}
		
		public long getImage_id() {
			return image_id;
		}
		
		public long getMot_id() {
			return mot_id;
		}
		
		public long getPlaces_id() {
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

		public void setCurseur_id(long curseur_id) {
			this.curseur_id = curseur_id;
		}

		public void setLesCurseurs(List<Long> lesCurseurs) {
			this.lesCurseurs = lesCurseurs;
		}

		public void setImage_id(long image_id) {
			this.image_id = image_id;
		}


		public void setMot_id(long mot_id) {
			this.mot_id = mot_id;
		}

		public List<Long> getLesMots() {
			return lesMots;
		}

		public void setLesMots(List<Long> lesMots) {
			this.lesMots = lesMots;
		}

	
		public void setPlaces_id(long places_id) {
			this.places_id = places_id;
		}

		public void setUtilisateur_id(long utilisateur_id) {
			this.utilisateur_id = utilisateur_id;
		}
		
}
