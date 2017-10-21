package com.dataservicios.ttauditotrosagentes.interbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.ttauditotrosagentes.R;
import com.dataservicios.ttauditotrosagentes.SQLite.DatabaseHelper;
import com.dataservicios.ttauditotrosagentes.app.AppController;
import com.dataservicios.ttauditotrosagentes.model.Audit;
import com.dataservicios.ttauditotrosagentes.model.Encuesta;
import com.dataservicios.ttauditotrosagentes.model.PollDetail;
import com.dataservicios.ttauditotrosagentes.util.AuditUtil;
import com.dataservicios.ttauditotrosagentes.util.GPSTracker;
import com.dataservicios.ttauditotrosagentes.util.GlobalConstant;
import com.dataservicios.ttauditotrosagentes.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by usuario on 07/04/2015.
 */
public class EvaluacionTransaccionOnce  extends Activity {
    private static final String LOG_TAG = EvaluacionTransaccionOnce.class.getSimpleName();
    private ProgressDialog pDialog;
    private int idCompany, idPDV, idRuta, idAuditoria,idUser ,poll_id,store_id ,audit_id,road_id, user_id  ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, name_user;
    private  int result;
    private String opciones;
    Activity MyActivity = (Activity) this;
    TextView pregunta ;
    Button guardar;
    RadioGroup rgTipo;
    RadioButton rbA,rbB,rbC,rbD ,rbE, rbF;
    RadioButton rbSi,rbNo;
    EditText comentario;

    // Database Helper
    private DatabaseHelper db;

    private PollDetail pollDetail;
    private Audit mAudit;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inter_evaluacion_transaccion_once);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Evaluación de Transacción");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

        db = new DatabaseHelper(getApplicationContext());

        gpsTracker = new GPSTracker(MyActivity);

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        rbA=(RadioButton) findViewById(R.id.rbA);
        rbB=(RadioButton) findViewById(R.id.rbB);
        rbC=(RadioButton) findViewById(R.id.rbC);
        rbD=(RadioButton) findViewById(R.id.rbD);
        comentario=(EditText) findViewById(R.id.etComentario);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(MyActivity);
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        idUser = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        params = new JSONObject();
        //Recogiendo paramentro del anterior Activity
        //Bundle bundle = savedInstanceState.getArguments();
        Bundle bundle = getIntent().getExtras();
        idCompany=bundle.getInt("company_id");
        idPDV= bundle.getInt("idPDV");
        idRuta= bundle.getInt("idRuta");
        idAuditoria= bundle.getInt("idAuditoria");

        store_id = bundle.getInt("idPDV");
        audit_id = bundle.getInt("idAuditoria");
        road_id = bundle.getInt("idRuta");
        poll_id = GlobalConstant.poll_id[20];

