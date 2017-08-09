package com.fjbatresv.serialnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.serial)
    TextView serial;

    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters = new IntentFilter[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            this.adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null){
            adapter.disableForegroundDispatch(this);
        }
    }

    @OnClick(R.id.fab)
    public void read(){
        serial.setText("Esperando etiqueta");
        setAdapterNfc();
    }

    private void setAdapterNfc() {
        adapter = NfcAdapter.getDefaultAdapter(this);
        if(adapter == null){
            Toast.makeText(this, "Tu dispositivo no soporta NFC", Toast.LENGTH_LONG).show();
            return;
        }
        if (!adapter.isEnabled()){
            Toast.makeText(this, "Por favor enciende el NFC de tu dispositivo", Toast.LENGTH_LONG).show();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("activity", "new Intent | " + intent.toString());
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)){
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            serial.setText(ByteArrayToHexString(tag.getId()));
        }else{
            Toast.makeText(this, "No es una EXTRA TAG", Toast.LENGTH_LONG).show();
        }
        this.adapter = null;
    }

    String ByteArrayToHexString(byte [] inarray)
    {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
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
