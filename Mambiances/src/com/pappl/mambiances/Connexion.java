package com.pappl.mambiances;

import com.pappl.mambiances.db.LocalDataSource;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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


	  public static final String PREFS_NAME = ".Preferences"; 
	  private static final String PREF_LOGIN = "login";
	  private static final String PREF_MOTDEPASSE = "mot de passe";
	  private static final String PREF_CHECKED = "checked";

	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.connexion);

		  final LocalDataSource  db = new LocalDataSource(this);

		    
	    /********************************/
	    /* Définit le nom de l'Activity */
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
	       
	        if(db.correctUtilisateur(login, mdp)){};
	    	/***/
	        	
	        	
	          /***************************************/
	          /* Lancement de l'Activity "DashBoard" */
	          /***************************************/
	
	          Intent dashboard = new Intent(getApplicationContext(), Dashboard.class);
	          dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	          startActivity(dashboard);
	
	          /****************************/
	          /* Ferme l'Activity "Connexion" */
	          /****************************/
	
	          finish();
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
	  }
	}
