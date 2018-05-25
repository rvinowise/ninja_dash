package org.rvinowise.ninja_dash.activities.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.rvinowise.bumblebee_jumper.R;
import org.rvinowise.ninja_dash.activities.Game_menu_activity;

public class Social_fragment extends Fragment
implements Google_fragment.Google_social,
    Facebook_fragment.Facebook_social,
        View.OnClickListener
{

    Button btn_sign_out;
    Button btn_show_leaderboard;

    Game_menu_activity game_menu_activity;


    enum Connection_nerwork {
        google,
        facebook
    }
    Connection_nerwork connected_to;

    boolean player_wants_signing_in = false;
    Google_fragment google_fragment;
    Facebook_fragment facebook_fragment;

    Context context;
    @Override
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_layout();

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            game_menu_activity = (Game_menu_activity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Game_menu_activity");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    public void restoreInstanceState(Bundle savedInstanceState) {
        can_connect_to_cloud();
    }
    private void can_connect_to_cloud() {
        //if (player_wants_signing_in) {
            if (connected_to == Connection_nerwork.google) {
                google_fragment.sign_in();
            } else if (connected_to == Connection_nerwork.facebook) {
                facebook_fragment.sign_in();
            }
        //}
    }

    private void init_layout() {
        btn_sign_out = (Button) getView().findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(this);
        btn_show_leaderboard = (Button) getView().findViewById(R.id.btn_show_leaderboard);
        btn_show_leaderboard.setOnClickListener(this);
        FragmentManager fragmentManager = getChildFragmentManager();
        facebook_fragment = (Facebook_fragment) fragmentManager.findFragmentById(R.id.fragment_facebook);
        google_fragment = (Google_fragment) fragmentManager.findFragmentById(R.id.fragment_google);
        // draw_disconnected_interface();
    }



    @Override
    public void onClick(View v) {
        switch ( v.getId() )
        {
            case R.id.btn_show_leaderboard:
                onShowLeaderboardsRequested(v);
                break;
            case R.id.btn_sign_out:
                onSignOutButtonClicked(v);
                break;
        }
    }


    public void onShowLeaderboardsRequested(View v) {
        if (connected_to==Connection_nerwork.google) {
            google_fragment.show_leaderboard();
        } else if (connected_to==Connection_nerwork.facebook) {
            facebook_fragment.show_leaderboard();
        } else {
            return;
        }
    }

    public void onSignOutButtonClicked(View v) {
        if (connected_to==Connection_nerwork.google) {
            google_fragment.sign_out();
        } else if (connected_to==Connection_nerwork.facebook) {
            facebook_fragment.sign_out();
        } else {
            return;
        }
        player_wants_signing_in = false;

        draw_disconnected_interface();

    }


    private String get_player_name() {
        if (connected_to == Connection_nerwork.google) {
            return google_fragment.get_player_name();
        } else if (connected_to == Connection_nerwork.facebook) {
            return facebook_fragment.get_player_name();
        } else {
            return "";
        }
    }

    private void draw_connected_interface() {
        try {
            facebook_fragment.getView().setVisibility(View.GONE);
            google_fragment.getView().setVisibility(View.GONE);
        } catch (NullPointerException e){}
        btn_sign_out.setVisibility(View.VISIBLE);
        btn_show_leaderboard.setVisibility(View.VISIBLE);
    }
    private void draw_disconnected_interface() {
        try {
            facebook_fragment.getView().setVisibility(View.VISIBLE);
            google_fragment.getView().setVisibility(View.VISIBLE);
        } catch (NullPointerException e){}
        btn_sign_out.setVisibility(View.GONE);
        btn_show_leaderboard.setVisibility(View.GONE);
    }

    @Override
    public void onGoogle_connected() {
        connected_to = Connection_nerwork.google;
        draw_connected_interface();
    }
    @Override
    public void onGoogle_connection_failed() {
        draw_disconnected_interface();
    }

    @Override
    public void onFacebook_connected() {
        connected_to = Connection_nerwork.facebook;
        draw_connected_interface();
    }
}
