package com.dataservicios.ttauditotrosagentes;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

/**
 * Created by usuario on 30/01/2015.
 */
public class PresenciaProducto extends Activity {

    private Switch sw;
    private ViewGroup linearLayout;
    Activity MyActivity = (Activity) this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.presencia_producto);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Presencia de Producto");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
        linearLayout = (ViewGroup) findViewById(R.id.lyControles);

        for (int i = 0; i < 4; i++) {
            sw = new Switch(MyActivity);
            LinearLayout ly = new LinearLayout(MyActivity);
            ly.setOrientation(LinearLayout.VERTICAL);
            ly.setId(i+'_');
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT
            );
            params.setMargins(0, 10, 0, 10);
            ly.setLayoutParams(params);
            //sw.setBackgroundColor(getResources().getColor(R.color.color_base));
            //sw.setTextColor(getResources().getColor(R.color.color_fondo));
            sw.setText("fffff");

            //bt.setBackground();
            sw.setId(Integer.valueOf(i));
            ly.addView(sw);
            linearLayout.addView(ly);

        }

//        for (int i = 0; i < 4; i++){
//            sw.getId(i);
//
//        }
    }
}
