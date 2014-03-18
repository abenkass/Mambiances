package com.pappl.mambiances.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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


		private String[] allColumnsMarqueur = {MySQLiteHelper.COLUMN_MARQUEURID, MySQLiteHelper.COLUMN_MARQUEURDATECREATION, MySQLiteHelper.COLUMN_MARQUEURDATEDERNIEREEDITION, MySQLiteHelper.COLUMN_PLACESID, MySQLiteHelper.COLUMN_UTILISATEURID};
		private String[] allColumnsMots = {MySQLiteHelper.COLUMN_MOTID, MySQLiteHelper.COLUMN_MOT, MySQLiteHelper.COLUMN_MARQUEURID,};
		private String[] allColumnsCurseur = {MySQLiteHelper.COLUMN_CURSEURID, MySQLiteHelper.COLUMN_CURSEURVALEUR, MySQLiteHelper.COLUMN_CURSEURNOM, MySQLiteHelper.COLUMN_MARQUEURID,};
		private String[] allColumnsImage = {MySQLiteHelper.COLUMN_IMAGEID, MySQLiteHelper.COLUMN_IMAGEURL, MySQLiteHelper.COLUMN_MARQUEURID,};
		private String[] allColumnsPlaces = {MySQLiteHelper.COLUMN_PLACESID, MySQLiteHelper.COLUMN_PLACESNOM, MySQLiteHelper.COLUMN_PLACESLATITUDE, MySQLiteHelper.COLUMN_PLACESLONGITUDE, MySQLiteHelper.COLUMN_ADRESSEID};
		private String[] allColumnsAdresse = {MySQLiteHelper.COLUMN_ADRESSEID,  MySQLiteHelper.COLUMN_ADRESSENOM/*MySQLiteHelper.COLUMN_ADRESSERUE , MySQLiteHelper.COLUMN_ADRESSENUMERORUE, MySQLiteHelper.COLUMN_ADRESSEVILLE, MySQLiteHelper.COLUMN_ADRESSECODEPOSTAL*/};
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
	        
	        if(exist){
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
		 * knowing a utilisateur_login, we want to get the utilisateur itself
		 * @param id is the id of the utilisateur we are looking for
		 * @return u1 is the utilisateur we were looking for
		 */
	    public Utilisateur getUtilisateurWithLogin(String login){
	        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR, allColumnsUtilisateur, MySQLiteHelper.COLUMN_UTILISATEURLOGIN + " = \"" + login +"\"", null, null, null, null);
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
	        //Cursor c = database.rawQuery("select " + MySQLiteHelper.COLUMN_UTILISATEURLOGIN +", " +MySQLiteHelper.COLUMN_UTILISATEURMDP + " from " + MySQLiteHelper.TABLE_UTILISATEUR + " where = ?", new String[]{"login", "mdp"});
	        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR, allColumnsUtilisateur, MySQLiteHelper.COLUMN_UTILISATEURID + "=? AND " + MySQLiteHelper.COLUMN_UTILISATEURMDP + "=?", new String[]{"login", "mdp"}, null, null, null);
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
	     * @param u1 is the utilisateur we want to delete
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
		  
		  /**
		   * Méthode pour créer un curseur
		   */
		  
		  public Curseur createCurseur (int valeur, String nom, long marqueurId){
				ContentValues values = new ContentValues(); 
				values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, valeur);
				values.put(MySQLiteHelper.COLUMN_CURSEURNOM, nom);
				values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueurId);
				long insertId = database.insert(MySQLiteHelper.TABLE_CURSEUR, null, values);
				//TODO check the utily of autoincrement
				Cursor cursor = 
						database.query(
								MySQLiteHelper.TABLE_CURSEUR,
								allColumnsCurseur,
								MySQLiteHelper.COLUMN_CURSEURID+" = "+insertId,
								null, null, null, null);
				cursor.moveToFirst();
				Curseur newCurseur = cursorToCurseur(cursor);//method at the end of the class
				cursor.close();
				return newCurseur;
			}
		  
		  /**
			 * overload of previous method
			 * creating a new Curseur in the database
			 * @return curseur is the created curseur
			 */
			public Curseur createCurseur (long id, int valeur, String nom, long marqueurId){
		        Boolean exist = existCurseurWithId(id);
		        
		        if(exist){
		        	Curseur existCurseur = getCurseurWithId(id);
		        	Curseur updatedCurseur = updateCurseur(existCurseur, valeur);
		            return updatedCurseur;
		        }
		        else {
		            ContentValues values = new ContentValues();
		            values.put(MySQLiteHelper.COLUMN_CURSEURID, id);
		            values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, valeur);
		            values.put(MySQLiteHelper.COLUMN_CURSEURNOM, nom);
		            values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueurId);

		            long insertId = database.insert(MySQLiteHelper.TABLE_CURSEUR, null,values);
		            Cursor cursor = database.query(MySQLiteHelper.TABLE_CURSEUR,
		                    allColumnsCurseur, MySQLiteHelper.COLUMN_CURSEURID + " = " + insertId, null,null, null, null);
		            cursor.moveToFirst();
		            Curseur c2 = cursorToCurseur(cursor);
		            cursor.close();
		            return c2;
		        }
		    }

			/**
			 * update a Curseur
			 * @return curseur updated
			 */
			
			//TODO après test, voir s'il faut ajouter le marqueurId
			
			public Curseur updateCurseur(Curseur curseur, int valeur){
				ContentValues values = new ContentValues();
				values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, valeur);

				database.update(MySQLiteHelper.TABLE_CURSEUR, values, MySQLiteHelper.COLUMN_CURSEURID + " = " +curseur.getCurseur_id(), null);
				return getCurseurWithId(curseur.getCurseur_id());
		 }


			/**
			 * knowing a Curseur_id, we want to get the curseur itself
			 * @param id is the id of the curseur we are looking for
			 * @return c1 is the curseur we were looking for
			 */
		    public Curseur getCurseurWithId(long id){
		        Cursor c = database.query(MySQLiteHelper.TABLE_CURSEUR, allColumnsCurseur, MySQLiteHelper.COLUMN_CURSEURID + " = \"" + id +"\"", null, null, null, null);
		        c.moveToFirst();
		        Curseur c1 = cursorToCurseur(c);
		        c.close();
		        return c1;
		    }

			/**
			 * knowing an id we test if this curseur exists
			 * @param id is the id of the curseur we ask
			 * @return boolean says if the curseur with this id exists or not
			 */
		    public Boolean existCurseurWithId(long id){
		        Cursor c = database.query(MySQLiteHelper.TABLE_CURSEUR, allColumnsCurseur, MySQLiteHelper.COLUMN_CURSEURID + " = \"" + id +"\"", null, null, null, null);
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
		     * deleting a Curseur
		     * @param c1 is the curseur we want to delete
		     */
			public void deleteCurseur(Curseur c1){
				long id = c1.getCurseur_id();
				long marqueurId = c1.getMarqueur_id();
				System.out.println("Curseur deleted with id: "+ id);
				database.delete(MySQLiteHelper.TABLE_CURSEUR, MySQLiteHelper.COLUMN_CURSEURID+" = "+ id, null);
				database.delete(MySQLiteHelper.TABLE_MARQUEUR, MySQLiteHelper.COLUMN_MARQUEURID+" = "+ marqueurId, null);
			}
			
			
			/**
			 * convert a cursor to a curseur
			 * @param cursor
			 * @return curseur 
			 */
			private Curseur cursorToCurseur(Cursor cursor) {
			    Curseur c1 = new Curseur();
			    c1.setCurseur_id(cursor.getLong(0));
			    c1.setCurseur_valeur(cursor.getInt(1));
			    c1.setCurseur_nom(cursor.getString(2));
			    c1.setMarqueur_id(cursor.getLong(3));
			    return c1;
			}
			
			public String[] getCurseursARemplir(long utilisateurId, long placesId){
				final String MY_QUERY = "select c.curseur_id, c.curseur_valeur, c.curseur_nom, c.marqueur_id  " +
						"from curseur as c inner join marqueur as m on c.marqueur_id = m.marqueur_id " +
						"where m.utilisateur_id =? " + 
						"and m.places_id =?";
				Cursor c = database.rawQuery(MY_QUERY, new String[]{String.valueOf(utilisateurId), String.valueOf(placesId)});
		    	try{
		    		c.moveToFirst();
		    	}catch(Exception e){
		    		
		    	}
		    	String[] nomCurseurs = {"Cozy", "Palpitant", "Formel", "Accueillant", "Sécurisant", "Inspirant", "Intime", "Animé", "Luxueux", "Chill", "Personnel", "Romantique", "Ennuyeux", "Chaleureux", "Business", "Reposant"};
		    	ArrayList<String> liste = new ArrayList<String>();
		    	for(String s : nomCurseurs){
		    		liste.add(s);
		    	}
		    	Boolean notLast = true;
		    	while (notLast){
		    		Curseur curseur = cursorToCurseur(c);
		    		String cursNom = curseur.getCurseur_nom();
		    		for (int i =liste.size() - 1; i>=0; i--){
		    			if (liste.get(i)==cursNom){
		    				liste.remove(i);
		    			}
		    		}
		    		notLast = c.moveToNext();
		    	}
		        String[] aRemplir = new String[liste.size()];
		    	aRemplir = liste.toArray(aRemplir);
		    	
		    	return aRemplir;
		    }
			
			public Curseur[] getMesCurseurs(long utilisateurId, long placesId){
				final String MY_QUERY = "select c.curseur_id, c.curseur_valeur, c.curseur_nom, c.marqueur_id  " +
						"from curseur as c inner join marqueur as m on c.marqueur_id = m.marqueur_id " +
						"where m.utilisateur_id =? " + 
						"and m.places_id =?";
				Cursor c = database.rawQuery(MY_QUERY, new String[]{String.valueOf(utilisateurId), String.valueOf(placesId)});
		    	try{
		    		c.moveToFirst();
		    	}catch(Exception e){
		    		
		    	}
		    	ArrayList<Curseur> liste = new ArrayList<Curseur>();
		    	Boolean notLast = true;
		    	while (notLast){
		    		Curseur curseur = cursorToCurseur(c);
		    		liste.add(curseur);
		    		notLast = c.moveToNext();
		    	}
		        Curseur[] mesCurseurs = new Curseur[liste.size()];
		    	mesCurseurs = liste.toArray(mesCurseurs);
		    	
		    	return mesCurseurs;
		    }
			
			public Curseur getCurseurWithNom(long utilisateurId, long placesId, String curseurNom){
				final String MY_QUERY = "select c.curseur_id, c.curseur_valeur, c.curseur_nom, c.marqueur_id " +
						"from curseur as c inner join marqueur as m on c.marqueur_id = m.marqueur_id" +
						"where m.utilisateur_id =?" + 
						"and m.places_id =?" +
						"and c.curseur_nom =?";
				Cursor c = database.rawQuery(MY_QUERY, new String[]{String.valueOf(utilisateurId), String.valueOf(placesId), curseurNom});
		    	c.moveToFirst();
		    	Curseur curseur = cursorToCurseur(c);
		    	return curseur;
		    }
			
			/**
			   * Méthode pour créer un mot
			   */
			  
			  public Mot createMot (String mot, long marqueurId){
					ContentValues values = new ContentValues(); 
					values.put(MySQLiteHelper.COLUMN_MOT, mot);
					values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueurId);
					long insertId = database.insert(MySQLiteHelper.TABLE_MOT, null, values);
					//TODO check the utily of autoincrement
					Cursor cursor = 
							database.query(
									MySQLiteHelper.TABLE_MOT,
									allColumnsMots,
									MySQLiteHelper.COLUMN_MOTID+" = "+insertId,
									null, null, null, null);
					cursor.moveToFirst();
					Mot newMot = cursorToMot(cursor);//method at the end of the class
					cursor.close();
					return newMot;
				}
			  
			  /**
				 * overload of previous method
				 * creating a new Mot in the database
				 * @return mot is the created mot
				 */
				public Mot createMot (long id, String mot, long marqueurId){
			        Boolean exist = existMotWithId(id);
			        
			        if(exist){
			        	Mot existMot = getMotWithId(id);
			        	Mot updatedMot = updateMot(existMot, mot);
			            return updatedMot;
			        }
			        else {
			            ContentValues values = new ContentValues();
			            values.put(MySQLiteHelper.COLUMN_MOTID, id);
			            values.put(MySQLiteHelper.COLUMN_MOT, mot);
			            values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueurId);

			            long insertId = database.insert(MySQLiteHelper.TABLE_MOT, null,values);
			            Cursor cursor = database.query(MySQLiteHelper.TABLE_MOT,
			                    allColumnsMots, MySQLiteHelper.COLUMN_MOTID + " = " + insertId, null,null, null, null);
			            cursor.moveToFirst();
			            Mot m2 = cursorToMot(cursor);
			            cursor.close();
			            return m2;
			        }
			    }

				/**
				 * update a Mot
				 * @return mot updated
				 */
				public Mot updateMot(Mot mot, String colMot){
					ContentValues values = new ContentValues();
					values.put(MySQLiteHelper.COLUMN_MOT, colMot);

					database.update(MySQLiteHelper.TABLE_MOT, values, MySQLiteHelper.COLUMN_MOTID + " = " +mot.getMot_id(), null);
					return getMotWithId(mot.getMot_id());
			 }


				/**
				 * knowing a Mot_id, we want to get the mot itself
				 * @param id is the id of the mot we are looking for
				 * @return c1 is the mot we were looking for
				 */
			    public Mot getMotWithId(Long id){
			        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMots, MySQLiteHelper.COLUMN_MOTID + " = \"" + id +"\"", null, null, null, null);
			        c.moveToFirst();
			        Mot m1 = cursorToMot(c);
			        c.close();
			        return m1;
			    }

				/**
				 * knowing an id we test if this mot exists
				 * @param id is the id of the mot we ask
				 * @return boolean says if the mot with this id exists or not
				 */
			    public Boolean existMotWithId(Long id){
			        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMots, MySQLiteHelper.COLUMN_MOTID + " = \"" + id +"\"", null, null, null, null);
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
			     * deleting a Mot
			     * @param m1 is the mot we want to delete
			     */
				public void deleteMot(Mot m1){
					long id = m1.getMot_id();
					long marqueurId = m1.getMarqueur_id();
					System.out.println("Mot deleted with id: "+ id);
					database.delete(MySQLiteHelper.TABLE_MOT, MySQLiteHelper.COLUMN_MOTID+" = "+ id, null);
					database.delete(MySQLiteHelper.TABLE_MARQUEUR, MySQLiteHelper.COLUMN_MARQUEURID+" = "+ marqueurId, null);
				}
				
				
				/**
				 * convert a cursor to a mot
				 * @param cursor
				 * @return mot 
				 */
				private Mot cursorToMot(Cursor cursor) {
				    Mot m1 = new Mot();
				    m1.setMot_id(cursor.getLong(0));
				    m1.setMot(cursor.getString(1));
				    m1.setMarqueur_id(cursor.getLong(2));
				    return m1;
				}
				

				/**
				 * knowing a marqueur_id, we want to get the mot itself
				 * @param id is the id of the marqueur linked to the mot we are looking for
				 * @return c1 is the mot we were looking for
				 */
			    public Mot getMotWithMarqueurId(long id){
			        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMots, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id +"\"", null, null, null, null);
			        c.moveToFirst();
			        Mot m1 = cursorToMot(c);
			        c.close();
			        return m1;
			    }
				
			    public Mot[] getMesMots(long utilisateurId, long placesId){
			    	final String MY_QUERY = "SELECT a.mot_id, a.mot, a.marqueur_id FROM "+ MySQLiteHelper.TABLE_MOT + " a INNER JOIN "+ MySQLiteHelper.TABLE_MARQUEUR + "  b ON a." + MySQLiteHelper.COLUMN_MARQUEURID + "=b." + MySQLiteHelper.COLUMN_MARQUEURID + " WHERE b." + MySQLiteHelper.COLUMN_PLACESID +"=? AND " + MySQLiteHelper.COLUMN_UTILISATEURID +"=?";
			    	Cursor c = database.rawQuery(MY_QUERY, new String[]{String.valueOf(placesId), String.valueOf(utilisateurId)});
			    	c.moveToFirst();
			    	ArrayList<Mot> mesMots = new ArrayList<Mot>();
			    	Boolean notLast = true;
			    	while (notLast){
			    		Mot mot = cursorToMot(c);
			    		mesMots.add(mot);
			    		notLast = c.moveToNext();
			    	}
			    	Mot[] motArr = new Mot[mesMots.size()];
			    	motArr = mesMots.toArray(motArr);
			    	
			    	return motArr;
			    }
				
				/**
				   * Méthode pour créer une image
				   */
				  
				  public Image createImage (String url, long marqueurId){
						ContentValues values = new ContentValues(); 
						values.put(MySQLiteHelper.COLUMN_IMAGEURL, url);
						values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueurId);
						long insertId = database.insert(MySQLiteHelper.TABLE_IMAGE, null, values);
						//TODO check the utily of autoincrement
						Cursor cursor = 
								database.query(
										MySQLiteHelper.TABLE_IMAGE,
										allColumnsImage,
										MySQLiteHelper.COLUMN_IMAGEID+" = "+insertId,
										null, null, null, null);
						cursor.moveToFirst();
						Image newImage = cursorToImage(cursor);//method at the end of the class
						cursor.close();
						return newImage;
					}
				  
				  /**
					 * overload of previous method
					 * creating a new Image in the database
					 * @return image is the created image
					 */
					public Image createImage (long id, String url, long marqueurId){
				        Boolean exist = existImageWithId(id);
				        
				        if(exist){
				        	Image existImage = getImageWithId(id);
				        	Image updatedImage = updateImage(existImage, url);
				            return updatedImage;
				        }
				        else {
				            ContentValues values = new ContentValues();
				            values.put(MySQLiteHelper.COLUMN_IMAGEID, id);
				            values.put(MySQLiteHelper.COLUMN_IMAGEURL, url);
				            values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueurId);

				            long insertId = database.insert(MySQLiteHelper.TABLE_IMAGE, null,values);
				            Cursor cursor = database.query(MySQLiteHelper.TABLE_IMAGE,
				                    allColumnsImage, MySQLiteHelper.COLUMN_IMAGEID + " = " + insertId, null,null, null, null);
				            cursor.moveToFirst();
				            Image i2 = cursorToImage(cursor);
				            cursor.close();
				            return i2;
				        }
				    }

					/**
					 * update a Image
					 * @return image updated
					 */
					public Image updateImage(Image image, String url){
						ContentValues values = new ContentValues();
						values.put(MySQLiteHelper.COLUMN_IMAGEURL, url);

						database.update(MySQLiteHelper.TABLE_IMAGE, values, MySQLiteHelper.COLUMN_IMAGEID + " = " +image.getImage_id(), null);
						return getImageWithId(image.getImage_id());
					}

					/**
					 * knowing a Mot_id, we want to get the image itself
					 * @param id is the id of the image we are looking for
					 * @return c1 is the image we were looking for
					 */
				    public Image getImageWithId(Long id){
				        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_IMAGEID + " = \"" + id +"\"", null, null, null, null);
				        c.moveToFirst();
				        Image i1 = cursorToImage(c);
				        c.close();
				        return i1;
				    }

					/**
					 * knowing an id we test if this image exists
					 * @param id is the id of the image we ask
					 * @return boolean says if the image with this id exists or not
					 */
				    public Boolean existImageWithId(Long id){
				        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_IMAGEID + " = \"" + id +"\"", null, null, null, null);
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
				     * deleting an Image
				     * @param i1 is the image we want to delete
				     */
					public void deleteImage(Image i1){
						long id = i1.getImage_id();
						long marqueurId = i1.getMarqueur_id();
						System.out.println("Image deleted with id: "+ id);
						database.delete(MySQLiteHelper.TABLE_IMAGE, MySQLiteHelper.COLUMN_IMAGEID+" = "+ id, null);
						database.delete(MySQLiteHelper.TABLE_MARQUEUR, MySQLiteHelper.COLUMN_MARQUEURID+" = "+ marqueurId, null);
					}
					
					
					/**
					 * convert a cursor to an image
					 * @param cursor
					 * @return image
					 */
					private Image cursorToImage(Cursor cursor) {
					    Image i1 = new Image();
					    i1.setImage_id(cursor.getLong(0));
					    i1.setImage_url(cursor.getString(1));
					    i1.setMarqueur_id(cursor.getLong(2));
					    return i1;
					}
					
					/**
					   * Méthode pour créer une adresse
					   */
					  
					  public Adresse createAdresse (String nom){
							ContentValues values = new ContentValues(); 
							values.put(MySQLiteHelper.COLUMN_ADRESSENOM, nom);
							long insertId = database.insert(MySQLiteHelper.TABLE_ADRESSE, null, values);
							//TODO check the utily of autoincrement
							Cursor cursor = 
									database.query(
											MySQLiteHelper.TABLE_ADRESSE,
											allColumnsAdresse,
											MySQLiteHelper.COLUMN_ADRESSEID+" = "+insertId,
											null, null, null, null);
							cursor.moveToFirst();
							Adresse newAdresse = cursorToAdresse(cursor);//method at the end of the class
							cursor.close();
							return newAdresse;
						}
					  
					  /**
						 * overload of previous method
						 * creating a new Adresse in the database
						 * @return adresse is the created adresse
						 */
						public Adresse createAdresse (long id, String nom){
					        Boolean exist = existImageWithId(id);
					        
					        if(exist){
					        	Adresse existAdresse = getAdresseWithId(id);
					        	Adresse updatedAdresse = updateAdresse(existAdresse, nom);
					            return updatedAdresse;
					        }
					        else {
					        	ContentValues values = new ContentValues(); 
								values.put(MySQLiteHelper.COLUMN_ADRESSENOM, nom);
								long insertId = database.insert(MySQLiteHelper.TABLE_ADRESSE, null, values);
								//TODO check the utily of autoincrement
								Cursor cursor = 
										database.query(
												MySQLiteHelper.TABLE_ADRESSE,
												allColumnsAdresse,
												MySQLiteHelper.COLUMN_ADRESSEID+" = "+insertId,
												null, null, null, null);
								cursor.moveToFirst();
								Adresse a2 = cursorToAdresse(cursor);//method at the end of the class
								cursor.close();
								return a2;
								}
					    }

						/**
						 * update a Adresse
						 * @return adresse updated
						 */
						public Adresse updateAdresse(Adresse adresse, String nom){
							ContentValues values = new ContentValues();
							values.put(MySQLiteHelper.COLUMN_ADRESSENOM, nom);

							database.update(MySQLiteHelper.TABLE_ADRESSE, values, MySQLiteHelper.COLUMN_ADRESSEID + " = " +adresse.getAdresse_id(), null);
							return getAdresseWithId(adresse.getAdresse_id());
						}

						/**
						 * knowing an Adresse_id, we want to get the adresse itself
						 * @param id is the id of the adresse we are looking for
						 * @return a1 is the adresse we were looking for
						 */
					    public Adresse getAdresseWithId(Long id){
					        Cursor c = database.query(MySQLiteHelper.TABLE_ADRESSE, allColumnsAdresse, MySQLiteHelper.COLUMN_ADRESSEID + " = \"" + id +"\"", null, null, null, null);
					        c.moveToFirst();
					        Adresse a1 = cursorToAdresse(c);
					        c.close();
					        return a1;
					    }

						/**
						 * knowing an id we test if this adresse exists
						 * @param id is the id of the adresse we ask
						 * @return boolean says if the adresse with this id exists or not
						 */
					    public Boolean existAdresseWithId(Long id){
					        Cursor c = database.query(MySQLiteHelper.TABLE_ADRESSE, allColumnsAdresse, MySQLiteHelper.COLUMN_ADRESSEID + " = \"" + id +"\"", null, null, null, null);
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
					     * deleting an Adresse
					     * @param a1 is the adresse we want to delete
					     */
						public void deleteAdresse(Adresse a1){
							long id = a1.getAdresse_id();
							System.out.println("Adresse deleted with id: "+ id);
							database.delete(MySQLiteHelper.TABLE_ADRESSE, MySQLiteHelper.COLUMN_ADRESSEID+" = "+ id, null);
						}
						
						
						/**
						 * convert a cursor to an adresse
						 * @param cursor
						 * @return adresse
						 */
						private Adresse cursorToAdresse(Cursor cursor) {
						    Adresse a1 = new Adresse();
						    a1.setAdresse_id(cursor.getLong(0));
						    a1.setAdresse_nom(cursor.getString(1));
						    return a1;
						}
						
					/**
					   * Méthode pour créer une place
					   */
					  
					  public Places createPlace (String nom, double latitude, double longitude, long adresse_id){
							ContentValues values = new ContentValues(); 
							values.put(MySQLiteHelper.COLUMN_PLACESNOM, nom);
							values.put(MySQLiteHelper.COLUMN_PLACESLONGITUDE, longitude);
							values.put(MySQLiteHelper.COLUMN_PLACESLATITUDE, latitude);
							values.put(MySQLiteHelper.COLUMN_ADRESSEID, adresse_id);
							long insertId = database.insert(MySQLiteHelper.TABLE_PLACES, null, values);
							//TODO check the utily of autoincrement
							Cursor cursor = 
									database.query(
											MySQLiteHelper.TABLE_PLACES,
											allColumnsPlaces,
											MySQLiteHelper.COLUMN_PLACESID+" = "+insertId,
											null, null, null, null);
							cursor.moveToFirst();
							Places newPlace = cursorToPlace(cursor);//method at the end of the class
							cursor.close();
							return newPlace;
						}
					  
					  /**
						 * overload of previous method
						 * creating a new Image in the database
						 * @return image is the created image
						 */
						public Places createPlace (long id, String nom, double latitude, double longitude, long adresse_id){
					        Boolean exist = existPlaceWithLatLng(latitude, longitude);
					        
					        if(exist){
					        	Places existPlace = getPlaceWithId(id);
					        	Places updatedPlace = updatePlace(existPlace, nom, latitude, longitude, adresse_id);
					            return updatedPlace;
					        }
					        else {
					        	ContentValues values = new ContentValues();
					        	values.put(MySQLiteHelper.COLUMN_PLACESID, id);
					        	values.put(MySQLiteHelper.COLUMN_PLACESNOM, nom);
								values.put(MySQLiteHelper.COLUMN_PLACESLONGITUDE, longitude);
								values.put(MySQLiteHelper.COLUMN_PLACESLATITUDE, latitude);
								values.put(MySQLiteHelper.COLUMN_ADRESSEID, adresse_id);
								long insertId = database.insert(MySQLiteHelper.TABLE_PLACES, null, values);
								//TODO check the utily of autoincrement
								Cursor cursor = 
										database.query(
												MySQLiteHelper.TABLE_PLACES,
												allColumnsPlaces,
												MySQLiteHelper.COLUMN_PLACESID+" = "+insertId,
												null, null, null, null);
								cursor.moveToFirst();
								Places p2 = cursorToPlace(cursor);//method at the end of the class
								cursor.close();
								return p2;
					        }
					    }

						/**
						 * update a Image
						 * @return image updated
						 */
						public Places updatePlace(Places place, String nom, double latitude, double longitude, long adresse_id){
							ContentValues values = new ContentValues();
				        	values.put(MySQLiteHelper.COLUMN_PLACESNOM, nom);
							values.put(MySQLiteHelper.COLUMN_PLACESLONGITUDE, longitude);
							values.put(MySQLiteHelper.COLUMN_PLACESLATITUDE, latitude);
							values.put(MySQLiteHelper.COLUMN_ADRESSEID, adresse_id);

							database.update(MySQLiteHelper.TABLE_PLACES, values, MySQLiteHelper.COLUMN_PLACESID + " = " +place.getPlaces_id(), null);
							return getPlaceWithId(place.getPlaces_id());
						}

						/**
						 * knowing a Mot_id, we want to get the image itself
						 * @param id is the id of the image we are looking for
						 * @return c1 is the image we were looking for
						 */
					    public Places getPlaceWithId(long id){
					        Cursor c = database.query(MySQLiteHelper.TABLE_PLACES, allColumnsPlaces, MySQLiteHelper.COLUMN_PLACESID + " = \"" + id +"\"", null, null, null, null);
					        c.moveToFirst();
					        Places p1 = cursorToPlace(c);
					        c.close();
					        return p1;
					    }
					    
					    /**
						 * knowing a Mot_id, we want to get the image itself
						 * @param id is the id of the image we are looking for
						 * @return c1 is the image we were looking for
						 */
					    
					    public Places getPlaceWithLatLng(double lat, double lng){
					        Cursor c = database.query(MySQLiteHelper.TABLE_PLACES, allColumnsPlaces, MySQLiteHelper.COLUMN_PLACESLATITUDE + " = \"" + lat +"\"" + " AND " + MySQLiteHelper.COLUMN_PLACESLONGITUDE + " = \"" + lng +"\"", null, null, null, null);
					        c.moveToFirst();
					        Places p1 = cursorToPlace(c);
					        c.close();
					        return p1;
					    }

						/**
						 * knowing an id we test if this image exists
						 * @param id is the id of the image we ask
						 * @return boolean says if the image with this id exists or not
						 */
					    public Boolean existPlaceWithLatLng(double lat, double lng){
					        Cursor c = database.query(MySQLiteHelper.TABLE_PLACES, allColumnsPlaces, MySQLiteHelper.COLUMN_PLACESLATITUDE + " = \"" + lat +"\"" + " AND " + MySQLiteHelper.COLUMN_PLACESLONGITUDE + " = \"" + lng +"\"", null, null, null, null);
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
					     * deleting an Image
					     * @param i1 is the image we want to delete
					     */
						public void deletePlace(Places p1){
							long id = p1.getPlaces_id();
							System.out.println("Image deleted with id: "+ id);
							database.delete(MySQLiteHelper.TABLE_IMAGE, MySQLiteHelper.COLUMN_IMAGEID+" = "+ id, null);
						}
						
						
						/**
						 * convert a cursor to an image
						 * @param cursor
						 * @return image
						 */
						private Places cursorToPlace(Cursor cursor) {
						    Places p1 = new Places();
						    p1.setPlaces_id(cursor.getLong(0));
						    p1.setPlaces_nom(cursor.getString(1));
						    p1.setPlaces_latitude(cursor.getDouble(2));
						    p1.setPlaces_longitude(cursor.getDouble(3));
						    p1.setAdresse_id(cursor.getLong(4));
						    return p1;
						}
					/**
					 * create a Marqueur
					 * 
					 */
					public Marqueur createMarqueur(long placesId, long utilisateurId){
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dateMnt = new Date(System.currentTimeMillis());
						ContentValues values = new ContentValues();
					    values.put(MySQLiteHelper.COLUMN_MARQUEURDATECREATION, dateFormat.format(dateMnt));
					    values.put(MySQLiteHelper.COLUMN_MARQUEURDATEDERNIEREEDITION, dateFormat.format(dateMnt));
					    values.put(MySQLiteHelper.COLUMN_PLACESID, placesId);
					    values.put(MySQLiteHelper.COLUMN_UTILISATEURID, utilisateurId);
					    long insertId = database.insert(MySQLiteHelper.TABLE_MARQUEUR, null,
					        values);
					    Cursor cursor = database.query(MySQLiteHelper.TABLE_MARQUEUR,
					        allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID+ " = " + insertId, null,
					        null, null, null);
					    cursor.moveToFirst();
					    Marqueur newMarqueur = cursorToMarqueur(cursor);
					    cursor.close();
					    return newMarqueur;

					}
					
					public Marqueur createMarqueur(long marqueurId, long placesId, long utilisateurId){
						Boolean exist = existMarqueurWithId(marqueurId);
				        
				        if(exist){
				        	Marqueur existMarqueur = getMarqueurWithId(marqueurId);
				        	Marqueur updatedMarqueur = updateMarqueur(existMarqueur);
				            return updatedMarqueur;
				        }
				        else {
				        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date dateMnt = new Date(System.currentTimeMillis());
							ContentValues values = new ContentValues();
						    values.put(MySQLiteHelper.COLUMN_MARQUEURDATECREATION, dateFormat.format(dateMnt));
						    values.put(MySQLiteHelper.COLUMN_MARQUEURDATEDERNIEREEDITION, dateFormat.format(dateMnt));
						    values.put(MySQLiteHelper.COLUMN_PLACESID, placesId);
						    values.put(MySQLiteHelper.COLUMN_UTILISATEURID, utilisateurId);
						    long insertId = database.insert(MySQLiteHelper.TABLE_MARQUEUR, null,
						        values);
						    Cursor cursor = database.query(MySQLiteHelper.TABLE_MARQUEUR,
						        allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID+ " = " + insertId, null,
						        null, null, null);
						    cursor.moveToFirst();
						    Marqueur newMarqueur = cursorToMarqueur(cursor);
						    cursor.close();
						    return newMarqueur;
				        }

					}
					
					/**
					 * update a Marqueur
					 * @return marqueur updated
					 */
					public Marqueur updateMarqueur(Marqueur marqueur){
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dateMnt = new Date(System.currentTimeMillis());
						ContentValues values = new ContentValues();
						values.put(MySQLiteHelper.COLUMN_MARQUEURDATEDERNIEREEDITION, dateFormat.format(dateMnt));

						database.update(MySQLiteHelper.TABLE_MARQUEUR, values, MySQLiteHelper.COLUMN_MARQUEURID + " = " +marqueur.getMarqueur_id(), null);
						return getMarqueurWithId(marqueur.getMarqueur_id());
					}

					/**
					 * knowing a Marqueur_id, we want to get the marqueur itself
					 * @param id is the id of the marqueur we are looking for
					 * @return m1 is the marqueur we were looking for
					 */
				    public Marqueur getMarqueurWithId(Long id){
				        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id +"\"", null, null, null, null);
				        c.moveToFirst();
				        Marqueur m1 = cursorToMarqueur(c);
				        c.close();
				        return m1;
				    }
				    
				    public Marqueur getMonMarqueur(long utilisateur_id, long places_id, int position){
				        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur,
				        		MySQLiteHelper.COLUMN_UTILISATEURID + " = \"" + utilisateur_id +"\"" + 
				        		" AND " +
				        		MySQLiteHelper.COLUMN_PLACESID + " = \"" + places_id + "\"" , null, null, null, null);
				        c.moveToFirst();
				        for (int i=0; i<position; i++){
				        	c.moveToNext();
				        }
				        Marqueur m1 = cursorToMarqueur(c);
				        c.close();
				        return m1;
				    }

					/**
					 * knowing an id we test if this marqueur exists
					 * @param id is the id of the marqueur we ask
					 * @return boolean says if the marqueur with this id exists or not
					 */
				    public Boolean existMarqueurWithId(Long id){
				        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id +"\"", null, null, null, null);
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
				     * deleting a Marqueur
				     */
					public void deleteMarqueur(Marqueur m1){
						long id = m1.getMarqueur_id();
						System.out.println("Marqueur deleted with id: "+ id);
						database.delete(MySQLiteHelper.TABLE_MARQUEUR, MySQLiteHelper.COLUMN_MARQUEURID+" = "+ id, null);
					}


					private Marqueur cursorToMarqueur(Cursor cursor) {
					    Marqueur marqueur = new Marqueur();

					    marqueur.setMarqueur_id(cursor.getLong(0));

					    Date dateCreation = new Date(cursor.getLong(1)*1000);
					    marqueur.setMarqueur_date_creation(dateCreation);

					    Date dateEdition = new Date(cursor.getLong(2)*1000);
					    marqueur.setMarqueur_date_derniere_edition(dateEdition);

					    marqueur.setPlaces_id(cursor.getString(3));
					    
					    marqueur.setUtilisateur_id(cursor.getLong(4));
					    return marqueur;
					  }

}

