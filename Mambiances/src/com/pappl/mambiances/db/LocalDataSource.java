package com.pappl.mambiances.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LocalDataSource {
	
	//Database fields
		/**
		 * database attributes which contains the database (need to pen and close it for each actions)
		 */
		private SQLiteDatabase database;

		/**
		 * database attributes which allow to create the database
		 */
		private MySQLiteHelper dbHelper;


		private String[] allColumnsMarqueur = {MySQLiteHelper.COLUMN_MARQUEURID, MySQLiteHelper.COLUMN_MOTID, MySQLiteHelper.COLUMN_CURSEURID, MySQLiteHelper.COLUMN_IMAGEID, MySQLiteHelper.COLUMN_MARQUEURDATECREATION, MySQLiteHelper.COLUMN_MARQUEURDATEDERNIEREEDITION, MySQLiteHelper.COLUMN_PLACESID, MySQLiteHelper.COLUMN_UTILISATEURID};
		private String[] allColumnsMots = {MySQLiteHelper.COLUMN_MOTID, MySQLiteHelper.COLUMN_MOT};
		private String[] allColumnsCurseur = {MySQLiteHelper.COLUMN_CURSEURID, MySQLiteHelper.COLUMN_CURSEURNOM, MySQLiteHelper.COLUMN_CURSEURVALEUR};
		private String[] allColumnsImage = {MySQLiteHelper.COLUMN_IMAGEID, MySQLiteHelper.COLUMN_IMAGEURL};
		private String[] allColumnsPlaces = {MySQLiteHelper.COLUMN_PLACESID, MySQLiteHelper.COLUMN_PLACESLATITUDE, MySQLiteHelper.COLUMN_PLACESNOM, MySQLiteHelper.COLUMN_PLACESLONGITUDE};
		private String[] allColumnsAdresse = {MySQLiteHelper.COLUMN_ADRESSEID, MySQLiteHelper.COLUMN_ADRESSERUE , MySQLiteHelper.COLUMN_ADRESSENUMERORUE, MySQLiteHelper.COLUMN_ADRESSEVILLE, MySQLiteHelper.COLUMN_ADRESSECODEPOSTAL};
		private String[] allColumnsAmbianceMot = {MySQLiteHelper.COLUMN_AMBIANCEMOTID, MySQLiteHelper.COLUMN_PLACESID,  MySQLiteHelper.COLUMN_AMBIANCEMOTMOT};
		private String[] allColumnsAmbianceCurseur = {MySQLiteHelper.COLUMN_AMBIANCECURSEURID, MySQLiteHelper.COLUMN_PLACESID, MySQLiteHelper.COLUMN_AMBIANCECURSEURNOM, MySQLiteHelper.COLUMN_AMBIANCECURSEURVALEUR};
		private String[] allColumnsUtilisateur = {MySQLiteHelper.COLUMN_UTILISATEURID, MySQLiteHelper.COLUMN_UTILISATEURLOGIN, MySQLiteHelper.COLUMN_UTILISATEURMDP};
		private String[] allColumnsAmis = {MySQLiteHelper.COLUMN_UTILISATEURID, MySQLiteHelper.COLUMN_UTILISATEURUTILISATEURID, MySQLiteHelper.COLUMN_AMIS};
		private String[] allColumnsFavoris = {MySQLiteHelper.COLUMN_UTILISATEURID, MySQLiteHelper.COLUMN_PLACESID, MySQLiteHelper.COLUMN_FAVORISDATE};
		private String[] allColumnsParametres = {MySQLiteHelper.COLUMN_PARAMETRESID, MySQLiteHelper.COLUMN_UTILISATEURID};

		//getters 
		/**
		 * getter for the MySQLiteHelper
		 * @return dbHelper
		 */
		public MySQLiteHelper getDbHelper() {
			return dbHelper;
		}

		/**
		 * getter for the SQLiteDatabase
		 * @return database
		 */
		public SQLiteDatabase getDatabase() {
			return database;
		}

		//constructor
		/**
		 * constructor only consists in creating dbHelper 
		 * @param context
		 */
		public LocalDataSource(Context context){
			dbHelper = new MySQLiteHelper(context);
		}

		/**
		 * Open database
		 * @throws SQLException
		 */
		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}

		/**
		 * close the database, need to be called to avoid any issue on the database treatment
		 */
		public void close(){
			dbHelper.close();
		}
		
		/**
		 * create a Marqueur
		 * 
		 */
		public Marqueur createMarqueur()
		
		/**
		 * update a Marqueur
		 */
		
		/**
		 * delete a Marqueur
		 */




}
