package com.motondon.navigationdrawerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by Joca on 12/8/2016.
 */

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Help");

        // Show a back narrow indicator allowing user to go back to the previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HelpFragment fragment = (HelpFragment) getSupportFragmentManager().findFragmentByTag(HelpFragment.TAG);
        if (fragment == null) {
            fragment = new HelpFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.help_activity_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
