package com.wubydax.gearreboot;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;


public class RebootActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogRebootFragment newFragment = new DialogRebootFragment();
        newFragment.show(getFragmentManager(), "reboot");



    }




}
