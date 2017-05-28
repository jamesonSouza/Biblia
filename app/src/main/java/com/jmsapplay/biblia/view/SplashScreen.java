package com.jmsapplay.biblia.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.jmsapplay.biblia.R;

/**
 * Created by Jameson on 27/05/2017.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        /*
                metodo para anuncios google
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);*/
        Thread timeThread = new Thread(){
            @Override
            public void run(){
            try{

                sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {

                Intent  intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);

            }

            }


        };
            timeThread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
