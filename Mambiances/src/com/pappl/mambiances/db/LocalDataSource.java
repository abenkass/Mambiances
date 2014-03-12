package com.pappl.mambiances.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public  class LocalDataSource {
	
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
			this.dbHelper = new MySQLiteHelper(context);
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
		
		//Methodes

		/**
		 * method to create a Utilisateur
		 * @param login is the login of Utilisateur
		 * @param mdp is the mdp of Utilisateur
		 */
		public Utilisateur createUtilisateur (String login, String mdp){
			ContentValues values = new ContentValues(); 
			values.put(MySQLiteHelper.COLUMN_UTILISATEURLOGIN, login);
			values.put(MySQLiteHelper.COLUMN_UTILISATEURMDP, mdp);
			long insertId = database.insert(MySQLiteHelper.TABLE_UTILISATEUR, null, values);
			//TODO check the utily of autoincrement
			Cursor cursor = 
					database.query(
							MySQLiteHelper.TABLE_UTILISATEUR,
							allColumnsUtilisateur,
							MySQLiteHelper.COLUMN_UTILISATEURID+" = "+insertId,
							null, null, null, null);
			cursor.moveToFirst();
			Utilisateur newUtilisateur = cursorToUtilisateur(cursor);//method at the end of the class
			cursor.close();
			return newUtilisateur;
		}
		/**
		 * overload of previous method
		 * creating a new Utilisateur in the database
		 * @param id is the utilisateur_id 
		 * @param login is the utilisateur login
		 * @param mdp is the utilisateur mdp 
		 * @return utilisateur is the created utilisateur
		 */
		public Utilisateur createUtilisateur (long id, String login, String mdp){
	        Boolean exist = existUtilisateurWithId(id);
	        
	        if(exist == true){
	        	Utilisateur existUtilisateur = getUtilisateurWithId(id);
	        	Utilisateur updatedUtilisateur = updateUtilisateur(existUtilisateur, login, mdp);
	            return updatedUtilisateur;
	        }
	        else {
	            ContentValues values = new ContentValues();
	            values.put(MySQLiteHelper.COLUMN_UTILISATEURID, id);
	            values.put(MySQLiteHelper.COLUMN_UTILISATEURLOGIN, login);
	            values.put(MySQLiteHelper.COLUMN_UTILISATEURMDP, mdp);

	            long insertId = database.insert(MySQLiteHelper.TABLE_UTILISATEUR, null,values);
	            Cursor cursor = database.query(MySQLiteHelper.TABLE_UTILISATEUR,
	                    allColumnsUtilisateur, MySQLiteHelper.COLUMN_UTILISATEURID + " = " + insertId, null,null, null, null);
	            cursor.moveToFirst();
	            Utilisateur u2 = cursorToUtilisateur(cursor);
	            cursor.close();
	            return u2;
	        }
	    }

		/**
		 * update a Utilisateur
		 * @param id 
		 * @param utilisateur we ant to update
		 * @param descr we want to change for
		 * @return utilisateur updated
		 */
		public Utilisateur updateUtilisateur(Utilisateur utilisateur, String descr, String mdp){
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_UTILISATEURLOGIN, descr);
			values.put(MySQLiteHelper.COLUMN_UTILISATEURMDP, mdp);

			database.update(MySQLiteHelper.TABLE_UTILISATEUR, values, MySQLiteHelper.COLUMN_UTILISATEURID + " = " +utilisateur.getUtilisateur_id(), null);
			return getUtilisateurWithId(utilisateur.getUtilisateur_id());
	 }


		/**
		 * knowing a Utilisateur_id, we want to get the utilisateur itself
		 * @param id is the id of the utilisateur we are looking for
		 * @return u1 is the utilisateur we were looking for
		 */
	    public Utilisateur getUtilisateurWithId(Long id){
	        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR, allColumnsUtilisateur, MySQLiteHelper.COLUMN_UTILISATEURID + " = \"" + id +"\"", null, null, null, null);
	        c.moveToFirst();
	        Utilisateur u1 = cursorToUtilisateur(c);
	        c.close();
	        return u1;
	    }

		/**
		 * knowing an id we test if this utilisateur exists
		 * @param id is the id of the utilisateur we ask
		 * @return boolean says if the project with this id exists or not
		 */
	    public Boolean existUtilisateurWithId(Long id){
	        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR, allColumnsUtilisateur, MySQLiteHelper.COLUMN_UTILISATEURID + " = \"" + id +"\"", null, null, null, null);
	        if(c.getCount()>0){
	            c.close();
	            return true;
	        }
	        else {
	            c.close();
	            return false;
	        }
	    }
	    /**
		 * knowing an login we test if this utilisateur exists
		 * @param login is the login of the utilisateur we ask
		 * @return boolean says if the utilisateur with this login exists or not
		 */
	    public Boolean existUtilisateurWithLogin(String login){
	        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR, allColumnsUtilisateur, MySQLiteHelper.COLUMN_UTILISATEURLOGIN + " = \"" + login +"\"", null, null, null, null);
	        if(c.getCount()>0){
	            c.close();
	            return true;
	        }
	        else {
	            c.close();
	            return false;
	        }
	    }
	    
	    /**
		 * knowing login and mdp we test if this utilisateur authentificates
		 * @param login is the login of the utilisateur we ask
		 * @param mdp is the mot de passe of the utilisateur we ask

		 * @return boolean says if the utilisateur with this login exists or not
		 */
	    public Boolean correctUtilisateur(String login, String mdp){
	        Cursor c = database.rawQuery("select " + MySQLiteHelper.COLUMN_UTILISATEURLOGIN +"and" +MySQLiteHelper.COLUMN_UTILISATEURMDP + " from " + MySQLiteHelper.TABLE_UTILISATEUR + " where = ?", new String[]{"login", "mdp"});
	        if(c.getCount()>0){
	            c.close();
	            return true;
	        }
	        else {
	            c.close();
	            return false;
	        }
	    }

	    /**
	     * deleting a Utilisateur
	     * @param u1 is the project we want to delete
	     */
		public void deleteUtilisateur(Utilisateur u1){
			long id = u1.getUtilisateur_id();
			System.out.println("Utilisateur deleted with id: "+ id);
			database.delete(MySQLiteHelper.TABLE_UTILISATEUR, MySQLiteHelper.COLUMN_UTILISATEURID+" = "+ id, null);
		}
		
		
		/**
		 * convert a cursor to a utilisateur
		 * @param cursor
		 * @return utilisateur 
		 */
		private Utilisateur cursorToUtilisateur(Cursor cursor) {
		    Utilisateur u1 = new Utilisateur();
		    u1.setUtilisateur_id(cursor.getLong(0));
		    u1.setUtilisateur_login(cursor.getString(1));
		    u1.setUtilisateur_mdp(cursor.getString(2));
		    return u1;
		}
		
		  /**
		   * Getting user login status
		   * return true if rows are there in table
		   * */
		  public int getRowCount() {
		    String countQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_UTILISATEUR;
		    Cursor cursor = database.rawQuery(countQuery, null);
		    int rowCount = cursor.getCount();
		    database.close();
		    cursor.close();
		    // return row count
		    return rowCount;
		  }
}
