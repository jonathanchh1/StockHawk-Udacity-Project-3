package com.sam_chordas.android.stockhawk.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final int STOCKS_LOADER = 1;
    @Bind(R.id.symbol_label)
    TextView msymbol;
    @Bind(R.id.change_label)
    TextView mchange;
    @Bind(R.id.bid_label)
    TextView mbidPrice;
    @Bind(R.id.date_label)
    TextView mDate;
    private String mTitlesymbol;
    private LineChartView lineChartView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        ButterKnife.bind(this);

        Bundle arguments = new Bundle();
        Intent intent = getIntent();
        mTitlesymbol = intent.getStringExtra(getResources().getString(R.string.string_symbol));
        arguments.putString(getResources().getString(R.string.string_symbol), intent.getStringExtra(getResources().getString(R.string.string_symbol)));
        setTitle(mTitlesymbol);
         lineChartView = (LineChartView) findViewById(R.id.linechart);
        mDate.setText(getTime());
        getSupportLoaderManager().initLoader(STOCKS_LOADER, null, this);
    }

    private String getTime(){
        long time = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);
        String dateString = df.format(time);

        return dateString;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STOCKS_LOADER:
                return new android.support.v4.content.CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.SYMBOL + " = ?",
                        new String[]{mTitlesymbol},
                        null);
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == STOCKS_LOADER && data != null && data.getCount() != 0 && data.moveToFirst()) {

            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            msymbol.setText(symbol);


            String BidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
             mbidPrice.setText(BidPrice);

            String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
            String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            String RateChange = change + "(" + percentChange + ")";
            mchange.setText(RateChange);


            renderChart(data);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void renderChart(Cursor data) {
        LineSet lineSet = new LineSet();
        float minPrice = Float.MAX_VALUE;
        float maxPrice = Float.MIN_VALUE;

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            String label = data.getString(data.getColumnIndexOrThrow(QuoteColumns.BIDPRICE));
            float price = Float.parseFloat(label);

            lineSet.addPoint(label, price);
            minPrice = Math.min(minPrice, price);
            maxPrice = Math.max(maxPrice, price);
        }

        lineSet.setColor(Color.BLUE)
                .setFill(getColor(R.color.md_material_blue_600))
                .setDotsColor(Color.WHITE)
                .setDotsRadius(10)
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});



        lineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.WHITE)
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(Math.round(Math.max(0f, minPrice - 5f)), Math.round(maxPrice + 5f))
                .addData(lineSet);

        Animation aniXLine = new Animation();

        if (lineSet.size() > 1 && lineSet != null) {
            lineChartView.show(aniXLine);
        } else {
            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }
}
