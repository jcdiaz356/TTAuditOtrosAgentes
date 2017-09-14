package com.dataservicios.ttauditotrosagentes.Services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dataservicios.ttauditotrosagentes.AlbumStorageDirFactory;
import com.dataservicios.ttauditotrosagentes.BaseAlbumDirFactory;
import com.dataservicios.ttauditotrosagentes.FroyoAlbumDirFactory;
import com.dataservicios.ttauditotrosagentes.R;
import com.dataservicios.ttauditotrosagentes.util.AndroidMultiPartEntity;
import com.dataservicios.ttauditotrosagentes.util.GlobalConstant;
import com.dataservicios.ttauditotrosagentes.util.JSONParser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


//Subida de Archivos
public class UploadService extends IntentService {

    public static final int NOTIFICATION_ID=1;
    private int totalMessages = 0;

    private NotificationManager mNotificationManager;

    long totalSize = 0;

    Context context = this;
    public UploadService(String name) {
        super(name);
    }
    public UploadService(){
        super("UploadService");
    }
    ArrayList<String> names = new ArrayList<String>();
    private static final String url_upload_image = GlobalConstant.dominio + "/uploadImagesAuditBayer";
   // private static final String url_upload_image = GlobalConstant.dominio + "/uploadImagesAudit";
    private static final String url_insert_image = GlobalConstant.dominio + "/insertImagesPoll";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    String idPDV,idPoll,tipo;
    @Override
    protected void onHandleIntent(Intent intent) {
       // notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        //Uri uri  = intent.getData();
        names=intent.getStringArrayListExtra("names");
        idPDV=intent.getStringExtra("idPDV");
        idPoll=intent.getStringExtra("idPoll");
        tipo=intent.getStringExtra("tipo");
        //Log.i("FOO", uri.toString());
        //new ServerUpdate().execute(names);
        //new loadInsert().execute(names);
        new ServerUpdate().execute(names);

    }
    class ServerUpdate extends AsyncTask<ArrayList<String>,String,Boolean> {
        //ProgressDialog pDialog;
        @Override
        protected Boolean doInBackground(ArrayList<String>... arg0) {
            Uri uri;
            int lastPercent = 0;
            //uri=arg0[0];
            for (int i = 0; i < arg0[0].size(); i++) {
                String foto = arg0[0].get(i);

                if (uploadFoto(getAlbumDirTemp().getAbsolutePath() + "/" + foto) ) {
                    File file = new File(getAlbumDirTemp().getAbsolutePath() + "/" + foto);
                    file.delete();
                } else {
                    return false ;
                }
            }
           return true;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                Toast.makeText(context,"Se guardó correctamente la imágen en el servidor", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"Error no se pudo guardar la imágen, consulta su backup de imagenes", Toast.LENGTH_SHORT).show();
                updateNotification();
            }
        }
    }


    class loadInsert extends AsyncTask<ArrayList<String>, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(ArrayList<String>... arg0) {
            // TODO Auto-generated method stub
            //cargaTipoPedido();
            for (int i = 0; i < arg0[0].size(); i++) {
                String foto = arg0[0].get(i);
                onInsert(foto);
            }
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            new ServerUpdate().execute(names);
        }
    }

    //Metodo que escala la imágen
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private boolean uploadFoto(String imag){
        File file = new File(imag);
        Bitmap bbicon;
         HttpResponse resp;

        HttpClient httpClient = new DefaultHttpClient();

        Log.i("FOO", "Notification started");
        bbicon= BitmapFactory.decodeFile(String.valueOf(file));
        // Rotando imágen y escalando
        Bitmap scaledBitmap;
        if(Build.MODEL.equals("MotoG3")){
            scaledBitmap = rotateImage(scaleDown(bbicon, 450 , true),0);
        } else {
            scaledBitmap = rotateImage(scaleDown(bbicon, 450 , true),90);
        }
        //Bitmap scaledBitmap = rotateImage(scaleDown(bbicon, 450 , true),90);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        //bbicon.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());
        // InputStream in = new ByteArrayInputStream(bos.toByteArray());
        //If you are stuck with HTTPClient 4.0, use InputStreamBody instead:
        //ContentBody foto = new InputStreamBody(in, "image/jpeg", file.getName());
        //ContentBody foto = new FileBody(file, "image/jpeg");
        //Use a ByteArrayBody instead (available since HTTPClient 4.1), despite its name it takes a file name, too:
        ContentBody foto = new ByteArrayBody(bos.toByteArray(), file.getName());
        HttpClient httpclient = new DefaultHttpClient();

        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(url_upload_image);
        //MultipartEntity mpEntity = new MultipartEntity();
        AndroidMultiPartEntity mpEntity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
            @Override
            public void transferred(long num) {
                //notification.contentView.setProgressBar(R.id.progressBar1, 100,(int) ((num / (float) totalSize) * 100), true);
               // notificationManager.notify(1, notification);
            }
        });

        //totalSize =  mpEntity.getContentLength();
        //mpEntity.addPart("fotoUp", foto);
        httppost.setEntity(mpEntity);

        try {




            totalSize =  mpEntity.getContentLength();
            mpEntity.addPart("fotoUp", foto);
            mpEntity.addPart("archivo", new StringBody(String.valueOf(file.getName())));
            mpEntity.addPart("store_id", new StringBody(String.valueOf(idPDV)));
            mpEntity.addPart("product_id", new StringBody(String.valueOf("0")));
            mpEntity.addPart("poll_id", new StringBody(String.valueOf(idPoll)));
            mpEntity.addPart("company_id", new StringBody(String.valueOf(GlobalConstant.company_id)));
            mpEntity.addPart("tipo", new StringBody(String.valueOf(tipo)));

            Log.i("FOO", "About to call httpClient.execute");
            resp = httpClient.execute(httppost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.i("FOO", "All done");
            } else {
                Log.i("FOO", "Screw up with http - " + resp.getStatusLine().getStatusCode());
                return false;
            }
            resp.getEntity().consumeContent();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }



    private String getAlbunNameTemp(){
        return  getString(R.string.album_name_temp);
    }

    private File getAlbumDirTemp() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbunNameTemp());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d(getAlbunNameTemp(), "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
    private boolean onInsert(String imag_name)  {

        int success;
        try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("archivo",imag_name));
                nameValuePairs.add(new BasicNameValuePair("store_id",idPDV));
                nameValuePairs.add(new BasicNameValuePair("poll_id",idPoll));
                nameValuePairs.add(new BasicNameValuePair("tipo",tipo));
                nameValuePairs.add(new BasicNameValuePair("company_id", String.valueOf(GlobalConstant.company_id)));

                JSONParser jsonParser = new JSONParser();
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(url_insert_image,"POST", nameValuePairs);
                // check your log for json response
                Log.d("Login attempt", json.toString());
                // json success, tag que retorna el json
                success = json.getInt("success");
                if (success == 1) {
                    return  true;
                }else{
                    // Log.d(LOG_TAG, json.getString("message"));
                    // return json.getString("message");
                    return  false;
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        mBuilder.setContentTitle("IBK");
        mBuilder.setContentText("Fotos truncadas IBK.");
        mBuilder.setTicker("Notificación de fotos truncadas Alert!");
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.ic_inter_active);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        mBuilder.setNumber(++totalMessages);
        // Intent resultIntent = new Intent(this, NotificationClass.class);
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // stackBuilder.addParentStack(NotificationClass.class);
        // stackBuilder.addNextIntent(resultIntent);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        // mBuilder.setContentIntent(resultPendingIntent);

        String pathFile = Environment.getExternalStorageDirectory().toString()+"/Pictures/" + getAlbunNameTemp()  ;
        File filePath = new File(pathFile);

        if (filePath.isDirectory()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri myUri = Uri.parse(String.valueOf(filePath));
            intent.setDataAndType(myUri, "resource/folder");
            //startActivity(intent);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //stackBuilder.addParentStack(NotificationClass.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
