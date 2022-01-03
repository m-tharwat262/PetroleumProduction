package com.suez.uni.petroleum.engineering.production.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.suez.uni.petroleum.engineering.production.R;

import java.util.ArrayList;

public class GraphWellDesignActivity extends AppCompatActivity {

    private static final String LOG_TAG = GraphActivity.class.getSimpleName();

    private GraphView mGraphView;

    private LineGraphSeries<DataPoint> mLineGraphSeries = new LineGraphSeries<>();

    private ArrayList<Double> mAllPressures = new ArrayList<>();
    private ArrayList<Double> mAllFlowRates = new ArrayList<>();

    private boolean mHasFe;
    private int mFeNumbers;




    private ArrayList<Double> AllFlowRatesTableTwo = new ArrayList<>();
    private int mDNumbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_well_design);


        mGraphView = findViewById(R.id.activity_graph_well_design_graph_view);
        mGraphView.setScaleX(0.9f);
        mGraphView.setScaleY(0.9f);
        mGraphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.x_axis_title));
        mGraphView.getGridLabelRenderer().setVerticalAxisTitleColor(R.color.primary_color);
        mGraphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.y_axis_title));
        mGraphView.getGridLabelRenderer().setHorizontalAxisTitleColor(R.color.primary_color);
        mGraphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);


        mHasFe = getIntent().getBooleanExtra("has_fe", false);

        mAllPressures = (ArrayList<Double>) getIntent().getSerializableExtra("all_pressures");

        createIprLine();




        AllFlowRatesTableTwo = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rate_two");
        mDNumbers = getIntent().getIntExtra("d_numbers", 1);


        createWellDesignLine();




    }

    private void createWellDesignLine() {

        Log.i(LOG_TAG, "unix the point :");
        mDNumbers = getIntent().getIntExtra("d_numbers", 1);

            for (int i = 0; i < mDNumbers; i++) {

                ArrayList<Double> allPressures = (ArrayList<Double>) getIntent().getSerializableExtra("all_pressure_two_" + i);

                LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();

                for (int j = 0; j < AllFlowRatesTableTwo.size(); j++) {

                    double xAxis = AllFlowRatesTableTwo.get(j);


                    double yAxis = allPressures.get(j);

                    Log.i(LOG_TAG, "unix the point : " + xAxis + "  " + yAxis);

                    if (xAxis == -1) {
                        break;
                    }
                    lineGraphSeries.appendData(new DataPoint(xAxis, yAxis), true, 1000);
                    lineGraphSeries.setTitle(getString(R.string.d_with_number, (i + 1)));

                }

                if (i == 1) {
                    lineGraphSeries.setColor(R.color.purple_200);
                }


                if (i == 1) {
                    lineGraphSeries.setColor(R.color.table_black);
                }


                mGraphView.getGridLabelRenderer().setNumHorizontalLabels(15);

                mGraphView.addSeries(lineGraphSeries);
                mGraphView.getLegendRenderer().setVisible(true);

            }


    }

    private void createIprLine() {

        if (!mHasFe) {

            mAllFlowRates = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rates");

            for (int i = 0 ; i < mAllPressures.size() ; i++) {

                double yAxis = mAllPressures.get(i);

                double xAxis = mAllFlowRates.get(i);

                mLineGraphSeries.appendData(new DataPoint(xAxis, yAxis), true, 1000);

            }

            mGraphView.getGridLabelRenderer().setNumHorizontalLabels(15);
            mGraphView.addSeries(mLineGraphSeries);

        } else {

            mFeNumbers = getIntent().getIntExtra("fe_numbers", 1);

            for (int i = 0; i < mFeNumbers; i++) {

                ArrayList<Double> allFlowRatesWithFe = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rates_with_fe_" + i);

                LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();

                for (int j = 0 ; j < mAllPressures.size() ; j++) {

                    double yAxis = mAllPressures.get(j);

                    double xAxis = allFlowRatesWithFe.get(j);

                    if (xAxis == -1) {
                        break;
                    }
                    lineGraphSeries.appendData(new DataPoint(xAxis, yAxis), true, 1000);
                    lineGraphSeries.setTitle(getString(R.string.fe_with_number, (i+1)));

                }

                if (i == 1) {
                    lineGraphSeries.setColor(R.color.purple_200);
                }


                mGraphView.getGridLabelRenderer().setNumHorizontalLabels(15);

                mGraphView.addSeries(lineGraphSeries);
                mGraphView.getLegendRenderer().setVisible(true);
            }

        }


    }


}