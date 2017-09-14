package com.dataservicios.ttauditotrosagentes.interbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.dataservicios.ttauditotrosagentes.model.Encuesta;
import com.dataservicios.ttauditotrosagentes.util.GlobalConstant;
import com.dataservicios.ttauditotrosagentes.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by usuario on 30/03/2015.
 */
public class introduccion extends Activity {
    private static final String LOG_TAG = introduccion.class.getSimpleName();
    private ProgressDialog pDialog;
    private int idCompany, idPDV, idRuta, idAuditoria,idUser ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, name_user;
    private  int result;

    private String opciones;

    Activity MyActivity = (Activity) this;
    TextView pregunta ;
    Button guardar;
    RadioGroup rgTipo;
    RadioButton rbA,rbB,rbC,rbD, rbE, rbF;
    EditText comentario;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inter_introduccion);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Introducción");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

        db = new DatabaseHelper(getApplicationContext());

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        rbA=(RadioButton) findViewById(R.id.rbA);
        rbB=(RadioButton) findViewById(R.id.rbB);
        rbC=(RadioButton) findViewById(R.id.rbC);
        rbD=(RadioButton) findViewById(R.id.rbD);
        rbE=(RadioButton) findViewById(R.id.rbE);
        rbF=(RadioButton) findViewById(R.id.rbF);

        comentario = (EditText) findViewById(R.id.etComentario) ;

        enabledControl(false);
//        pregunta.setText("Hoa");
//        pregunta.setTag("145");

//        Toast toast;
//        toast = Toast.makeText(MyActivity, pregunta.getText() + ": " +pregunta.getTag() , Toast.LENGTH_LONG);
//        toast.show();

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
        params = new JSONObject();
        //Recogiendo paramentro del anterior Activity
        //Bundle bundle = savedInstanceState.getArguments();
        Bundle bundle = getIntent().getExtras();
        idCompany=bundle.getInt("company_id");
        idPDV= bundle.getInt("idPDV");
        idRuta= bundle.getInt("idRuta");
        idAuditoria= bundle.getInt("idAuditoria");


        try {
            params.put("idPDV", idPDV);
            //params.put("idRuta", idRuta);
            params.put("idAuditoria", idAuditoria);
            params.put("idCompany", idCompany);
            params.put("idUser", idUser);

            //params.put("id_pdv",idPDV);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        db.deleteAllEncuesta();
        leerPreguntasEncuesta(params);

        rgTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbF:
                        enabledControl(true);
                        break;

                    default:
                        enabledControl(false);
                        break;
                }
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
                        opciones = "a";
                    } else if(id == rbB.getId()){
                        opciones = "b";
                    }else if(id == rbC.getId()){
                        opciones = "c";
                    }else if(id == rbD.getId()){
                        opciones = "d";
                    }else if(id == rbE.getId()){
                        opciones = "e";
                    }else if(id == rbF.getId()){
                        opciones = "f";
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
                        JSONObject paramsData;
                        paramsData = new JSONObject();
                        try {
                            paramsData.put("poll_id", pregunta.getTag());
                            paramsData.put("user_id", String.valueOf(idUser));
                            paramsData.put("store_id", idPDV);
                            paramsData.put("idAuditoria", idAuditoria);
                            paramsData.put("idCompany", idCompany);
                            paramsData.put("idRuta", idRuta);
                            paramsData.put("sino", "0");
                            paramsData.put("options", "1");
                            paramsData.put("limits", "0");
                            paramsData.put("media", "0");
                            paramsData.put("coment", "0");
                            paramsData.put("coment_options", "1");
                           // paramsData.put("result", result);
                            paramsData.put("status", "0");
                            paramsData.put("opcion",  pregunta.getTag().toString()+opciones.toString());
                            paramsData.put("comentario", "");
                            paramsData.put("comentario_options", comentario.getText());
                            //params.put("id_pdv",idPDV);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        insertaEncuesta(paramsData);
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
                                Bundle argRuta = new Bundle();
                                argRuta.clear();
                                argRuta.putInt("company_id",idCompany);
                                argRuta.putInt("idPDV",idPDV);
                                argRuta.putInt("idRuta", idRuta );
                                argRuta.putInt("idAuditoria",idAuditoria);
                                Intent intent;
                                intent = new Intent(MyActivity,introduccionDos.class);
                                intent.putExtras(argRuta);
                                startActivity(intent);
                                finish();

                                //onBackPressed();
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


    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Bundle argPDV = new Bundle();
//        argPDV.putInt("idPDV", idPDV );
//        argPDV.putInt("idRuta", idRuta );
//        argPDV.putString("fechaRuta",fechaRuta);
//        Intent a = new Intent(this,DetallePdv.class);
//        a.putExtras(argPDV);
//
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }
    //Lee las preguntas del servidor para mostralo en la inteface
    private void leerPreguntasEncuesta(JSONObject params){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonGetQuestions" ,params,
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
                            //idCompany =response.getInt("company");
                            if (success == 1) {
                                JSONArray agentesObjJson;
                                agentesObjJson = response.getJSONArray("questions");
                                // looping through All Products
                                for (int i = 0; i < agentesObjJson.length(); i++) {
                                    JSONObject obj = agentesObjJson.getJSONObject(i);
                                    // Storing each json item in variable
                                    String idPregunta = obj.getString("id");
                                    String question = obj.getString("question");
                                   // int status = obj.getInt("state");
                                    Encuesta encuesta = new Encuesta();
                                    encuesta.setId(Integer.valueOf(obj.getString("id")));
                                    encuesta.setQuestion(obj.getString("question"));
                                    db.createEncuesta(encuesta);

//                                    if (idPregunta.equals("26")  ){
//                                        pregunta.setText(question);
//                                        pregunta.setTag(idPregunta);
//                                    }


                                }
                            }
                            leerEncuesta();
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

    private void leerEncuesta() {
        if(db.getEncuestaCount()>0) {
            //Encuesta encuesta = db.getEncuesta(527);
            Encuesta encuesta = db.getEncuesta(GlobalConstant.poll_id[1]);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
            //}
        }
    }

    private void enabledControl(boolean state){
        if (state) {
            comentario.setText("");;
            comentario.setVisibility(View.VISIBLE);
            comentario.setEnabled(true);
        } else {
            comentario.setText("");;
            comentario.setVisibility(View.INVISIBLE);
            comentario.setEnabled(false);
        }

    }

}


