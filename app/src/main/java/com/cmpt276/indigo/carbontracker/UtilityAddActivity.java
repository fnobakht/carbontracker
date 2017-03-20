package com.cmpt276.indigo.carbontracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cmpt276.indigo.carbontracker.carbon_tracker_model.CarbonFootprintComponentCollection;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.DuplicateComponentException;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.UtilityModel;

/*
    implements Utility Add activity
 */

public class UtilityAddActivity extends AppCompatActivity {
    public static final int RESULT_DELETE = 15;
    CarbonFootprintComponentCollection carbonFootprintInterface;
    final Calendar startCalendar = Calendar.getInstance();
    final Calendar endCalendar = Calendar.getInstance();
    int duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        carbonFootprintInterface = CarbonFootprintComponentCollection.getInstance();
        setupOKButton();
        loadSpinner();
        //setupDeleteButton();
        gettingDate(R.id.StartDatebtn, startCalendar);
        gettingDate(R.id.endDateBtn,endCalendar );
    }


    private void gettingDate(final int id , final Calendar myCalendar){

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Button txt = (Button) findViewById(id);
                txt.setText(myCalendar.get(Calendar.YEAR) + " " + myCalendar.get(Calendar.MONTH) + " " + myCalendar.get(Calendar.DAY_OF_MONTH));
            }

        };
        Button txt = (Button) findViewById(id);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UtilityAddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


    }

    private void loadSpinner() {
        Spinner spinnerCompany  = (Spinner) findViewById(R.id.utility_add_spinner_company);
        spinnerCompany.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UtilityModel.Company.values()));
    }

    private void setupOKButton() {
        Button btn = (Button) findViewById(R.id.utility_add_button_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration = ((endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 360) + (
                        (endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)) * 30
                ) + (endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH));
                Toast.makeText(UtilityAddActivity.this, duration + "", Toast.LENGTH_LONG).show();

                Spinner spinnerCompany  = (Spinner) findViewById(R.id.utility_add_spinner_company);
                UtilityModel.Company company = (UtilityModel.Company) spinnerCompany.getSelectedItem();

                if (company == null) {
                    Toast.makeText(UtilityAddActivity.this, "Company should be selected", Toast.LENGTH_SHORT).show();
                }

                EditText editTextName = (EditText) findViewById(R.id.utility_add_editText_name);

                if (editTextName.getText().toString().length() == 0) {
                    Toast.makeText(UtilityAddActivity.this, "Please enter a Utility name.", Toast.LENGTH_SHORT)
                            .show();
                    return;

                }


                EditText editTextEnergy = (EditText) findViewById(R.id.utility_add_editText_energy_consumption);
                if (editTextEnergy.getText().toString().length() == 0) {
                    Toast.makeText(UtilityAddActivity.this, "Please enter Energy Consumption", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                EditText editTextPeople = (EditText) findViewById(R.id.utility_add_editText_num_ppl);
                if (editTextPeople.getText().toString().length() == 0) {
                    Toast.makeText(UtilityAddActivity.this, "Please enter Occupants", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                UtilityModel newUtility = createUtility();

                //adding Utility to collection if it is not duplicate
                if (!addUtility(newUtility)) {
                    return;
                }
                else {
                    Toast.makeText(UtilityAddActivity.this, "Utility Added!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("utility", newUtility);
                    setResult(Activity.RESULT_OK, intent);

                    finish();
                }
            }

        });
    }

    private UtilityModel createUtility() {
        // Get values from UI:
        Spinner spinnerCompany  = (Spinner) findViewById(R.id.utility_add_spinner_company);
        UtilityModel.Company company = (UtilityModel.Company) spinnerCompany.getSelectedItem();

        EditText editTextName = (EditText) findViewById(R.id.utility_add_editText_name);
        String name = editTextName.getText().toString();


        EditText editTextEnergy = (EditText) findViewById(R.id.utility_add_editText_energy_consumption);
        int energy = Integer.parseInt(editTextEnergy.getText().toString());
        UtilityModel newUtility = new UtilityModel(company);
        newUtility.setName(name);
        newUtility.setBillingPeriodInDays(duration);
        newUtility.setTotalEnergyConsumptionInGWH(energy);
        return  newUtility;
    }

    boolean addUtility(UtilityModel utility){
        try{
            carbonFootprintInterface.add(this,utility);
        }
        catch(DuplicateComponentException e){
                Toast.makeText(UtilityAddActivity.this, "This Utility already exists.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static Intent makeIntentForNewUtility(Context packageContext) {
        return new Intent(packageContext, UtilityAddActivity.class);
    }

    public static Intent makeIntentForEditUtility(Context packageContext, UtilityModel utility) {
        Intent intent = makeIntentForNewUtility(packageContext);
        intent.putExtra("utility", utility);
        return intent;
    }
}
