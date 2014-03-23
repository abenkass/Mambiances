package com.pappl.mambiances;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.sync.Sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Connexion extends Activity {
	 
	  EditText inputLogin;
	  EditText inputMotDePasse;
	  CheckBox checkBox;
	  Button btnSeConnecter;
	  Button btnLinkToRegister;  
	  public static LocalDataSource datasource;
	  
	  public static Context baseContext;
	  
	  public static final String PREFS_NAME = ".Preferences"; 
	  private static final String PREF_LOGIN = "login";
	  private static final String PREF_MOTDEPASSE = "mot de passe";
	  private static final String PREF_CHECKED = "checked";
      public static final String DB_PATH = "data/data/com.pappl.mambiances/databases/local_db";

	  //TODO Enlever quand tests finis
	  
	  private void doDBCheck()
	  {
	      try{
	              File file = new File(DB_PATH);
	              file.delete();
	      }catch(Exception ex)
	      {}
	  }
	  
	  private void exportDB(){
			File sd = Environment.getExternalStorageDirectory();
		      	File data = Environment.getDataDirectory();
		       FileChannel source=null;
		       FileChannel destination=null;
		       String currentDBPath = "/data/"+ "com.pappl.mambiances" +"/databases/"+"local_db";
		       String backupDBPath = "local_db";
		       File currentDB = new File(data, currentDBPath);
		       File backupDB = new File(sd, backupDBPath);
		       try {
		            source = new FileInputStream(currentDB).getChannel();
		            destination = new FileOutputStream(backupDB).getChannel();
		            destination.transferFrom(source, 0, source.size());
		            source.close();
		            destination.close();
		            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
		        } catch(IOException e) {
		        	e.printStackTrace();
		        }
		}
	  
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.connexion);

		  datasource = new LocalDataSource(this);
		  
		  baseContext = getApplicationContext();
		
		/********************************************************/
		/* Charger la liste des utilisateurs de la base externe */
		/********************************************************/
		
		new Sync.LoadAllUsers().execute();
		  
	    /********************************/
	    /* D�finit le nom de l'Activity */
	    /********************************/

	      setTitle("Connexion");

	    /**********************************************************/
	    /* Importation des caractéristiques des champs et boutons */
	    /**********************************************************/

	    inputLogin = (EditText) findViewById(R.id.login);
	    inputMotDePasse = (EditText) findViewById(R.id.loginPassword);
	    checkBox = (CheckBox)findViewById(R.id.cbSeSouvenir);
	    btnSeConnecter = (Button) findViewById(R.id.btnSeConnecter);
	    btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

	    /***********************************************************************/
	    /* Restauration des préférences sauvegardées si la checkbox est cochée */
	    /***********************************************************************/    

	    SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);  
	    String email = pref.getString(PREF_LOGIN, "");
	    String password = pref.getString(PREF_MOTDEPASSE, "");
	    String checked = pref.getString(PREF_CHECKED, "");

	    inputLogin.setText(email);
	    inputMotDePasse.setText(password);
	    checkBox.setChecked(Boolean.parseBoolean(checked));

	    /****************************/
	    /* Clic sur le bouton Login */
	    /****************************/

	    btnSeConnecter.setOnClickListener(new View.OnClickListener() 
	    {      
	      public void onClick(View view) 
	      {
	        /************************************************************/
	        /* Enregistrement des préférences si la checkbox est cochée */
	        /************************************************************/  

	        if(checkBox.isChecked())
	        {
	          getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
	            .edit()
	            .putString(PREF_LOGIN, inputLogin.getText().toString())
	            .putString(PREF_MOTDEPASSE, inputMotDePasse.getText().toString())
	            .putString(PREF_CHECKED,"TRUE")
	            .commit();
	        }

	        /***********************/
	        /* Sinon on les efface */ 
	        /***********************/  

	        else if(!checkBox.isChecked())
	        {
	          getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit().clear().commit();
	        }

	        /***************************************/
	        /* On récupère le contenu des EditText */
	        /***************************************/  

	        String login = inputLogin.getText().toString();
	        String mdp = inputMotDePasse.getText().toString();
	        
	        /******************************************************/
	        /* On stocke les infos la BDD sqlite si le login et le mdp sont corrects*/
	        /******************************************************/
	        datasource.open();
	        //TODO Enlever quand tests finis
	        exportDB();
	        if(datasource.existUtilisateurWithLogin(login)){
	        	  /***************************************/
		          /* Lancement de l'Activity "MapActivity" */
		          /***************************************/
		
		          Intent mapActivity = new Intent(getApplicationContext(), MapActivity.class);
		          mapActivity.putExtra("LOGIN", login);
		          startActivity(mapActivity);
		          
		          /***/

		          /****************************/
		          /* Ferme l'Activity "Connexion" */
		          /****************************/
		
		          finish();
		          
	        }else{
	        	Toast.makeText(getApplicationContext(), 
                        "Identifiants incorrects", Toast.LENGTH_LONG).show(); 
	        }
	    	
	        }
	    });

	    /*****************************/
	    /* Clic sur le lien Enregistrer */
	    /*****************************/
	    btnLinkToRegister.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View view) 
	      {
	        /**************************************/
	        /* Lancement de l'Activity "Enregistrer" */
	        /**************************************/
	        Intent i = new Intent(getApplicationContext(), Enregistrer.class);
	        startActivity(i);

	        /****************************/
	        /* Ferme l'Activity "Connexion" */
	        /****************************/
	        finish();
	      }
	    });
	    datasource.close();
	  }
	}
