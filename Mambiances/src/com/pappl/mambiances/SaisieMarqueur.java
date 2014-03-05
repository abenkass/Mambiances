package com.pappl.mambiances;

import java.util.LinkedList;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaisieMarqueur extends Activity {

	/*
	 * the button for validate the data capture
	 */
	Button envoyer = null;
	/*
	 * written words by the user
	 */
	EditText mots = null ;
	/*
	 * List of the written words
	 */
	LinkedList<String> liste; 
	
	TextView result = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saisie_marqueur);

		mots = (EditText)findViewById(R.id.mots);
		envoyer = (Button)findViewById(R.id.bouton_envoyer);
		result = (TextView)findViewById(R.id.result);
		//on touche le bouton envoyer
		envoyer.setOnClickListener(envoyerListener);
		
	}
	
	//Methodes
	  private TextWatcher textWatcher = new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	      result.setText("rentrer qqchose");
	    }
	    @Override	
	    public void beforeTextChanged(CharSequence s, int start, int count,
	      int after) {
	  
	    }
	    @Override
	    public void afterTextChanged(Editable s) {
	  
	    }
	  };
	  
	// Cliquer sur le bouton envoyer
	private OnClickListener envoyerListener = new OnClickListener() {
	@Override
	public void onClick(View v){
		String m= mots.getText().toString();
		liste = new LinkedList();
		StringTokenizer st = new StringTokenizer(m,",");
		while (st.hasMoreTokens()) {
			liste.add(st.nextToken());
		}
		//test
		result.setText("Vous avez saisi le(s) mot(s) :"+ liste.toArray() );
	}
	};

}
