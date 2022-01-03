package com.suez.uni.petroleum.engineering.production.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.suez.uni.petroleum.engineering.production.R;
import com.suez.uni.petroleum.engineering.production.activities.views.ZoomView;

import java.util.ArrayList;

public class GraphWellDesignActivity extends AppCompatActivity {

    private static final String LOG_TAG = GraphActivity.class.getSimpleName();

//    private GraphView mGraphView;

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


//        mGraphView = findViewById(R.id.activity_graph_well_design_graph_view);
//        mGraphView.setScaleX(0.9f);
//        mGraphView.setScaleY(0.9f);
//        mGraphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.x_axis_title));
//        mGraphView.getGridLabelRenderer().setVerticalAxisTitleColor(R.color.primary_color);
//        mGraphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.y_axis_title));
//        mGraphView.getGridLabelRenderer().setHorizontalAxisTitleColor(R.color.primary_color);
//        mGraphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);


        mHasFe = getIntent().getBooleanExtra("has_fe", false);

        mAllPressures = (ArrayList<Double>) getIntent().getSerializableExtra("all_pressures");

        LinearLayout linearLayout = findViewById(R.id.activity_graph_well_main_layout);

        GraphView graphView = new GraphView(this);
        ZoomView zoomView = new ZoomView(this);
        zoomView.addView(graphView);



        graphView.setScaleX(0.9f);
        graphView.setScaleY(0.9f);
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.x_axis_title));
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(R.color.primary_color);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.y_axis_title));
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(R.color.primary_color);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(10);


        AllFlowRatesTableTwo = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rate_two");
        mDNumbers = getIntent().getIntExtra("d_numbers", 1);

        createIprLine(graphView);
        createWellDesignLine(graphView);


        linearLayout.addView(zoomView);

    }

    private void createWellDesignLine(GraphView graphView) {

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

                if (i == 0) {
                    lineGraphSeries.setColor(R.color.red);
                }


                if (i == 1) {
                    lineGraphSeries.setColor(R.color.red);
                }

                if (i == 2) {
                    lineGraphSeries.setColor(R.color.red);
                }


                graphView.getGridLabelRenderer().setNumHorizontalLabels(15);

                graphView.addSeries(lineGraphSeries);
                graphView.getLegendRenderer().setVisible(true);

            }


    }

    private void createIprLine(GraphView graphView) {

        graphView.setScaleX(0.9f);
        graphView.setScaleY(0.9f);
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.x_axis_title));
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(R.color.primary_color);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.y_axis_title));
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(R.color.primary_color);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(10);


        if (!mHasFe) {

            mAllFlowRates = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rates");

            for (int i = 0 ; i < mAllPressures.size() ; i++) {

                double yAxis = mAllPressures.get(i);

                double xAxis = mAllFlowRates.get(i);

                mLineGraphSeries.appendData(new DataPoint(xAxis, yAxis), true, 1000);

            }

            graphView.getGridLabelRenderer().setNumHorizontalLabels(15);
            mLineGraphSeries.setTitle("Inflow curve");
            mLineGraphSeries.setColor(R.color.black);
            graphView.addSeries(mLineGraphSeries);

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
                    lineGraphSeries.setColor(R.color.black);
                }


                graphView.getGridLabelRenderer().setNumHorizontalLabels(15);

                graphView.addSeries(lineGraphSeries);
                graphView.getLegendRenderer().setVisible(true);
            }

        }


    }


}