package com.uoft.gobblgobble.schedulefiller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class AccessTokenReceiver extends AppCompatActivity {

    String string;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        string = intent.getDataString();
        Log.d("IN ON NEW INTENT", "STRING IS: " + string);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_token_receiver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        onNewIntent(getIntent());
        String accessToken = string.substring(string.indexOf("&access_token") + 14);
        String userId = string.substring(string.indexOf("&user_id")+9, string.indexOf("&token_type"));
        String tokenType = string.substring(string.indexOf("&token_type")+12,string.indexOf("&expires_in"));

        Log.i("TAG", string);
        Log.i("TAG", accessToken);
        Log.i("TAG", userId);
        Log.i("TAG", tokenType);
        Toast.makeText(this, "Total String: " + this.string, Toast.LENGTH_LONG).show();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("HAVE_AUTHORIZATION", true).apply();
        sharedPreferences.edit().putString("ACCESS_TOKEN", accessToken).apply();
        sharedPreferences.edit().putString("USER_ID",userId).apply();
        sharedPreferences.edit().putString("TOKEN_TYPE",tokenType).apply();
        sharedPreferences.edit().putString("FULL_AUTHORIZATION",tokenType+" "+accessToken).apply();

        Intent intent = new Intent(AccessTokenReceiver.this, display_schedulable_courses.class);
        startActivity(intent);
    }

}
