package com.pappl.mambiances.db;

import android.content.ContentValues;
import android.database.Cursor;

public class Utilisateur extends DAO{
	//Attributes
		/*
		 * long id for Utilisateur
		 */
		private long utilisateur_id;
		

		/*
		 * String login for Utilisateur
		 */
		private String utilisateur_login;
		
		/*
		 * String mdp for Utilisateur
		 */
		private String utilisateur_mdp;

		
		//Constructor
			public Utilisateur (){}
			
			public Utilisateur (long monCompteUtilisateur, String monLogin, String monMdp ){
				this.setUtilisateur_id(monCompteUtilisateur);
				this.setUtilisateur_login(monLogin);
				this.setUtilisateur_mdp(monMdp);
			}
			
		//Getters and Setters
			public long getUtilisateur_id() {
				return utilisateur_id;
			}

			public void setUtilisateur_id(long utilisateur_id) {
				this.utilisateur_id = utilisateur_id;
			}

			public String getUtilisateur_login() {
				return utilisateur_login;
			}

			public void setUtilisateur_login(String utilisateur_login) {
				this.utilisateur_login = utilisateur_login;
			}

			public String getUtilisateur_mdp() {
				return utilisateur_mdp;
			}

			public void setUtilisateur_mdp(String utilisateur_mdp) {
				this.utilisateur_mdp = utilisateur_mdp;
			}
		

			//Methods
			/**
			 * method to save le Compte utilisateur in the Database
			 * @param datasource is the Localdatasource of which Utilisateur is a table
			 */
			public void saveToLocal(LocalDataSource datasource){
				ContentValues values = new ContentValues(); 

				values.put(MySQLiteHelper.COLUMN_UTILISATEURLOGIN, this.utilisateur_login);
				
				//pour l'instant on en prend pas en compte le cas de la synchronisation avec l'ext.}
				 
				//!!!if(this.registredInLocal){
					String str = "utilisateur_id "+"="+this.utilisateur_id;
					datasource.getDatabase().update(MySQLiteHelper.TABLE_UTILISATEUR, values, str, null);
				
				/*!!!else{
					Cursor cursor = datasource.getDatabase().rawQuery(GETMAXUTILISATEURID, null);
					cursor.moveToFirst();
					if(!cursor.isAfterLast()){
						long old_id = this.getUtilisateur_id();
						long new_id = 1+this.utilisateur_id+Sync.getMaxId().get("Utilisateur");
						this.setUtilisateur_id(new_id);
						this.trigger(old_id, new_id, MainActivity.composed);
					}
					values.put(MySQLiteHelper.COLUMN_UTILISATEURID, this.utilisateur_id);
					datasource.getDatabase().insert(MySQLiteHelper.TABLE_UTILISATEUR, null, values);
					this.setRegistredInLocal(true);
				}*/
			}
			
			/**
			 * query to get the biggest utilisateur_id from local db
			 * 
			 */
			private static final String
				GETMAXUTILISATEURID = 
					"SELECT "+MySQLiteHelper.TABLE_UTILISATEUR+"."+MySQLiteHelper.COLUMN_UTILISATEURID+" FROM "
					+ MySQLiteHelper.TABLE_UTILISATEUR 
					+" ORDER BY "+MySQLiteHelper.TABLE_UTILISATEUR+"."+MySQLiteHelper.COLUMN_UTILISATEURID
					+" DESC LIMIT 1 ;"
				;


			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return null;
			}
	}
