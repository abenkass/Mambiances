package com.pappl.mambiances.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Floremassoullie1
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

	
	/*
	 * Declaration of tables
	 */
	public static final String TABLE_MARQUEUR = "Marqueur";
	public static final String TABLE_CURSEUR = "Curseur";
	public static final String TABLE_MOT = "Mot";
	public static final String TABLE_IMAGE = "Image";
	public static final String TABLE_PLACES = "Places";
	public static final String TABLE_ADRESSE = "Adresse";
	public static final String TABLE_AMBIANCEMOT = "AmbianceMot";
	public static final String TABLE_AMBIANCECURSEUR = "AmbianceCurseur";
	public static final String TABLE_UTILISATEUR = "Utilisateur";
	public static final String TABLE_PARAMETRES= "Parametres";
	public static final String TABLE_FAVORIS = "Favoris";
	public static final String TABLE_AMIS = "Amis";



	//creation of table getters
	/**
	 * get the name of Table Marqueur
	 * @return String which is the name of table marqueur
	 */
	public static String getTableMarqueur() {
		return TABLE_MARQUEUR;
	}
	
	/**
	 * get the name of Table Curseur
	 * @return String which is the name of table curseur
	 */
	public static String getTableCurseur() {
		return TABLE_CURSEUR;
	}
	
	/**
	 * get the name of Table Mot
	 * @return String which is the name of table mot
	 */
	public static String getTableMot() {
		return TABLE_MOT;
	}
	
	/**
	 * get the name of Table Image
	 * @return String which is the name of table image
	 */
	public static String getTableImage() {
		return TABLE_IMAGE;
	}
	
	/**
	 * get the name of Table Places
	 * @return String which is the name of table places
	 */
	public static String getTablePlaces() {
		return TABLE_PLACES;
	}
	
	/**
	 * get the name of Table Adresse
	 * @return String which is the name of table adresse
	 */
	public static String getTableAdresse() {
		return TABLE_ADRESSE;
	}
	
	/**
	 * get the name of Table AmbianceMot
	 * @return String which is the name of table ambiancemot
	 */
	public static String getTableAmbianceMot() {
		return TABLE_AMBIANCEMOT;
	}
	
	/**
	 * get the name of Table AmbianceCurseur
	 * @return String which is the name of table ambiancecurseur
	 */
	public static String getTableAmbianceCurseur() {
		return TABLE_AMBIANCECURSEUR;
	}
	
	/**
	 * get the name of Table Utilisateur
	 * @return String which is the name of table utilisateur
	 */
	public static String getTableUtilisateur() {
		return TABLE_UTILISATEUR;
	}
	
	/**
	 * get the name of Table Parametres
	 * @return String which is the name of table parametres
	 */
	public static String getTableParametres() {
		return TABLE_PARAMETRES;
	}
	
	/**
	 * get the name of Table Favoris
	 * @return String which is the name of table favoris
	 */
	public static String getTableFavoris() {
		return TABLE_FAVORIS;
	}
	
	/**
	 * get the name of Table Amis
	 * @return String which is the name of table amis
	 */
	public static String getTableAmis() {
		return TABLE_AMIS;
	}
	
	
	
	/**
	 * names of the columns of the whole database
	 */

	public static final String COLUMN_MARQUEURID = "marqueur_id";
	public static final String COLUMN_MARQUEURDATECREATION = "marqueur_date_creation";
	public static final String COLUMN_MARQUEURDATEDERNIEREEDITION = "marqueur_date_derniere_edition";

	public static final String COLUMN_CURSEURID = "curseur_id";
	public static final String COLUMN_CURSEURVALEUR = "curseur_valeur";
	public static final String COLUMN_CURSEURNOM = "curseur_nom";
	
	public static final String COLUMN_MOTID = "mot_id";
	public static final String COLUMN_MOT = "mot";

	public static final String COLUMN_IMAGEID = "image_id";
	public static final String COLUMN_IMAGEURL = "image_url";

	public static final String COLUMN_PLACESID = "places_id";
	public static final String COLUMN_PLACESNOM = "places_nom";
	public static final String COLUMN_PLACESLATITUDE = "places_latitude";
	public static final String COLUMN_PLACESLONGITUDE = "places_longitude";

	public static final String COLUMN_ADRESSEID = "adresse_id";
	public static final String COLUMN_ADRESSERUE = "adresse_rue";
	public static final String COLUMN_ADRESSEVILLE = "adresse_ville";
	public static final String COLUMN_ADRESSENUMERORUE = "adresse_numero_rue";
	public static final String COLUMN_ADRESSECODEPOSTAL = "adresse_code_postal";
	public static final String COLUMN_ADRESSENOM = "adresse_nom";

	public static final String COLUMN_AMBIANCEMOTID = "amabiancemot_id";
	public static final String COLUMN_AMBIANCEMOTMOT= "ambiancemot_mot";
	
	public static final String COLUMN_AMBIANCECURSEURID = "amabiancecurseur_id";
	public static final String COLUMN_AMBIANCECURSEURNOM= "ambiancecurseur_nom";
	public static final String COLUMN_AMBIANCECURSEURVALEUR= "ambiancecurseur_valeur";

	public static final String COLUMN_PARAMETRESID = "parametres_id";
	
	public static final String COLUMN_FAVORISDATE = "favoris_date";

	public static final String COLUMN_AMISDATE = "amis_date";
	public static final String COLUMN_AMIS = "amis";
	public static final String COLUMN_UTILISATEURUTILISATEURID = "utilisateur_utilisateur_id";

	public static final String COLUMN_UTILISATEURID = "utilisateur_id";
	public static final String COLUMN_UTILISATEURLOGIN = "utilisateur_login";
	public static final String COLUMN_UTILISATEURMDP= "utilisateur_mdp";

	/**
	 * name of the local database
	 * upgrading the version force the database to be deleted and recreated
	 */
	public static final String DATABASE_NAME = "local_db";
	public static final int DATABASE_VERSION = 1;

	/**
	 * query to create table MARQUEUR
	 */
	private static final String 
				DATABASE_CREATEMARQUEUR = 		
					"create table "
					+ TABLE_MARQUEUR + " (" 
					+ COLUMN_MARQUEURID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_MARQUEURDATECREATION + " DATETIME, "
					+ COLUMN_MARQUEURDATEDERNIEREEDITION + " DATETIME, "
					+ COLUMN_PLACESID + " INTEGER, "
					+ COLUMN_UTILISATEURID + " INTEGER, "
					+ " FOREIGN KEY( "+ COLUMN_PLACESID +" ) REFERENCES "+TABLE_PLACES+" ( "+COLUMN_PLACESID+" ),"
					+ " FOREIGN KEY( "+ COLUMN_UTILISATEURID +" ) REFERENCES "+TABLE_UTILISATEUR+" ( "+COLUMN_UTILISATEURID+" )"
					+"); "
	;
	
	/**
	 * query to create table CURSEUR
	 */
	private static final String 
				DATABASE_CREATECURSEUR = 		
					"create table "
					+ TABLE_CURSEUR + " (" 
					+ COLUMN_CURSEURID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_CURSEURVALEUR + " DOUBLE, " 
					+ COLUMN_CURSEURNOM + " text not null, "
					+ COLUMN_MARQUEURID + " INTEGER, "
					+ "FOREIGN KEY( "+ COLUMN_MARQUEURID +" ) REFERENCES "+ TABLE_MARQUEUR+" ( "+COLUMN_MARQUEURID+" )"
					+"); "
	;
	
	/**
	 * query to create table MOT
	 */
	private static final String 
				DATABASE_CREATEMOT = 		
					"create table "
					+ TABLE_MOT + " (" 
					+ COLUMN_MOTID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_MOT + " text not null, "
					+ COLUMN_MARQUEURID + " INTEGER, "
					+ "FOREIGN KEY( "+ COLUMN_MARQUEURID +" ) REFERENCES "+ TABLE_MARQUEUR+" ( "+COLUMN_MARQUEURID+" )"
					+"); "
	;
	/**
	 * query to create table IMAGE
	 */
	private static final String 
				DATABASE_CREATEIMAGE = 		
					"create table "
					+ TABLE_IMAGE + " (" 
					+ COLUMN_IMAGEID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_IMAGEURL + " text not null, "
					+ COLUMN_MARQUEURID + " INTEGER, "
					+ "FOREIGN KEY( "+ COLUMN_MARQUEURID +" ) REFERENCES "+ TABLE_MARQUEUR+" ( "+COLUMN_MARQUEURID+" )"
					+"); "
	;
	
	/**
	 * query to create table PLACES
	 */
	private static final String 
				DATABASE_CREATEPLACES = 		
					"create table "
					+ TABLE_PLACES + " (" 
					+ COLUMN_PLACESID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_PLACESNOM + " text not null, " 
					+ COLUMN_PLACESLATITUDE + " text not null, " 
					+ COLUMN_PLACESLONGITUDE + " text not null, "
					+ COLUMN_ADRESSEID + " INTEGER, "
					+ " FOREIGN KEY( "+ COLUMN_ADRESSEID +" ) REFERENCES "+TABLE_ADRESSE+" ( "+COLUMN_ADRESSEID+" )"
					+"); "
	;
	
	/**
	 * query to create table ADRESSE
	 */
	private static final String 
				DATABASE_CREATEADRESSE = 		
					"create table "
					+ TABLE_ADRESSE + " (" 
					+ COLUMN_ADRESSEID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					/*+ COLUMN_ADRESSERUE + " text not null, " 
					+ COLUMN_ADRESSENUMERORUE + " text not null, " 
					+ COLUMN_ADRESSEVILLE + " text not null, " 
					+ COLUMN_ADRESSECODEPOSTAL + " INTEGER " */
					+ COLUMN_ADRESSENOM 
					+"); "
	;
	
	/**
	 * query to create table AMBIANCEMOT
	 */
	private static final String 
				DATABASE_CREATEMOTAMB = 		
					"create table "
					+ TABLE_AMBIANCEMOT + " (" 
					+ COLUMN_AMBIANCEMOTID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_AMBIANCEMOTMOT + " text not null, " 
					+ COLUMN_PLACESID + " INTEGER, "
					+ " FOREIGN KEY( "+ COLUMN_PLACESID +" ) REFERENCES "+TABLE_PLACES+" ( "+COLUMN_PLACESID+" ) "
					+"); "
	;
	
	/**
	 * query to create table AMBIANCECURSEUR
	 */
	private static final String 
				DATABASE_CREATECURSAMB = 		
					"create table "
					+ TABLE_AMBIANCECURSEUR + " (" 
					+ COLUMN_AMBIANCECURSEURID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_AMBIANCECURSEURNOM + " text not null, " 
					+ COLUMN_AMBIANCECURSEURVALEUR + " DOUBLE, "
					+ COLUMN_PLACESID + " INTEGER, "
					+ " FOREIGN KEY( "+ COLUMN_PLACESID +" ) REFERENCES "+TABLE_PLACES+" ( "+COLUMN_PLACESID+" ) "
					+"); "
	;
	
	/**
	 * query to create table UTILISATEUR
	 */
	private static final String 
				DATABASE_CREATEUTILISATEUR = 		
					"create table "
					+ TABLE_UTILISATEUR + " (" 
					+ COLUMN_UTILISATEURID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_UTILISATEURLOGIN + " text not null, " 
					+ COLUMN_UTILISATEURMDP + " text not null "
					+"); "
	;
	
	/**
	 * query to create table PARAMETRES
	 */
	private static final String 
				DATABASE_CREATEPARAMETRES = 		
					"create table "
					+ TABLE_PARAMETRES + " (" 
					+ COLUMN_PARAMETRESID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_UTILISATEURID + " INTEGER, "
					+ " FOREIGN KEY( "+ COLUMN_UTILISATEURID +" ) REFERENCES "+TABLE_UTILISATEUR+" ( "+COLUMN_UTILISATEURID+" ) "
					+"); "
	;
	
	/**
	 * query to create table FAVORIS
	 */
	private static final String 
				DATABASE_CREATEFAVORIS = 		
					"create table "
					+ TABLE_FAVORIS + " (" 
					+ COLUMN_UTILISATEURID + " INTEGER, "
					+ COLUMN_PLACESID + " INTEGER, "
					+ COLUMN_FAVORISDATE + " DATETIME, "
					+" PRIMARY KEY ( "+COLUMN_UTILISATEURID+", "+COLUMN_PLACESID+" ), "
					+ " FOREIGN KEY( "+ COLUMN_UTILISATEURID +" ) REFERENCES "+TABLE_UTILISATEUR+" ( "+COLUMN_UTILISATEURID+" ) , "
					+ " FOREIGN KEY( "+ COLUMN_PLACESID +" ) REFERENCES "+TABLE_PLACES+" ( "+COLUMN_PLACESID+" ) "
					+"); "
	;
	
	/**
	 * query to create table AMIS
	 */
	private static final String 
				DATABASE_CREATEAMIS = 		
					"create table "
					+ TABLE_AMIS + " ("
					+ COLUMN_UTILISATEURID + " INTEGER, "
					+ COLUMN_UTILISATEURUTILISATEURID + " INTEGER, "
					+ COLUMN_AMIS + " BOOLEAN, "
					+ COLUMN_AMISDATE + " DATETIME, "
					+" PRIMARY KEY ( "+COLUMN_UTILISATEURID+", "+COLUMN_UTILISATEURUTILISATEURID+" ) , "
					+ " FOREIGN KEY( "+ COLUMN_UTILISATEURUTILISATEURID +" ) REFERENCES "+TABLE_UTILISATEUR+" ( "+COLUMN_UTILISATEURID+" ) , "					
					+ " FOREIGN KEY( "+ COLUMN_UTILISATEURID +" ) REFERENCES "+TABLE_UTILISATEUR+" ( "+COLUMN_UTILISATEURID+" ) "					
					+"); "
	;

	/**
	 * constructor
	 * @param context of our activity
	 */
	public MySQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}



	@Override
	public void onCreate(SQLiteDatabase database) {
		/**
		 * we call each methods to create every table
		 */
		database.execSQL(getDatabaseCreate1());	
		database.execSQL(getDatabaseCreate2());
		database.execSQL(getDatabaseCreate3());
		database.execSQL(getDatabaseCreate4());
		database.execSQL(getDatabaseCreate5());	
		database.execSQL(getDatabaseCreate6());
		database.execSQL(getDatabaseCreate7());
		database.execSQL(getDatabaseCreate8());
		database.execSQL(getDatabaseCreate9());
		database.execSQL(getDatabaseCreate10());
		database.execSQL(getDatabaseCreate11());
		database.execSQL(getDatabaseCreate12());


	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(), 
				"Upgrading database from version"+oldVersion+" to "+newVersion+", which will destroy all your data");
				db.execSQL(
						"DROP TABLE IF EXISTS" 
								+"; "
						);
				onCreate(db);		
	}


	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate1() {
		return DATABASE_CREATEUTILISATEUR;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate2() {
		return DATABASE_CREATEADRESSE;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate3() {
		return DATABASE_CREATEPLACES;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate4() {
		return DATABASE_CREATEMARQUEUR;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate5() {
		return DATABASE_CREATEMOT;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate6() {
		return DATABASE_CREATEIMAGE;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate7() {
		return DATABASE_CREATECURSEUR;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate8() {
		return DATABASE_CREATEMOTAMB;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate9() {
		return DATABASE_CREATECURSAMB;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate10() {
		return DATABASE_CREATEAMIS;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate11() {
		return DATABASE_CREATEFAVORIS;
	}
	
	/**
	 * getter for createquery of related number
	 * @return the query as String
	 */
	public static String getDatabaseCreate12() {
		return DATABASE_CREATEPARAMETRES;
	}

}
