package org.rvinowise.ninja_dash.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rvinowise.bumblebee_jumper.R;
import org.rvinowise.ninja_dash.activities.Game_menu_activity;


public class Facebook_fragment extends Fragment {



    Game_menu_activity game_menu_activity;

    interface Facebook_social {
        void onFacebook_connected();
    }
    Facebook_social facebook_social;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            game_menu_activity = (Game_menu_activity) context;
            FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
            facebook_social = (Facebook_social)fragmentManager.findFragmentById(R.id.fragment_social);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Game_menu_activity");
        }
    }

    public String get_player_name() {
        return "facebook name";
    }

    public void sign_in() {

    }

    public void show_leaderboard() {

    }

    public void sign_out() {

    }
}

