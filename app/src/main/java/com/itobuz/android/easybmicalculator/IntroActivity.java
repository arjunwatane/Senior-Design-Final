package com.itobuz.android.easybmicalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by Debasis on 18/10/16.
 */

public class IntroActivity extends AppIntro2 {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(IntroActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getSupportActionBar().hide();

        setGoBackLock(true);
        showSkipButton(false);

        addSlide(OneFragment.newInstance(R.layout.one_fragment));
        addSlide(new TwoFragment());
        addSlide(new ThreeFragment());
        addSlide(new FourFragment());
        addSlide(new FinalFragment());

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        //Finish this activity
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        //Finish this activity
        finish();
        //Start main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        //do the rest
    }

}
