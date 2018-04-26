package com.snownaul.studyguide.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kakao.util.helper.Utility;
import com.snownaul.studyguide.MainActivity;
import com.snownaul.studyguide.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    LinearLayout logo;
    TextView tvMadeBy;

    Timer timer=new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        getAppKeyHash();

        logo=findViewById(R.id.logo);
        tvMadeBy=findViewById(R.id.tvmadeby);

        Animation ani= AnimationUtils.loadAnimation(this,R.anim.appear_logo);
        logo.startAnimation(ani);
        ani=AnimationUtils.loadAnimation(this,R.anim.appear_madeby);
        tvMadeBy.startAnimation(ani);

        timer.schedule(task,3000);
    }

    TimerTask task=new TimerTask() {
        @Override
        public void run() {
            Intent intent=new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something+"\n\n\n");

                String hash=Utility.getKeyHash(this);
                Log.i("Key Hash",hash);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}
