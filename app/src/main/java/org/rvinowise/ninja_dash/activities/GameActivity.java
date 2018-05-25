package org.rvinowise.ninja_dash.activities;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;


import org.rvinowise.ninja_dash.NinjaEngine;

import org.rvinowise.game_engine.Engine;


public class GameActivity extends Activity
        implements
        View.OnTouchListener,
        Engine.System_listener

{

    private GLSurfaceView glSurfaceView;
    private boolean renderer_set = false;

    private NinjaEngine engine;
    //private Ads ads;

    protected Handler handler_menu = new Handler();


    public GameActivity() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        engine = new NinjaEngine();
        engine.setContext(this);
        engine.setSystem_listener(this);
        engine.setHandler_menu(handler_menu);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(engine);

        setContentView(glSurfaceView);
        glSurfaceView.setOnTouchListener(this);


        //ads = new Ads(this);
        //ads.request_rewarded();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        engine.onResume();
        //ads.onResume();
        if (renderer_set) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        engine.onPause();
        //ads.onPause();
        if (renderer_set) {
            glSurfaceView.onPause();
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //ads.onDestroy();
        //return_score_to_start_screen();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        engine.change_resolution(config.screenWidthDp, config.screenHeightDp);

    }

    /*@Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.pu
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return engine.onTouch(v, event);
    }

    @Override
    public void return_score_to_start_screen() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("score", engine.getScore().get_current());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        return_score_to_start_screen();
    }

}
