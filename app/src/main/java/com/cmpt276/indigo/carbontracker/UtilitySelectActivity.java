package com.cmpt276.indigo.carbontracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.cmpt276.indigo.carbontracker.carbon_tracker_model.CarbonFootprintComponentCollection;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.UtilityModel;

import java.util.ArrayList;
import java.util.List;

/*
    implements Utility Select activity
 */
public class UtilitySelectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private long indexOfUtilityEditing = -1;
    private static final int ACTIVITY_RESULT_ADD = 30;
    private static final int ACTIVITY_RESULT_EDIT = 90;

    CarbonFootprintComponentCollection carbonFootprintInterface;
    ArrayList<UtilityModel> utilities;
    private int selectedItemIndex;
    int image = R.drawable.utility;
    CustomizedArrayAdapter adapter;
    CustomizedArrayAdapterItem arrayAdapterItems[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItemIndex = -1;
        setContentView(R.layout.activity_utility_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setupBottomNavigation();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        carbonFootprintInterface = CarbonFootprintComponentCollection.getInstance();
        createListView();
        MainMenu.setupMargin(this, R.id.content_utility_select);
    }

    private void setupList(){
        ArrayList<UtilityModel> utilities = carbonFootprintInterface.getUtilities(this);
        int utilitiesSize = utilities.size();
        arrayAdapterItems = new CustomizedArrayAdapterItem[utilitiesSize];
        for (int i = 0; i < utilitiesSize; i++){
            arrayAdapterItems[i] = new CustomizedArrayAdapterItem(image, utilities.get(i).getName(), "", "");
        }
        adapter = new CustomizedArrayAdapter(this, arrayAdapterItems, getTitles(arrayAdapterItems));
    }

    private String[] getTitles(CustomizedArrayAdapterItem items[]){
        if (items.length == 0){
            return new String[0];
        }
        String[] titles = new String[items.length];
        for (int i = 0; i < items.length; i++){
            titles[i] = items[i].getText1();
        }
        return titles;
    }

    private void setupBottomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                Intent intent = new Intent(UtilitySelectActivity.this, UtilityAddActivity.class);
                switch(item.getItemId())
                {
                    case R.id.action_add:
                        startActivityForResult(intent, ACTIVITY_RESULT_ADD);
                        break;
                    case R.id.action_edit:
                        editItem();
                        break;
                    case R.id.action_select:
                        onSelectUtility();
                        break;
                }
                return true;
            }
        });
        setBottomNavigationItemsStatus();
    }

    private void editItem(){
        if(selectedItemIndex > -1) {
            UtilityModel utility = utilities.get(selectedItemIndex);
            indexOfUtilityEditing = utility.getId();
            Intent intent = UtilityAddActivity.makeIntentForEditUtility(UtilitySelectActivity.this, utility);
            startActivityForResult(intent, ACTIVITY_RESULT_EDIT); //open the edit activity
        }
    }

    private void onSelectUtility(){
        Intent intent = new Intent(getBaseContext(), UtilityResultActivity.class);
        // Passing selected Utilities to the caller activity
        UtilityModel selectedUtility = carbonFootprintInterface.getUtilities(this).get(selectedItemIndex);
        intent.putExtra("utility", selectedUtility);
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    private void setBottomNavigationItemsStatus() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem select = menu.findItem(R.id.action_select);
        if(selectedItemIndex < 0){
            edit.setEnabled(false);
            select.setEnabled(false);
        }
        else{
            edit.setEnabled(true);
            select.setEnabled(true);
        }
    }

    //sample for demonstartion purposes
    private void createListView() {
        //set reference to listview
        ListView utilitiesList = (ListView) findViewById(R.id.utilities_select_list);
        populateUtilitiesList();

        final Context context = this;
        //handle click for each element in listview
        utilitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemIndex = position;
                adapter.setSelected(position);
                adapter.notifyDataSetChanged();
                setBottomNavigationItemsStatus();
            }
        });
    }

    private void populateUtilitiesList() {
        setupList();
        ListView utilitiesList = (ListView) findViewById(R.id.utilities_select_list);
        carbonFootprintInterface = CarbonFootprintComponentCollection.getInstance();
        utilities = carbonFootprintInterface.getUtilities(this);
        // putting Utilities in list
        List<String> utilityNameList = new ArrayList<>();
        //Add elements
        int counter = 0;
        for(UtilityModel v: utilities){
            if(!v.getIsDeleted()) {
                utilityNameList.add(v.getName());
            }
            counter++;
        }

        //Create array adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, //context
                android.R.layout.simple_list_item_1,
                utilityNameList //arrayList
        );
        adapter.setSelected(selectedItemIndex);
        //apply adapter ro listview
        utilitiesList.setAdapter(adapter);
    }

    private void setupEditUtilityLongPress() {
        ListView list = (ListView) findViewById(R.id.utilities_select_list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                UtilityModel utility = utilities.get(position);
                indexOfUtilityEditing = utility.getId();
                Intent intent = UtilityAddActivity.makeIntentForEditUtility(UtilitySelectActivity.this, utility);
                startActivityForResult(intent, ACTIVITY_RESULT_EDIT);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case ACTIVITY_RESULT_ADD:
                    populateUtilitiesList();
                    break;
                case ACTIVITY_RESULT_EDIT:
                    UtilityModel modifiedUtility = (UtilityModel) data.getSerializableExtra("utility");
                    modifiedUtility.setId(indexOfUtilityEditing);
                    carbonFootprintInterface.edit(this, modifiedUtility);
                    populateUtilitiesList();
                    break;
            }
        }
        else if (resultCode == UtilityAddActivity.RESULT_DELETE){
            selectedItemIndex = -1;
            populateUtilitiesList();
        }
        setBottomNavigationItemsStatus();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
        return new Intent(packageContext, UtilitySelectActivity.class);
    }
}
