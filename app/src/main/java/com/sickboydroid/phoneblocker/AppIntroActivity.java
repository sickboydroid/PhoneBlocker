package com.sickboydroid.phoneblocker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.appintro.AppIntroPageTransformerType;
import com.github.appintro.SlideBackgroundColorHolder;
import com.sickboydroid.phoneblocker.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.createInstance("Welcome!",
                " \"This is a demo example in java of AppIntro library, with a custom background on each slide!",
                R.mipmap.ic_launcher,
                R.color.materialDark));
        addSlide(AppIntroFragment.createInstance(
                "Clean App Intros",
                "This library offers developers the ability to add clean app intros at the start of their apps.",
                R.mipmap.ic_launcher,
                R.color.materialPink
        ));
        addSlide(AppIntroFragment.createInstance(
                "Simple, yet Customizable",
                "The library offers a lot of customization, while keeping it simple for those that like simple.",
                R.mipmap.ic_launcher,
                R.color.primaryDarkColor
        ));
        addSlide(AppIntroFragment.createInstance(
                "Explore",
                "Feel free to explore the rest of the library demo!",
                R.mipmap.ic_launcher,
                R.color.materialTeal));
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        showStatusBar(true);
        setScrollDurationFactor(3);
        setSystemBackButtonLocked(true);
        setWizardMode(true);
        setButtonsEnabled(true);
    }
}