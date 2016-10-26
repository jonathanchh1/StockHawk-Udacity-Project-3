package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public StockIntentService() {
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }


  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")) {
      args.putString("symbol", intent.getStringExtra("symbol"));
    }

    stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));


    try{
      String intent1 = intent.getStringExtra("tag");
      stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }catch (Exception e){
      final Handler mHandler = new Handler(getMainLooper());

      mHandler.post(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(getApplicationContext(), getResources().getString(R.string.symbol_error), Toast.LENGTH_SHORT).show();
        }
      });
      e.printStackTrace();
    }
    //http://stackoverflow.com/questions/15136199/when-to-use-handler-post-when-to-new-thread
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    // stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
  }
}