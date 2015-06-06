package com.ramsofttech.dummy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;

import com.ramsofttech.adpushlibrary.utility.GCMData;
import com.ramsofttech.adpushlibrary.view.AdView;
import com.ramsofttech.adpushlibrary.view.BannerView;
import com.ramsofttech.adpushlibrary.view.InterstialView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdView ad = ((AdView) findViewById(R.id.ad));
        ad.setmActivity(this.getSupportFragmentManager());

        ((BannerView) findViewById(R.id.bannerView)).setmActivity(this);
        AttributeSet attributeSet = null;
        InterstialView im = new InterstialView(this, attributeSet);
        im.setmActivity(this);


        GCMData data=new GCMData();
        data.updateData(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
