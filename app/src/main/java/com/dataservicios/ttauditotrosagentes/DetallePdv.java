package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.ttauditotrosagentes.app.AppController;
import com.dataservicios.ttauditotrosagentes.interbank.EvaluacionTransaccion;
import com.dataservicios.ttauditotrosagentes.interbank.EvaluacionTrato;
import com.dataservicios.ttauditotrosagentes.interbank.UsoIterbankAgente;
import com.dataservicios.ttauditotrosagentes.interbank.informacion;
import com.dataservicios.ttauditotrosagentes.interbank.introduccion;
import com.dataservicios.ttauditotrosagentes.util.AuditUtil;
import com.dataservicios.ttauditotrosagentes.util.GPSTracker;
import com.dataservicios.ttauditotrosagentes.util.GlobalConstant;
import com.dataservicios.ttauditotrosagentes.util.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by usuario on 14/01/2015.
 */
public class DetallePdv extends FragmentActivity {
    private static final String URL_PDVS = "http://www.dataservicios.com/webservice/pdvs.php";
    private GoogleMap map;
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewGroup linearLayout;
    private Button bt;
    private LocationManager locManager;
    private LocationListener locListener;
    private double latitude ;
    private double longitude ;
    private double lat ;
    private double lon ;
    private Marker MarkerNow;
    private ProgressDialog pDialog;

    private JSONObject params,paramsCordenadas;
    private SessionManager session;
    private String email_user, id_user, name_user;
    private int idPDV, IdRuta, idCompany, store_id;
    private String fechaRuta;
    private EditText pdvs1,pdvsAuditados1,porcentajeAvance1;
    private TextView tvTienda,tvDireccion , tv_Distrito, tv_Ejecutivo ,  tvPDVSdelDía , tvLong, tvLat, tv_CodClient, tv_Referencia;
    private Button btGuardarLatLong, btCerrarAudit;
    private Activity myActivity = (Activity) this;
    private String typeStore ;

    private  GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_pdv);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Detalle de PDV");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
        session = new SessionManager(myActivity);
        map =((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        pdvs1 = (EditText)  findViewById(R.id.etPDVS);
        pdvsAuditados1 = (EditText)  findViewById(R.id.etPDVSAuditados);
        porcentajeAvance1 = (EditText)  findViewById(R.id.etPorcentajeAvance);
        tvTienda = (TextView)  findViewById(R.id.tvTienda);
        tvDireccion = (TextView)  findViewById(R.id.tvDireccion);
        tv_Distrito = (TextView)  findViewById(R.id.tvDistrito);
        tv_Ejecutivo = (TextView)  findViewById(R.id.tvEjecutivo);
        tvPDVSdelDía = (TextView)  findViewById(R.id.tvPDVSdelDía);
        tv_CodClient = (TextView)  findViewById(R.id.tvCodCliente);
        tv_Referencia = (TextView)  findViewById(R.id.tvReferencia);

        btGuardarLatLong = (Button) findViewById(R.id.btGuardarLatLong);
        btCerrarAudit = (Button) findViewById(R.id.btCerrarAuditoria);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        id_user = user.get(SessionManager.KEY_ID_USER);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        gpsTracker = new GPSTracker(myActivity);


        btGuardarLatLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btGuardarLatLong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(gpsTracker.canGetLocation()){
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                        }else{
                            gpsTracker.showSettingsAlert();
                            return;
                        }

                        new saveLatLongStore().execute();

                    }
                });
            }
        });

        btCerrarAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de cerrar la auditoría: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strDate = sdf.format(c.getTime());
                        GlobalConstant.fin = strDate;

                        JSONObject paramsCloseAudit = new JSONObject();
                        try {
                            paramsCloseAudit.put("latitud_close", lat);
                            paramsCloseAudit.put("longitud_close", lon);
                            paramsCloseAudit.put("latitud_open", GlobalConstant.latitude_open);
                            paramsCloseAudit.put("longitud_open",  GlobalConstant.latitude_open);
                            paramsCloseAudit.put("tiempo_inicio",  GlobalConstant.inicio);
                            paramsCloseAudit.put("tiempo_fin",  GlobalConstant.fin);
                            paramsCloseAudit.put("tduser", id_user);
                            paramsCloseAudit.put("id", idPDV);
                            paramsCloseAudit.put("idruta", IdRuta);
                            paramsCloseAudit.put("company_id", GlobalConstant.company_id);
                            insertaTiemporAuditoria(paramsCloseAudit);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);


            }
        });



        linearLayout = (ViewGroup) findViewById(R.id.lyControles);
