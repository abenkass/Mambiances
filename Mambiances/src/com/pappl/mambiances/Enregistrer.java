package com.pappl.mambiances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.db.MySQLiteHelper;
import com.pappl.mambiances.sync.Sync;
import com.pappl.utils.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Enregistrer extends Activity {
	  
	  Button btnValider;
	  Button btnLinkToLogin;
	  EditText inputLogin;
	  EditText inputMotDePasse;
	  
	  ProgressDialog pDialog;
	  
	  JSONParser jsonParser = new JSONParser();
	  
	  //url to the php class
	  private static String url_create_user = "http://mambiances.ser-info-02.ec-nantes.fr/create_user.php";
	    
	  private LocalDataSource datasource;
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.enregistrer);   
  	  	
	    datasource = Connexion.datasource;

	    /**************************************/
	    /* Synchronisation avec la bdd externe*/
	    /**************************************/
	    
	    HashMap<String,Integer> maxIds = Sync.getMaxId();
	    int maxIdEx = maxIds.get("Utilisateur");
	    int maxIdInt = datasource.getRowCount(MySQLiteHelper.TABLE_UTILISATEUR);
	    if(maxIdEx != maxIdInt){
	    	new Sync.LoadAllUsers().execute();
	    }
	    
	    /********************************/
	    /* DÃ©finition du nom de l'Activity */
	    /********************************/

	    setTitle("Creer mon compte");

	    /**********************************************************/
	    /* Importation des caractÃ©ristiques des champs et boutons */
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
	  	      datasource.open();
	        	  
		       String login = inputLogin.getText().toString();
		       String password = inputMotDePasse.getText().toString();
	      
		       if(!isValidLogin(datasource,login))
		       {
		    	   Toast.makeText(getApplicationContext(), 
                           "Le nom d'utilisateur existe déjà.", Toast.LENGTH_LONG).show(); 
		       }else if(!isValidPassword(password)){
		    	   Toast.makeText(getApplicationContext(), 
                           "Le mot de passe n'est pas assez long", Toast.LENGTH_LONG).show(); 
		       }else{
		    	   new CreateNewUtilisateur().execute();
		    	   new Sync.LoadAllUsers().execute();
		       }
		       datasource.close();
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
	          /* Ferme l'Activity "Enregistrer" */
	          /****************************/
	
	          finish();
	        }
	      });
	  }

	  /******************************************************/
	  /* Teste si le mot de passe fait plus de 8 caractÃ¨res */
	  /******************************************************/ 
	  public static boolean isValidPassword(String password)
	  {
	    if(password.length() > 8)
	      return true;
	    else
	      return false;
	  }
	  /******************************************************/
	  /* Teste si le login fait plus de 8 caractÃ¨res */
	  /******************************************************/   
	  public static boolean isValidLogin(LocalDataSource localdatabase, String log)
	  {
	    if(!localdatabase.existUtilisateurWithLogin(log) )
	      return true;
	    else
	      return false;
	  }
	  
	  class CreateNewUtilisateur extends AsyncTask<String, String, String> {
		  
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(Enregistrer.this);
	            pDialog.setMessage("Création de l'utilisateur..");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }
	 
	        /**
	         * Creating product
	         * */
	        protected String doInBackground(String... args) {
	            String login = inputLogin.getText().toString();
	            String mdp = inputMotDePasse.getText().toString();
	 
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("login", login));
	            params.add(new BasicNameValuePair("mdp", mdp));
	 
	            // getting JSON Object
	            // Note that create product url accepts POST method
	            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
	                    "POST", params);
	 
	            // check log cat fro response
	            Log.d("Create Response", json.toString());
	 
	            // check for success tag
	            try {
	                int success = json.getInt("success");
	 
	                if (success == 1) {
	                	Toast.makeText(getApplicationContext(), 
	                            "Compte créé. Vous pouvez vous connecter avec vos identifiants.", Toast.LENGTH_LONG).show();
	 		    	    Intent i = new Intent(getApplicationContext(),  Connexion.class);
	 			        startActivity(i);
	 
	                    // closing this screen
	                    finish();
	                } else {
	                    // failed to create product
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 
	        /**
	         * After completing background task Dismiss the progress dialog
	         * **/
	        protected void onPostExecute(String file_url) {
	            // dismiss the dialog once done
	            pDialog.dismiss();
	        }
	 
	    }

}
