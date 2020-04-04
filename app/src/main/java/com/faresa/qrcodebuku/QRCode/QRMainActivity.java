package com.faresa.qrcodebuku.QRCode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faresa.qrcodebuku.QRCode.adapter.Code;
import com.faresa.qrcodebuku.QRCode.adapter.ListItem;
import com.faresa.qrcodebuku.QRCode.adapter.MyAdapter;
import com.faresa.qrcodebuku.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class QRMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    IntentIntegrator scan;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ListItem> listItems;
    private AdView mAdView;
    int cam_height = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrmain);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DbHelper dbHelper = new DbHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean beep = sharedPref.getBoolean("beep", true);
        Boolean frontCamera = sharedPref.getBoolean("frontCamera", false);
        int camId;
        if (frontCamera == false)
            camId = 0;
        else
            camId = 1;
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        adapter = new MyAdapter(listItems, this);
        recyclerView.setAdapter(adapter);
        CardView cardView = (CardView) findViewById(R.id.cardView);

        Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
        try {
            // Iterate Bunnys
            QueryResultIterable<Code> itr = cupboard().withCursor(codes).iterate(Code.class);
            for (Code bunny : itr) {
                // do something with bunny
                ListItem listItem = new ListItem(bunny._id, bunny.name, bunny.type);
                listItems.add(listItem);

                adapter = new MyAdapter(listItems, this);
                recyclerView.setAdapter(adapter);
            }
        } finally {
            // close the cursor
            codes.close();
        }
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                ListItem item = listItems.get(position);

                cupboard().withDatabase(db).delete(Code.class, item.get_id());
                listItems.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, listItems.size());

            }
        }).attachToRecyclerView(recyclerView);
        scan = new IntentIntegrator(this);
        scan.setBeepEnabled(beep);
        scan.setCaptureActivity(Potrait.class);
        scan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        scan.setCameraId(camId);
        scan.setOrientationLocked(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.setOrientationLocked(false);
                scan.initiateScan();
            }
        });

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
        if (id == R.id.action_clearAll) {
            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
            try {
                if (codes.getCount() > 0) {
                    cupboard().withDatabase(db).delete(Code.class, null);
                    listItems.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    return true;
                }
            } finally {
                codes.close();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "Result Not Found", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else {

                Code codeObj = new Code(result.getContents(), result.getFormatName());
                DbHelper dbHelper = new DbHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = cupboard().withDatabase(db).put(codeObj);
                listItems.clear();
                adapter.notifyDataSetChanged();
                Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
                try {
                    // Iterate Bunnys
                    QueryResultIterable<Code> itr = cupboard().withCursor(codes).iterate(Code.class);
                    for (Code bunny : itr) {
                        // do something with bunny
                        ListItem listItem = new ListItem(bunny._id, bunny.name, bunny.type);
                        listItems.add(listItem);
                        adapter = new MyAdapter(listItems, this);
                        recyclerView.setAdapter(adapter);
                    }
                } finally {
                    // close the cursor
                    codes.close();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void cardClick(View card) {
        TextView textView = (TextView) findViewById(R.id.textViewCode);
        String code = textView.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, code);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("beep")) {
            scan.setBeepEnabled(sharedPreferences.getBoolean(key, true));
        }
        if (key.equals("frontCamera")) {
            int camId;
            if (sharedPreferences.getBoolean(key, false) == false)
                camId = 0;
            else
                camId = 1;
            scan.setCameraId(camId);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
