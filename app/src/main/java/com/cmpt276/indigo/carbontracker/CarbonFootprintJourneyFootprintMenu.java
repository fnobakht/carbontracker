package com.cmpt276.indigo.carbontracker;

/**
 * Created by arya on 05/03/17.
 * Implements the menu for each for the graph
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class CarbonFootprintJourneyFootprintMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GraphJourneyMenu.OnFragmentInteractionListener,
        GraphBar.OnFragmentInteractionListener,
        GraphPie.OnFragmentInteractionListener{

    Fragment jouneryGraphFragment;
    Fragment barChartFragment;
    Fragment pieChartFragment;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbon_footprint_journey_footprint_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupBottomNavigation();
        addFragment(savedInstanceState);
    }

    private void addFragment(Bundle savedInstanceState){
        jouneryGraphFragment = new GraphJourneyMenu();
        barChartFragment = new GraphBar();
        pieChartFragment = new GraphPie();
        currentFragment = jouneryGraphFragment;
        if (findViewById(R.id.nav_carbon_footprint) != null) {
            if (savedInstanceState != null)
                return;

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.nav_carbon_footprint, jouneryGraphFragment);
            transaction.add(R.id.nav_carbon_footprint, barChartFragment);
            transaction.add(R.id.nav_carbon_footprint, pieChartFragment);
            transaction.hide(barChartFragment);
            transaction.hide(pieChartFragment);
            transaction.commit();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(currentFragment);
        transaction.show(fragment);
        transaction.commit();
        currentFragment = fragment;
        //fm.beginTransaction().remove(currentFragment).commit();
        //fm.beginTransaction().add(R.id.nav_carbon_footprint, fragment).commit();
        //currentFragment = fragment;
        //fm.beginTransaction().replace(R.id.nav_carbon_footprint, fragment).commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId())
                {
                    case R.id.action_journey_footprint:
                        replaceFragment(jouneryGraphFragment);
                        break;
                    case R.id.action_bar_graph:
                        replaceFragment(barChartFragment);
                        break;
                    case R.id.action_pie_graph:
                        replaceFragment(pieChartFragment);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return SideNavigationManager.handleSideNavigationSelection(this, item);
    }

    public static Intent makeIntent(Context packageContext) {
        return new Intent(packageContext, CarbonFootprintJourneyFootprintMenu.class);
    }
}
