package vn.edu.tdtu.vocawander.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdtu.vocawander.R;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.textO).setVisibility(View.INVISIBLE);
        findViewById(R.id.textC).setVisibility(View.INVISIBLE);
        findViewById(R.id.textA).setVisibility(View.INVISIBLE);
        findViewById(R.id.textW).setVisibility(View.INVISIBLE);
        findViewById(R.id.textA2).setVisibility(View.INVISIBLE);
        findViewById(R.id.textN).setVisibility(View.INVISIBLE);
        findViewById(R.id.textD).setVisibility(View.INVISIBLE);
        findViewById(R.id.textE).setVisibility(View.INVISIBLE);
        findViewById(R.id.textR).setVisibility(View.INVISIBLE);
        slideInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.textO).setVisibility(View.VISIBLE);
                findViewById(R.id.textC).setVisibility(View.VISIBLE);
                findViewById(R.id.textA).setVisibility(View.VISIBLE);
                findViewById(R.id.textW).setVisibility(View.VISIBLE);
                findViewById(R.id.textA2).setVisibility(View.VISIBLE);
                findViewById(R.id.textN).setVisibility(View.VISIBLE);
                findViewById(R.id.textD).setVisibility(View.VISIBLE);
                findViewById(R.id.textE).setVisibility(View.VISIBLE);
                findViewById(R.id.textR).setVisibility(View.VISIBLE);

                findViewById(R.id.textO).startAnimation(fadeInAnimation);
                findViewById(R.id.textC).startAnimation(fadeInAnimation);
                findViewById(R.id.textA).startAnimation(fadeInAnimation);
                findViewById(R.id.textW).startAnimation(fadeInAnimation);
                findViewById(R.id.textA2).startAnimation(fadeInAnimation);
                findViewById(R.id.textN).startAnimation(fadeInAnimation);
                findViewById(R.id.textD).startAnimation(fadeInAnimation);
                findViewById(R.id.textE).startAnimation(fadeInAnimation);
                findViewById(R.id.textR).startAnimation(fadeInAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        findViewById(R.id.textV).startAnimation(slideInAnimation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }, 3000);
    }
}