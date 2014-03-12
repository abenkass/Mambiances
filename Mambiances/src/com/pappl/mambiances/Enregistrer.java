package com.pappl.mambiances;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.pappl.mambiances.db.LocalDataSource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Enregistrer extends Activity {
	  
	  Button btnValider;
	  Button btnLinkToLogin;
	  EditText inputLogin;
	  EditText inputMotDePasse;
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.enregistrer);   
  	  	
	    final LocalDataSource  db = new LocalDataSource(this);

	    
	    /********************************/
	    /* Définition du nom de l'Activity */
	    /********************************/

	    setTitle("Creer mon compte");

	    /**********************************************************/
	    /* Importation des caractéristiques des champs et boutons */
	    /**********************************************************/

	    inputLogin = (EditText) findViewById(R.id.enregistrerLogin);
	    inputMotDePasse = (EditText) findViewById(R.id.enregistrerMotDePasse);
	    btnValider = (Button) findViewById(R.id.btnValider);
	    btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

	    /*******************************/
	    /* Clic sur le bouton Enregistrer */
	    /*******************************/

	    btnValider.setOnClickListener(new View.OnClickListener() 
	    {      

	      public void onClick(View view) 
	      {
	  	      db.open();
	        	  
		       String login = inputLogin.getText().toString();
		       String password = inputMotDePasse.getText().toString();
	      
		       if(isValidPassword(password) && isValidLogin(db,login))
		       {
		    	   db.createUtilisateur(login,password);
		       }else{
		    	   
		       }
	      }
	    }); 
	        
	      /**************************/
	      /* Clic sur le lien Connexion */
	      /**************************/
	
	      btnLinkToLogin.setOnClickListener(new View.OnClickListener() 
	      {
	        public void onClick(View view) 
	        {
	          /***********************************/
	          /* Lancement de l'Activity "Connexion" */
	          /***********************************/
	
	          Intent i = new Intent(getApplicationContext(),  Connexion.class);
	          startActivity(i);
	
	          /****************************/
	          /* Ferme l'Activity "Connexion" */
	          /****************************/
	
	          finish();
	        }
	      });
	  }

	  /******************************************************/
	  /* Teste si le mot de passe fait plus de 8 caractères */
	  /******************************************************/ 
	  public static boolean isValidPassword(String password)
	  {
	    if(password.length() == 8)
	      return true;
	    else
	      return false;
	  }
	  /******************************************************/
	  /* Teste si le login fait plus de 8 caractères */
	  /******************************************************/   
	  public static boolean isValidLogin(LocalDataSource localdatabase, String log)
	  {
	    if(!localdatabase.existUtilisateurWithLogin(log) )
	      return true;
	    else
	      return false;
	  }

}
