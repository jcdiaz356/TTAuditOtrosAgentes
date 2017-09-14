package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by usuario on 30/01/2015.
 */
public class IngresarEspacios extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingresa_espacios);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Ingresar Espacios");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
    }
}