//        try {
//            params.put("idPDV", idPDV);
//            //params.put("idRuta", idRuta);
//            params.put("idAuditoria", idAuditoria);
//            params.put("idCompany", idCompany);
//            params.put("idUser", idUser);
//
//            //params.put("id_pdv",idPDV);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        // db.deleteAllEncuesta();
        //cargarPreguntasEncuesta(params);
        leerEncuesta();


        //
        rgTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                if (rbD.getId()==checkedId){
                    // comentario.setEnabled(true);
                    // comentario.setFocusable(true);
                    comentario.setVisibility(View.VISIBLE);
                } else {
                    //comentario.setEnabled(false);
                    comentario.setVisibility(View.INVISIBLE);
                    comentario.setText("");
                }
                //textViewChoice.setText("You Selected "+rb.getText());
                //Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();

            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                long id = rgTipo.getCheckedRadioButtonId();
                if (id == -1){
                    //no item selected
                    //valor ="";
                    Toast toast;
                    toast = Toast.makeText(MyActivity,"Debe seleccionar una opción" , Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                else{
                    if (id == rbA.getId()){
                        //Do something with the button
                        opciones = "a";
                    } else if(id == rbB.getId()){
                        opciones = "b";
                    }else if(id == rbC.getId()){
                        opciones = "c";
                    }else if(id == rbD.getId()){
                        opciones = "d";
                    }

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        pollDetail = new PollDetail();
                        pollDetail.setPoll_id(poll_id);
                        pollDetail.setStore_id(store_id);
                        pollDetail.setSino(0);
                        pollDetail.setOptions(1);
                        pollDetail.setLimits(0);
                        pollDetail.setMedia(0);
                        pollDetail.setComment(0);
                        pollDetail.setResult(0);
                        pollDetail.setLimite("");
                        pollDetail.setComentario("");
                        pollDetail.setAuditor(user_id);
                        pollDetail.setProduct_id(0);
                        pollDetail.setCategory_product_id(0);
                        pollDetail.setPublicity_id(0);
                        pollDetail.setCompany_id(GlobalConstant.company_id);
                        pollDetail.setCommentOptions(1);
                        pollDetail.setSelectdOptions("");
                        pollDetail.setSelectedOtionsComment(comentario.getText().toString());
                        pollDetail.setPriority("0");


//                        JSONObject paramsData;
//                        paramsData = new JSONObject();
//                        try {
//                            paramsData.put("poll_id", pregunta.getTag());
//                            paramsData.put("user_id", String.valueOf(idUser));
//                            paramsData.put("store_id", idPDV);
//                            paramsData.put("idAuditoria", idAuditoria);
//                            paramsData.put("idCompany", idCompany);
//                            paramsData.put("idRuta", idRuta);
//                            paramsData.put("sino", "0");
//                            paramsData.put("options", "1");
//                            paramsData.put("limits", "0");
//                            paramsData.put("media", "0");
//                            paramsData.put("coment", "0");
//                            paramsData.put("coment_options", "1");
//                            paramsData.put("result", result);
//                            paramsData.put("status", "1");
//                            paramsData.put("opcion", pregunta.getTag().toString()+opciones.toString());
//                            paramsData.put("comentario", "");
//                            paramsData.put("comentario_options", comentario.getText());
//                            //params.put("id_pdv",idPDV);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        insertaEncuesta(paramsData);

                        new loadPoll().execute();
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

    }

    private void leerEncuesta() {

        if(db.getEncuestaCount()>0) {
            //Encuesta encuesta = db.getEncuesta(546);
            Encuesta encuesta = db.getEncuesta(GlobalConstant.poll_id[20]);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
            //}
        }

    }


    private void insertaEncuesta(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonInsertAuditPolls" ,paramsData,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(LOG_TAG, response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            //idCompany =response.getInt("company");
                            if (success == 1) {

                                Toast toast;
                                toast = Toast.makeText(MyActivity, "Se guardo correctamente los datos", Toast.LENGTH_LONG);
                                toast.show();
                                // onBackPressed();
//                                Bundle argRuta = new Bundle();
//                                argRuta.clear();
//                                argRuta.putInt("company_id",idCompany);
//                                argRuta.putInt("idPDV",idPDV);
//                                argRuta.putInt("idRuta", idRuta );
//                                argRuta.putInt("idAuditoria",idAuditoria);
//
//                                Intent intent;
//                                intent = new Intent(MyActivity,EvaluacionTransaccionOnce.class);
//                                intent.putExtras(argRuta);
//                                startActivity(intent);
                                finish();


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

    class loadPoll extends AsyncTask<Void , Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if(!AuditUtil.insertPollDetail(pollDetail)) return false;

            String time_close = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
            mAudit = new Audit();
            mAudit.setCompany_id(GlobalConstant.company_id);
            mAudit.setStore_id(store_id);
            mAudit.setId(audit_id);
            mAudit.setRoute_id(road_id);
            mAudit.setUser_id(user_id);
            mAudit.setLatitude_close(String.valueOf(gpsTracker.getLatitude()));
            mAudit.setLongitude_close(String.valueOf(gpsTracker.getLongitude()));
            mAudit.setLatitude_open(String.valueOf(GlobalConstant.latitude_open));
            mAudit.setLongitude_open(String.valueOf(GlobalConstant.longitude_open));
            mAudit.setTime_open(GlobalConstant.inicio);
            mAudit.setTime_close(time_close);

            if(!AuditUtil.closeAuditRoadStore(mAudit)) return false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                finish();
            } else {
                Toast.makeText(MyActivity , R.string.saveError, Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//
//        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }
}
