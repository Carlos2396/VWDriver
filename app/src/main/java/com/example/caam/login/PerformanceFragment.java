package com.example.caam.login;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class PerformanceFragment extends Fragment {
    private static final String TAG = "PerformanceFragment";
    private static final String WEEK = "1 semana";
    private static final String MONTH = "1 mes";
    private static final String THREEMONTHS = "3 meses";
    private static final String YEAR = "1 año";

    private static final String BRAKES = "Frenados en seco";
    private static final String DELAYS = "Retrasos";

    Authentication auth;
    LineChart lineChart;
    Spinner categorySpinner;
    Spinner timegapSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_performance, container, false);

        auth = new Authentication(getActivity());

        categorySpinner = (Spinner) view.findViewById(R.id.category);
        timegapSpinner = (Spinner) view.findViewById(R.id.timegap);
        fillTimegapSpinner();
        fillCategorySpinner();
        categorySpinner.setOnItemSelectedListener(new SpinnerListener());
        timegapSpinner.setOnItemSelectedListener(new SpinnerListener());

        lineChart = (LineChart)view.findViewById(R.id.line_chart);
        return view;
    }

    /**
     * Spinner methods
     */

    public void fillTimegapSpinner() {
        ArrayList<String> timegaps = new ArrayList<String>();
        timegaps.add(WEEK);
        timegaps.add(MONTH);
        timegaps.add(THREEMONTHS);
        timegaps.add(YEAR);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, timegaps);
        timegapSpinner.setAdapter(adapter);
    }

    public void fillCategorySpinner() {
        ArrayList<String> timegaps = new ArrayList<String>();
        timegaps.add(BRAKES);
        timegaps.add(DELAYS);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, timegaps);
        categorySpinner.setAdapter(adapter);
    }

    public void getStatisics(String timegap, String category){
        new StatisticsManager().execute(String.format("%s/drivers/%d/statistics?type=_sort=date&%s&%s", Authentication.SERVER, auth.getDriverID(), category, timegap));
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String timegap = timegapSpinner.getSelectedItem().toString();
            String category = categorySpinner.getSelectedItem().toString();

            timegap = getQueryTimegap(timegap);
            category = getQueryCategory(category);
            //Log.d(TAG, "Timegap " + timegap);
            if(timegap != null && category != null){
                getStatisics(timegap, category);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

        private String getQueryTimegap(String timegap) {
            Date begin = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

            switch (timegap) {
                case WEEK:
                    begin = new Date(begin.getTime() - MILLIS_PER_DAY * 7);
                    return String.format("date_gte=%s", df.format(begin));
                case MONTH:
                    begin = new Date(begin.getTime() - MILLIS_PER_DAY * 30);
                    return String.format("date_gte=%s", df.format(begin));
                case THREEMONTHS:
                    begin = new Date(begin.getTime() - MILLIS_PER_DAY * 90);
                    return String.format("date_gte=%s", df.format(begin));
                case YEAR:
                    begin = new Date(begin.getTime() - MILLIS_PER_DAY * 365);
                    return String.format("date_gte=%s", df.format(begin));
            }
            Log.d(TAG, "null");
            return null;
        }

        private String getQueryCategory(String category) {
            switch (category){
                case DELAYS:
                    return "type=delay";
                case BRAKES:
                    return "type=brake";
            }

            return null;
        }
    }

    public void initLineChart(){
        lineChart.getDescription().setTextSize(12);
        lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.animateY(1000);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
    }

    public void setLineDataSetFormat(LineDataSet lineDataSet, int color) {
        //lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighLightColor(color);
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(color);
    }

    public IAxisValueFormatter getXAxisFormatter(final String[] values) {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return values[(int)value];
            }
        };
    }

    private class StatisticsManager extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            Log.d(TAG, params[0]);

            try{
                URL url = new URL(params[0]);

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                }
                else {
                    System.out.println(responseCode);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            loadStatistics(result);
        }

        private void loadStatistics(String json) {
            ArrayList<Entry> lineEntries = new ArrayList<Entry>();

            try{
                JSONObject entity;
                JSONArray statistics = new JSONArray(json);
                if(statistics.length() == 0){

                    return;
                }

                initLineChart();

                String[] dates = new String[statistics.length()];

                for (int i=0; i<statistics.length(); i++){
                    entity = statistics.getJSONObject(i);
                    dates[i] = entity.getString("date");
                    lineEntries.add(new Entry(i, entity.getInt("value")));
                }

                LineDataSet lineDataSet = null;

                switch (categorySpinner.getSelectedItem().toString()){
                    case DELAYS:
                        lineChart.getDescription().setText(String.format("Estadísticas de %s.", DELAYS));
                        lineDataSet = new LineDataSet(lineEntries, DELAYS);
                        setLineDataSetFormat(lineDataSet, R.color.colorDarkCyan);
                    case BRAKES:
                        lineChart.getDescription().setText(String.format("Estadísticas de %s.", BRAKES));
                        lineDataSet = new LineDataSet(lineEntries, BRAKES);
                        setLineDataSetFormat(lineDataSet, R.color.colorDarkOrange);
                }

                LineData lineData = new LineData(lineDataSet);

                XAxis xAxis= lineChart.getXAxis();
                xAxis.setLabelCount(lineDataSet.getEntryCount());
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(getXAxisFormatter(dates));
                lineChart.setData(lineData);
            }
            catch (JSONException je){
                Toast.makeText(getActivity(), "No se ha podido cargar la información, intente en unos minutos", Toast.LENGTH_SHORT).show();
                je.printStackTrace();
            }
        }
    }
}
