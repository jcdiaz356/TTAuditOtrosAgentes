package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.ttauditotrosagentes.app.AppController;
import com.dataservicios.ttauditotrosagentes.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by usuario on 30/01/2015.
 */
public class Encuesta extends Activity {
    private Activity MyActivity ;
    private Switch[] sw;
    private ViewGroup linearLayout;
    private ProgressDialog pDialog;
    private Button btGuardar;
    private JSONObject params ,paramsEncuesta;
    private SessionManager session;
    private String email_user, id_user, name_user;
    private int idCompany, idPDV, idRuta, idAuditoria ;
    private String fechaRuta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.encuesta);

        MyActivity = (Activity) this;
        linearLayout = (ViewGroup) findViewById(R.id.lyControles);
        btGuardar = (Button) findViewById(R.id.btGuardar);



        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Encuesta");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);



        //Inicaliza clase sesió
        session = new SessionManager(MyActivity);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        id_user = user.get(SessionManager.KEY_ID_USER);



        Bundle bundle = getIntent().getExtras();
        idCompany= bundle.getInt("company_id");
        idPDV = bundle.getInt("idPDV");
        idRuta = bundle.getInt("idRuta");
        fechaRuta = bundle.getString("fechaRuta");
        idAuditoria  = bundle.getInt("idAuditoria");



        params = new JSONObject();
        try {
            params.put("id", idCompany);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Progres Dialogo
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);







        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , "http://ttaudit.com/JsonPollsCompany" ,params,
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
                                agentesObjJson = response.getJSONArray("questionsPoll");
                                // looping through All Products
                                int total =agentesObjJson.length();
                                //sw = initializeButtons(total);
                                sw = new Switch[total];
                                for (int i = 0; i < agentesObjJson.length(); i++) {
                                    JSONObject obj = agentesObjJson.getJSONObject(i);
                                    // Storing each json item in variable
                                    String idEncuesta = obj.getString("id");
                                    String encuesta = obj.getString("question");

                                    sw[i] = new Switch(MyActivity);
                                    LinearLayout ly = new LinearLayout(MyActivity);
                                    ly.setOrientation(LinearLayout.VERTICAL);
                                    ly.setId(i+'_');
                                    LinearLayout.LayoutParams paramsLy = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.FILL_PARENT,
                                            LinearLayout.LayoutParams.FILL_PARENT
                                    );
                                    paramsLy.setMargins(0, 10, 0, 10);
                                    ly.setLayoutParams(paramsLy);
                                    //sw.setBackgroundColor(getResources().getColor(R.color.color_base));
                                    //sw.setTextColor(getResources().getColor(R.color.color_fondo));
                                    sw[i].setText(encuesta);
                                    //bt.setBackground();
                                    sw[i].setId(Integer.valueOf(idEncuesta));

                                    ly.addView(sw[i]);
                                    linearLayout.addView(ly);

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






        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int swTotal=sw.length;

                        String arr = "";
                        paramsEncuesta=new JSONObject();
                        for(int i=0;i<swTotal;i++)
                        {
                               int valueChecke;
                               if(sw[i].isChecked())
                                   valueChecke=1;
                               else
                                   valueChecke=0;
                            if ( i==(swTotal-1)) {
                                arr   = arr +  sw[i].getId()+":" +  valueChecke   ;
                            } else
                            {
                                arr   = arr +  sw[i].getId()+":" +  valueChecke + "|" ;
                            }

                        }

                        try {
                            paramsEncuesta.put("question" ,arr);
                            paramsEncuesta.put("id" ,idPDV);
                            paramsEncuesta.put("idCompany" ,idCompany);
                            paramsEncuesta.put("idRuta" ,idRuta);
                            paramsEncuesta.put("idAuditoria" ,idAuditoria);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //Log.i(paramsEncuesta);
//                        for (int i = 0; i < 4; i++) {
//                            Log.i("sw",String.valueOf( " Total: " + String.valueOf(sw.length)  +  " Contenido: " + sw[i].getText() + "id: " +
//                                    sw[i].getId()  + " Checked: " +  sw[i].isChecked()));
//                        }

                        showpDialog();
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , "http://ttaudit.com/JsonUpdatePollsCompany" ,paramsEncuesta,
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

                                                Toast toast;
                                                toast = Toast.makeText(MyActivity, "Se guardo correctamente los datos", Toast.LENGTH_LONG);
                                                toast.show();
                                                onBackPressed();

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


}
