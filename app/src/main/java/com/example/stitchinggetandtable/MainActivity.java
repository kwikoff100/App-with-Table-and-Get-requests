package com.example.stitchinggetandtable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Typeface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String longString;
    TableLayout tableLayout;
    ArrayList<expenseDataObject> expenseObjs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //General page setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button to go fetch data
        Button fetchDataButton = (Button) findViewById(R.id.getData);

        //Button was clicked
        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Clear any previous table data
                if (tableLayout!=null)
                    tableLayout.removeAllViews();

                //Go start asyncthread
                getData process = new getData();

                //The following try/catch is to force the thread to finish
                try {
                    Object result = process.execute().get();
                    longString = process.data;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initViews();

            }
        });

    }


    public void initViews()
    {
        //Initialize using space designated on app xml file
        tableLayout = (TableLayout) findViewById(R.id.chart_layout);

        //Go transform the data from JSON to an array list
        fetchedDataToArray();

        //For testing purposes
        //createTestData();

        //Once array list is populated, create table
        addRows();
    }

    public void fetchedDataToArray()
    {

        //Make sure the asynctask didn't fail
        if (longString == null)
        {
            Log.e(TAG, "I hate you all");
            createTestData();
            return;
        }
        //Print to error log what the result was
        //Log.e(TAG, longString);

        expenseObjs = new ArrayList<>();

        String longCopy = longString;

        //Formatting string for use and init of variables used
        longCopy = longCopy.replaceAll("\"", "");
        longCopy = longCopy.substring(1,longCopy.length()-2);
        //Log.e(TAG, longCopy);
        int nextComma, indexOfDesc, indexOfAmount, indexOfMonth, indexOfYear, indexOfDay;
        int nextCurly = longCopy.indexOf("}");
        int lastCurly = 0;


        while(true)
        {
            //Find next curly, make sure we haven't gone too far
            nextCurly = longCopy.indexOf("}", lastCurly+1);
            if (nextCurly < 0)
            {
                nextCurly = longCopy.length()-1;
            }
            expenseDataObject test = new expenseDataObject();

            //Find description after last end curly
            indexOfDesc = longCopy.indexOf("description", lastCurly);
            nextComma = longCopy.indexOf(",", indexOfDesc);
            test.setDescription(longCopy.substring(indexOfDesc+12,nextComma));

            //Find amount after last end curly
            indexOfAmount = longCopy.indexOf("amount", lastCurly);
            nextComma = longCopy.indexOf(",", indexOfAmount);
            test.setAmount(longCopy.substring(indexOfAmount+7,nextComma));

            //Find month after last end curly
            indexOfMonth = longCopy.indexOf("month", lastCurly);
            nextComma = longCopy.indexOf(",", indexOfMonth);
            //Log.e(TAG, longCopy.substring(indexOfMonth+6,nextComma));
            if (nextComma<nextCurly)
                test.setMonth( longCopy.substring(indexOfMonth+6,nextComma) );
            else
                test.setMonth(longCopy.substring(indexOfMonth+6,nextCurly));

            //Find day after last end curly
            indexOfDay = longCopy.indexOf("day", lastCurly);
            nextComma = longCopy.indexOf(",", indexOfDay);
            if (indexOfDay>nextCurly || indexOfDay<0)
            {
                //Day was not included in this entry
                test.setDay("");
            }
            else
            {
                test.setDay((nextComma < nextCurly) ? longCopy.substring(indexOfDay + 4, nextComma) :
                        longCopy.substring(indexOfDay + 4, nextCurly));
            }

            //Find year after last end curly
            indexOfYear = longCopy.indexOf("year", lastCurly);
            nextComma = longCopy.indexOf(",", indexOfYear);
            test.setYear((nextComma<nextCurly) ? longCopy.substring(indexOfYear+5,nextComma) :
                    longCopy.substring(indexOfYear+5,nextCurly));

            //Add to arraylist
            expenseObjs.add(test);

            //See if we've gone too far
            if (longCopy.indexOf("}", lastCurly+1) < 0)
            {
                break;
            }

            //Log.e(TAG, longCopy.substring(indexOfYear+5,nextComma));

            //Get the next end curly
            lastCurly = longCopy.indexOf("}", lastCurly+1);

        }

    }

    public void createTestData()
    {
        expenseObjs = new ArrayList<>();

        for (int i = 1; i < 50; i ++)
        {

            expenseDataObject test = new expenseDataObject();
            test.setDescription("He was number " + Integer.toString(i));
            test.setAmount(Integer.toString(i));
            test.setYear("1999");
            test.setMonth("May");
            test.setDay(Integer.toString(i));
            expenseObjs.add(test);

        }

    }
    public void addRows()
    {
        //Create header first
        TableRow header = new TableRow(MainActivity.this);
        header.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        //Add headers using formatting function
        header.addView(getRowsTextView(0, "Date", Color.BLACK, Typeface.BOLD, R.drawable.cell_shape ));
        header.addView(getRowsTextView(0, "Description", Color.BLACK, Typeface.BOLD, R.drawable.cell_shape ));
        header.addView(getRowsTextView(0, "Amount", Color.BLACK, Typeface.BOLD, R.drawable.cell_shape ));

        //Add the header row to the table
        tableLayout.addView(header, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < expenseObjs.size(); i ++)
        {
            //Begin row setup
            TableRow row = new TableRow(MainActivity.this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            //Grab current objects month/day/year and format to string
            String day = expenseObjs.get(i).getDay();
            String month = expenseObjs.get(i).getMonth();
            String year = expenseObjs.get(i).getYear();
            String date;
            if (day.equals(""))
            {
                date = month + " " + year;
            }
            else
            {
                date = month + " " + day + ", " + year;
            }

            //Add the data to the row
            row.addView(getRowsTextView(0, date, Color.BLACK, Typeface.NORMAL, R.drawable.cell_shape ));
            row.addView(getRowsTextView(0, expenseObjs.get(i).getDescription(), Color.BLACK, Typeface.NORMAL, R.drawable.cell_shape ));
            row.addView(getRowsTextView(0, expenseObjs.get(i).getAmount(), Color.BLACK, Typeface.NORMAL, R.drawable.cell_shape ));

            //Add to table
            tableLayout.addView(row, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private TextView getRowsTextView(int id, String title, int color, int typeface,int bgColor)
    {
        //Formatting for row cells
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title);
        tv.setTextColor(color);
        tv.setPadding(10, 10, 10, 10);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundResource(bgColor);
        tv.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        return tv;
    }
}
