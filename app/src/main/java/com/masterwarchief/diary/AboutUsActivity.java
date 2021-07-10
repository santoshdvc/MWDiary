package com.masterwarchief.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AboutUsActivity extends AppCompatActivity {
    ConstraintLayout contact_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.about_page_bar);
        setSupportActionBar(myChildToolbar);
        contact_view=findViewById(R.id.contact_view);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle("About Developer");
        ab.setElevation(3f);
        ab.setDisplayHomeAsUpEnabled(true);
        contact_us();
    }
    public void contact_us() {
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //.addGroup("Developer Profile")
                .setImage(R.drawable.diary_small)
                .setDescription("")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with me")
                .addEmail("santoshdvc@gmail.com")
                //.addWebsite("http://medyo.github.io/")
                //.addPlayStore("com.ideashower.readitlater.pro")
                .addInstagram("santa_d_devil")
                .addGitHub("santoshdvc")
                .addWebsite("https://www.linkedin.com/in/santoshkumarbehera1998/", "LinkedIn")
                .create();
        aboutPage.setBackgroundColor(Color.WHITE);
        contact_view.addView(aboutPage);
        contact_view.setBackgroundColor(Color.WHITE);
    }


}
