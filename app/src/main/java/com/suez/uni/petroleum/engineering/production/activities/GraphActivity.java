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

public class GraphActivity extends AppCompatActivity {

    private static final String LOG_TAG = GraphActivity.class.getSimpleName();

//    private GraphView mGraphView;

    private LineGraphSeries<DataPoint> mLineGraphSeries = new LineGraphSeries<>();

    private double xAxis;
    private double yAxis;

    private ArrayList<Double> mAllPressures = new ArrayList<>();
    private ArrayList<Double> mAllFlowRates = new ArrayList<>();

    private boolean mHasFe;
    private int mFeNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        LinearLayout linearLayout = findViewById(R.id.activity_graph_main_layout);


//        mGraphView = findViewById(R.id.activity_graph_graph_view);
//        mGraphView.setScaleX(0.9f);
//        mGraphView.setScaleY(0.9f);
//        mGraphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.x_axis_title));
//        mGraphView.getGridLabelRenderer().setVerticalAxisTitleColor(R.color.primary_color);
//        mGraphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.y_axis_title));
//        mGraphView.getGridLabelRenderer().setHorizontalAxisTitleColor(R.color.primary_color);





        mHasFe = getIntent().getBooleanExtra("has_fe", false);

        mAllPressures = (ArrayList<Double>) getIntent().getSerializableExtra("all_pressures");



        GraphView graphView = new GraphView(this);
        ZoomView zoomView = new ZoomView(this);
        zoomView.addView(graphView);

        createIprLine(graphView);

        linearLayout.addView(zoomView);


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
                    lineGraphSeries.setDataPointsRadius(0.5f);

                }

                if (i == 1) {
                    lineGraphSeries.setColor(R.color.purple_200);
                }

                graphView.addSeries(lineGraphSeries);
                graphView.getLegendRenderer().setVisible(true);

            }

        }


    }

}