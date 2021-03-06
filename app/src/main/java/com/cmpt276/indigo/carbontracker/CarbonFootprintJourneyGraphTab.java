package com.cmpt276.indigo.carbontracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmpt276.indigo.carbontracker.carbon_tracker_model.CarbonFootprintComponentCollection;
import com.cmpt276.indigo.carbontracker.carbon_tracker_model.JourneyModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by arya on 05/03/17.
 * Implements the Graph for each Journey
 */

public class CarbonFootprintJourneyGraphTab extends Fragment {
    ArrayList<JourneyModel> journeys;
    CarbonFootprintComponentCollection carbonInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_carbon_footprint_journey_graph, container, false);

        carbonInterface = CarbonFootprintComponentCollection.getInstance();
//creating a pie graph with all the journeys of all time in it
        journeys = carbonInterface.getJournies(getActivity());

        PieChart pieChart = (PieChart) rootView.findViewById(R.id.bar_graph);

        ArrayList<String> pieEntriesLabels = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < journeys.size(); i++) {
            pieEntriesLabels.add(journeys.get(i).getRouteModel().getName());
            pieEntries.add(new PieEntry((float)journeys.get(i).getCo2EmissionInSpecifiedUnits(), pieEntriesLabels.get(i)));
        }

        PieDataSet dataSets = new PieDataSet(pieEntries,null);
        PieData data = new PieData(dataSets);
        dataSets.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSets.setValueTextSize(20f);
        pieChart.setDescription(null);
        pieChart.setData(data);
        pieChart.setEntryLabelTextSize(20f);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(2000);
        pieChart.invalidate();
        return rootView;
    }
}
