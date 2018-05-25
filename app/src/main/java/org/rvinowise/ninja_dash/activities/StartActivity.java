package org.rvinowise.ninja_dash.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.rvinowise.bumblebee_jumper.BuildConfig;
import org.rvinowise.bumblebee_jumper.R;
import org.rvinowise.ninja_dash.activities.fragments.Social_fragment;

import org.rvinowise.game_engine.ads.Ads;

//facebook
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class StartActivity extends FragmentActivity
implements Game_menu_activity
{
    CallbackManager facebookCallbackManager;

    private int last_score = 0;

    final String TAG = "StartActivity";
    private static final int GAME_REQUEST = 0;

    ImageView img_tutorial;
    TextView lab_hello;
    TextView lab_score;
    LoginButton btn_login_facebook;

    //Google_fragment google_fragment;
    //Facebook_fragment facebook_fragment;
    Social_fragment social_fragment;

    enum Special_launch {
        first_launch,
        first_updated_launch,
        routine_launch
    }
    private Special_launch special_launch;

    private Ads ads;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check_first_run();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        facebookCallbackManager = CallbackManager.Factory.create();

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        init_layout();

        ads = new Ads(this,
                getString(R.string.app_admob_id),
                getString(R.string.bumblebee_interstitial_ad_1));
        ads.request_interstitial();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("last_score", last_score);
        super.onSaveInstanceState(savedInstanceState);
    }
    public void restoreInstanceState(Bundle savedInstanceState) {
        last_score = savedInstanceState.getInt("last_score");
    }

    private void init_layout() {
        setContentView(R.layout.activity_start);

        img_tutorial = (ImageView) findViewById(R.id.img_tutorial);
        if (getSpecial_launch() == Special_launch.first_launch) {
            img_tutorial.setVisibility(View.VISIBLE);
        } else {
            img_tutorial.setVisibility(View.GONE);
        }

        lab_hello = (TextView) findViewById(R.id.lab_hello);
        lab_score = (TextView) findViewById(R.id.lab_score);
        if(last_score ==0) {
            lab_score.setVisibility(View.GONE);
        } else {
            lab_score.setText(String.valueOf(last_score));
        }

        //google_fragment = (Google_fragment) getFragmentManager().findFragmentById(R.id.fragment_google);
        //facebook_fragment = (Facebook_fragment) getFragmentManager().findFragmentById(R.id.fragment_facebook);
        social_fragment = (Social_fragment) getFragmentManager().findFragmentById(R.id.fragment_social);


        btn_login_facebook = (LoginButton) findViewById(R.id.btn_login_facebook);
        btn_login_facebook.setReadPermissions("email");
        // Callback registration
        btn_login_facebook.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("FACEBOOK", "onSuccess "+loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("FACEBOOK", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FACEBOOK", "onError "+exception);
            }
        });



        draw_disconnected_interface();
    }



    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ACTT", "onPause()");

    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected  void onResume() {
        super.onResume();
    }



    private void draw_connected_interface() {
        if (last_score > 0) {
            lab_hello.setText(getString(R.string.yout_score_is));
        }
    }
    private void draw_disconnected_interface() {
        if (last_score > 0) {
            lab_hello.setText(getString(R.string.yout_score_is));
        } else {
            lab_hello.setText(getString(R.string.sign_in_why));
        }
    }




    public void on_btn_start_game_click(View v) {
        clear_score();
        Intent game_intent = new Intent(StartActivity.this, GameActivity.class);
        startActivityForResult(game_intent, GAME_REQUEST);
    }

    private void clear_score() {
        last_score = 0;
    }


    private void can_show_ads() {
        final int min_score_for_ads = 10;
        if (last_score >= min_score_for_ads) {
            ads.can_show_interstitial();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean handled = facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GAME_REQUEST:
                fetch_result_score_from_game(resultCode, data);
                img_tutorial.setVisibility(View.GONE);
                break;
        }
    }

    private void fetch_result_score_from_game(int resultCode, Intent data) {
        try {
            switch (resultCode) {
                case RESULT_OK:
                case RESULT_CANCELED:
                    last_score = data.getIntExtra("score", 0);
                    if (last_score > 0) {
                        lab_score.setVisibility(View.VISIBLE);
                        lab_score.setText(String.valueOf(last_score));

                    } else {
                        lab_score.setVisibility(View.GONE);
                    }
                    can_show_ads();
                    //can_connect_to_cloud();
                    // сохранить очки в облаке можно только после Коннекшена Гугл-айпи, а это НЕ сразу после возврата
                    // результата Активити игры
            }
        } catch (RuntimeException e) {
            last_score = 0;
        }
    }


    public static Dialog info_dialog(Activity activity, String text) {
        return (new AlertDialog.Builder(activity)).setMessage(text)
                .setNeutralButton(android.R.string.ok, null).create();
    }

    private Special_launch getSpecial_launch() {
        return special_launch;
    }

    private void check_first_run() {

        final String PREFS_NAME = "prefs";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            special_launch = Special_launch.routine_launch;
        } else if (savedVersionCode == DOESNT_EXIST) {
            special_launch = Special_launch.first_launch;
        } else if (currentVersionCode > savedVersionCode) {
            special_launch = Special_launch.first_updated_launch;
        }
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    //@Override
    /*public void onAdClosed() {
        Log.d("ADS","onAdClosed");
    }
    @Override
    public void onAdLeftApplication() {
        can_syncronize_score_with_cloud();
    }*/

    @Override
    public int getLast_score() {
        return last_score;
    }

    @Override
    public Social_fragment getSocial_fragment() {
        return social_fragment;
    }


}