//        Button MyBoton = (Button) findViewById(R.id.button1);
//        MyBoton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent("com.dataservicios.systemauditor.LISTGENTE");
//                startActivity(intent);
//            }
//        });
        //Añadiendo parametros para pasar al Json por metodo POST
        params = new JSONObject();
        //Recogiendo paramentro del anterior Activity
        //Bundle bundle = savedInstanceState.getArguments();
        Bundle bundle = getIntent().getExtras();
        idPDV= bundle.getInt("idPDV");
        store_id = bundle.getInt("idPDV");
        IdRuta= bundle.getInt("idRuta");
        fechaRuta= bundle.getString("fechaRuta");
        tvPDVSdelDía.setText(fechaRuta);


        try {
            params.put("id", idPDV);
            params.put("idRoute", IdRuta);
            //Enviando

            params.put("iduser", id_user);
            params.put("company_id", GlobalConstant.company_id);

            //params.put("id_pdv",idPDV);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cargaPdvs();
        //cargarAditoriasInterbank();
        cargarAditoriasInterbank();

    }



    class saveLatLongStore extends AsyncTask<Void , Integer , Boolean> {

        @Override
        protected void onPreExecute() {

            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if(!AuditUtil.saveLatLongStore(store_id,latitude,longitude)) return false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                Toast.makeText(myActivity , R.string.message_save_gps_success, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(myActivity , R.string.message_save_gps_error, Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
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

    private void cargaPdvs(){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonRoadDetail" ,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");


                            if (success == 1) {
//
                                JSONArray ObjJson;
                                ObjJson = response.getJSONArray("roadsDetail");
                                // looping through All Products
                                if(ObjJson.length() > 0) {
                                    for (int i = 0; i < ObjJson.length(); i++) {
                                        try {
                                            JSONObject obj = ObjJson.getJSONObject(i);
                                            tvTienda.setText(obj.getString("fullname"));
                                           // tvDireccion.setText(obj.getString("address"));
                                            tvDireccion.setText(obj.getString("address"));
                                            tv_Distrito.setText(obj.getString("district"));
                                            tv_Ejecutivo.setText(obj.getString("ejecutivo"));
                                            tv_CodClient.setText(obj.getString("codclient"));
                                            tv_Referencia.setText(obj.getString("urbanization"));
                                            typeStore = obj.getString("comment") ;

                                            latitude= Double.valueOf(obj.getString("latitude"))  ;
                                            longitude= Double.valueOf(obj.getString("longitude"));
                                            map.clear();
                                            map.setMyLocationEnabled(true);
                                            MarkerNow = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Mi Ubicación")
                                                    //.snippet("Population: 4,137,400")
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
                                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                            // Establezco un listener para ver cuando cambio de posicion
                                            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                public void onMyLocationChange(Location pos) {
                                                    // TODO Auto-generated method stub
                                                    // Extraigo la Lat y Lon del Listener
                                                    lat = pos.getLatitude();
                                                    lon = pos.getLongitude();
                                                    // Muevo la camara a mi posicion
                                                    //CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
                                                    //map.moveCamera(cam);
                                                    // Notifico con un mensaje al usuario de su Lat y Lon
                                                    //Toast.makeText(myActivity,"Lat: " + lat + "\nLon: " + lon, Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void cargarAditoriasInterbank(){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST ,  GlobalConstant.dominio + "/JsonAuditsForStore" ,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            idCompany =response.getInt("company");
                            if (success == 1) {
                                JSONArray agentesObjJson;
                                agentesObjJson = response.getJSONArray("audits");
                                // looping through All Products
                                for (int i = 0; i < agentesObjJson.length(); i++) {
                                    JSONObject obj = agentesObjJson.getJSONObject(i);
                                    // Storing each json item in variable
                                    String idAuditoria = obj.getString("id");
                                    String auditoria = obj.getString("fullname");
                                    int status = obj.getInt("state");
                                    bt = new Button(myActivity);
                                    LinearLayout ly = new LinearLayout(myActivity);
                                    ly.setOrientation(LinearLayout.VERTICAL);
                                    ly.setId(i+'_');


                                    LayoutParams params = new LayoutParams(
                                            LayoutParams.FILL_PARENT,
                                            LayoutParams.FILL_PARENT
                                    );


                                    params.setMargins(0, 10, 0, 10);
                                    ly.setLayoutParams(params);
                                    bt.setBackgroundColor(getResources().getColor(R.color.color_base));
                                    bt.setTextColor(getResources().getColor(R.color.color_fondo));
                                    bt.setText(auditoria);

                                    if(status==1) {
                                        Drawable img = myActivity.getResources().getDrawable( R.drawable.ic_check_on);
                                        img.setBounds( 0, 0, 60, 60 );  // set the image size
                                        bt.setCompoundDrawables( img, null, null, null );
                                        bt.setBackgroundColor(getResources().getColor(R.color.color_bottom_buttom_pressed));
                                        bt.setTextColor(getResources().getColor(R.color.color_base));
                                        bt.setEnabled(false);
                                    }  else {
                                        Drawable img = myActivity.getResources().getDrawable( R.drawable.ic_check_off);
                                        img.setBounds( 0, 0, 60, 60 );  // set the image size
                                        bt.setCompoundDrawables( img, null, null, null );
                                    }
                                    if(GlobalConstant.global_close_audit==1){

                                        bt.setBackgroundColor(getResources().getColor(R.color.color_bottom_buttom_pressed));
                                        bt.setTextColor(getResources().getColor(R.color.color_base));
                                        bt.setEnabled(false);
                                    }
                                    //bt.setBackground();
                                    bt.setId(Integer.valueOf(idAuditoria));
                                    bt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                                    bt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Toast.makeText(getActivity(), j  , Toast.LENGTH_LONG).show();
                                            Button button1 = (Button) v;
                                            String texto = button1.getText().toString();
                                            //Toast toast=Toast.makeText(getActivity(), selected, Toast.LENGTH_SHORT);
                                            Toast toast;
                                            toast = Toast.makeText(myActivity, texto + ":" +  button1.getId(), Toast.LENGTH_LONG);
                                            toast.show();
                                            //int idBoton = Integer.valueOf(idAuditoria);
                                            Intent intent;
                                            int idAuditoria = button1.getId();

                                            Bundle argRuta = new Bundle();
                                            argRuta.clear();
                                            argRuta.putInt("company_id",idCompany);
                                            argRuta.putInt("idPDV",idPDV);
                                            argRuta.putInt("idRuta", IdRuta );
                                            argRuta.putInt("store_id", idPDV );
                                            argRuta.putInt("road_id", IdRuta );
                                            argRuta.putString("fechaRuta",fechaRuta);
                                            argRuta.putInt("idAuditoria",idAuditoria);
                                            argRuta.putInt("audit_id",idAuditoria);

                                            switch (idAuditoria) {
                                                case 30:
//                                                    intent = new Intent("com.dataservicios.systemauditor.ENCUESTA");
//                                                    startActivity(intent);

                                                    //intent = new Intent("com.dataservicios.systemauditor.INTRODUCCION");
                                                    intent = new Intent(myActivity,introduccion.class);
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);

                                                    break;
                                                case 31:
//                                                    intent = new Intent("com.dataservicios.systemauditor.ENCUESTA");
//                                                    startActivity(intent);

                                                    //intent = new Intent("com.dataservicios.systemauditor.USOIBK");
                                                    intent = new Intent(myActivity,UsoIterbankAgente.class);
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);

                                                    break;
                                                case 32:
                                                    //intent = new Intent("com.dataservicios.systemauditor.EVTRANSACCION");
                                                    intent = new Intent(myActivity,EvaluacionTransaccion.class);
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);
                                                    break;
                                                case 33:
                                                    //intent = new Intent("com.dataservicios.systemauditor.EVTRATO");
                                                    intent = new Intent(myActivity,EvaluacionTrato.class);
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);
                                                    break;

                                                case 34:
                                                    //intent = new Intent("com.dataservicios.systemauditor.INFO");
                                                    intent = new Intent(myActivity,informacion.class);
                                                    intent.putExtras(argRuta);
                                                    startActivity(intent);
                                                    break;

                                            }
                                        }
                                    });
                                    ly.addView(bt);
                                    linearLayout.addView(ly);

//                                    if(!typeStore.equals("BIM") && idAuditoria.equals("52")){
//
//                                        bt.setVisibility(View.INVISIBLE);
//
//                                    }
                                }



                                GlobalConstant.global_close_audit=0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    private void insertaTiemporAuditoria(JSONObject parametros) {
        showpDialog();
        //JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/insertaTiempo" ,parametros,
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/insertaTiempoNew" ,parametros,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            if (success == 1) {
//
                                Log.d("DATAAAA", response.toString());
                                Toast.makeText(myActivity, "Se ", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(myActivity, "No se ha podido enviar la información, intentelo mas tarde ", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(myActivity, "No se ha podido enviar la información, intentelo mas tarde ", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );


        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
//        Intent a = new Intent(this,PanelAdmin.class);
//        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }



}
