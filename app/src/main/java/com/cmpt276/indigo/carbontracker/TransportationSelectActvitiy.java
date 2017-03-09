package com.cmpt276.indigo.carbontracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cmpt276.indigo.carbontracker.carbon_tracker_model.CarbonFootprintComponent;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.CarbonFootprintComponentCollection;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.JourneyModel;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.VehicleModel;

import java.util.ArrayList;
import java.util.List;

/*

 */

public class TransportationSelectActvitiy extends AppCompatActivity {

    private int indexOfVehicleEditing = -1;
    CarbonFootprintComponentCollection carbonFootprintInterface;
    private static final int ACTIVITY_RESULT_ADD = 50;
    private static final int ACTIVITY_RESULT_EDIT = 100;

    List<Integer> vehicle_positionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_transportation_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Allows for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        startAddActivity();
        createListView();
        setupEditVehicleLongPress();
    }

    // FAB button to launch add activity
    private void startAddActivity() {
        //set reference to fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportationSelectActvitiy.this,TransportationAddActivity.class);
                startActivityForResult(intent, ACTIVITY_RESULT_ADD);
            }
        });
    }

    //sample for demonstartion purposes
    private void createListView() {
        //set reference to listview
        ListView carList = (ListView) findViewById(R.id.transportation_select_list);
        populateVehiclesList();

        //handle click for each element in listview
        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                // Passing selected vehicle to the caller activity
                int realPosition = vehicle_positionList.get(position);
                VehicleModel selectedVehicle = carbonFootprintInterface.getVehicles().get(realPosition);
                intent.putExtra("vehicle", selectedVehicle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void populateVehiclesList() {
        ListView carList = (ListView) findViewById(R.id.transportation_select_list);
        carbonFootprintInterface = CarbonFootprintComponentCollection.getInstance();
        ArrayList<VehicleModel> vehicles = carbonFootprintInterface.getVehicles();
        // putting vehicles in list
        List<String> vehicle_nameList = new ArrayList<>();
        vehicle_positionList = new ArrayList<>();
        //Add elements
        int counter = 0;
        for(VehicleModel v: vehicles){
            if(!v.getIsDeleted()) {
                vehicle_nameList.add(v.getName());
                vehicle_positionList.add(counter);
            }
            counter++;
        }

        //Create array adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, //context
                android.R.layout.simple_list_item_1,
                vehicle_nameList //arrayList
        );

        //apply adapter ro listview
        carList.setAdapter(arrayAdapter);
    }

    private void setupEditVehicleLongPress() {
        final ArrayList<VehicleModel> vehicles = carbonFootprintInterface.getVehicles();
        ListView list = (ListView) findViewById(R.id.transportation_select_list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition = vehicle_positionList.get(position);
                indexOfVehicleEditing = realPosition;
                VehicleModel vehicle = vehicles.get(realPosition);

                Intent intent = TransportationAddActivity.makeIntentForEditVehicle(TransportationSelectActvitiy.this, vehicle);
                startActivityForResult(intent, ACTIVITY_RESULT_EDIT);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case ACTIVITY_RESULT_ADD:
                    populateVehiclesList();
                    break;
                case ACTIVITY_RESULT_EDIT:
                    VehicleModel modifiedVehicle = (VehicleModel) data.getSerializableExtra("vehicle");
                    VehicleModel vehicle = carbonFootprintInterface.getVehicles().get(indexOfVehicleEditing);
                    for(JourneyModel v: carbonFootprintInterface.getJournies()){
                        if(v.getVehicleModel().equals(vehicle))
                        {
                            v.setVehicleModel(modifiedVehicle);
                        }
                    }
                    vehicle.setName(modifiedVehicle.getName());
                    vehicle.setModel(modifiedVehicle.getModel());
                    vehicle.setMake(modifiedVehicle.getMake());
                    vehicle.setYear(modifiedVehicle.getYear());
                    populateVehiclesList();
            }

        }
        else if (resultCode == TransportationAddActivity.RESULT_DELETE){
            populateVehiclesList();
        }

    }

}
