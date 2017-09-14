package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dataservicios.ttauditotrosagentes.SQLite.DatabaseHelper;
import com.dataservicios.ttauditotrosagentes.util.AuditUtil;
import com.dataservicios.ttauditotrosagentes.util.SessionManager;

import java.util.HashMap;

public class ContactoTelefonoActivity extends Activity {

    private static final String LOG_TAG = ContactoTelefonoActivity.class.getSimpleName();

    private Integer store_id,   user_id ;
    private Button btGuardar, btCancelar;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String userEmail, storeName, userName, telephone, contact, telephone_new, contact_new;

    private EditText etTelephone, etContact;
    private ProgressDialog pDialog;
    private SessionManager session;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto_telefono);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        etTelephone = (EditText) findViewById(R.id.etTelephone);
        etContact = (EditText) findViewById(R.id.etContact);
        //etReferencia = (EditText) findViewById(R.id.etReferencia);
        btGuardar = (Button) findViewById(R.id.btGuardar);
        btCancelar = (Button) findViewById(R.id.btCancelar);


        db = new DatabaseHelper(getApplicationContext());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        userEmail = String.valueOf(user.get(SessionManager.KEY_EMAIL));
        userName = String.valueOf(user.get(SessionManager.KEY_NAME));


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        telephone = bundle.getString("telephone");
        contact = bundle.getString("contact");
        //poll_id = 501 ;


        etTelephone.setText(telephone);
        etContact.setText(contact);


        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                telephone_new=etTelephone.getText().toString();
                contact_new=etContact.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        telephone = etTelephone.getText().toString();



                       new loadPoll().execute();

                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);
            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle(R.string.message_save);
                builder.setMessage("Está seguro que desea salir sin guardar ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();

                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);
            }
        });



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

            if(!AuditUtil.updateContact(store_id,contact_new,telephone_new,contact,telephone)) return false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();


                finish();


            } else {
                Toast.makeText(MyActivity , R.string.message_no_save_data, Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            //Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar pongase en contácto con el administrador", Toast.LENGTH_LONG).show();
            //onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
