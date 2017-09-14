package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by usuario on 08/11/2014.
 */
public class ListAgenteActivity extends Activity {


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_base, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//        switch (id) {
//            case R.id.action_profile:
////                intent = new Intent(this,MainActivity.class);
////                finish();
////                startActivity(intent);
////                break;
//                Toast.makeText(this, "Activiyy Profile", Toast.LENGTH_LONG).show();
//            case R.id.action_change_password:
////                intent = new Intent(this,PrimerActivity.class);
////                finish();
////                startActivity(intent);
//                Toast.makeText(this, "Activiyy Password", Toast.LENGTH_LONG).show();
//
//                break;
//
//
//            case R.id.action_exit:
//                //Creando una instacia a la clase alerta dialogo
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("Esta seguro que decea sali de la Aplicación?")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // MyActivity.this.finish(); //Close  this Activity for example: MyActivity.java
//                                System.exit(0);
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // some code if you want
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    TextView nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_agentes);
        //nombre = (TextView) findViewById(R.id.tvNombre);
        //Bundle bolsa = getIntent().getExtras();
        //nombre.setText(bolsa.getString("NOMBRE"));

        //LLenando el ListView
        ListView lv = (ListView) findViewById(R.id.listView);

        ArrayList<ItemCompra> itemsCompra = obtenerItems();

        ItemCompraAdapter adapter = new ItemCompraAdapter(this, itemsCompra);

        lv.setAdapter(adapter);


    }
    private ArrayList<ItemCompra> obtenerItems(){
        ArrayList<ItemCompra> items = new ArrayList<ItemCompra>();
        items.add(new ItemCompra(1, "Agente Pepito", "Lorem ipsum Lorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(2, "Agente Juan", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(3, "Agente Lorena", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(1, "Agente Pepito", "Lorem ipsum Lorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(2, "Agente Juan", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(3, "Agente Lorena", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(1, "Agente Pepito", "Lorem ipsum Lorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(2, "Agente Juan", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(3, "Agente Lorena", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(1, "Agente Pepito", "Lorem ipsum Lorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(2, "Agente Juan", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        items.add(new ItemCompra(3, "Agente Lorena", "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum", "drawable/agente"));
        return items;
    }

}
