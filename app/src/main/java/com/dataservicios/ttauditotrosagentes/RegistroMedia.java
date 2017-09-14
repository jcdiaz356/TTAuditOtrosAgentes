package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditotrosagentes.Repositories.MediaRepo;
import com.dataservicios.ttauditotrosagentes.SQLite.DatabaseHelper;
import com.dataservicios.ttauditotrosagentes.adapter.MediaAdapter;
import com.dataservicios.ttauditotrosagentes.model.Media;
import com.dataservicios.ttauditotrosagentes.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Jaime on 29/08/2016.
 */
public class RegistroMedia extends Activity {
    private Activity MyActivity = this ;
    private static final String LOG_TAG = RegistroMedia.class.getSimpleName();
    private SessionManager session;
    private ListView listView;
    private MediaAdapter adapter;
    private DatabaseHelper db;
    private MediaRepo mr ;
    private ProgressDialog pDialog;
    private List<Media> mediaList = new ArrayList<Media>();

    private String tipo,cadenaruc,fechaRuta;
    private Integer user_id ;

    private TextView tv_contador;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registro_media);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("Productos");

        tv_contador = (TextView) findViewById(R.id.tvContador);
        //bt_finalizar = (Button) findViewById(R.id.btFinalizar);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;


        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        db = new DatabaseHelper(getApplicationContext());
        mr = new MediaRepo(MyActivity);

        int total_products = mr.getAllMedias().size();
        tv_contador.setText(String.valueOf(total_products));

        listView = (ListView) findViewById(R.id.listProducts);
        mediaList =  mr.getAllMedias();

        adapter = new MediaAdapter(MyActivity,  mediaList);
        listView.setAdapter(adapter);
        Log.d(LOG_TAG, String.valueOf(mr.getAllMedias()));
        adapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String product_id = ((TextView) view.findViewById(R.id.tvId)).getText().toString();
                Toast toast = Toast.makeText(getApplicationContext(), product_id, Toast.LENGTH_SHORT);
                toast.show();

            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}

