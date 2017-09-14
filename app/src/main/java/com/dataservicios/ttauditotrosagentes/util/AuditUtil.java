package com.dataservicios.ttauditotrosagentes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.dataservicios.ttauditotrosagentes.model.Audit;
import com.dataservicios.ttauditotrosagentes.model.Media;
import com.dataservicios.ttauditotrosagentes.model.Pdv;
import com.dataservicios.ttauditotrosagentes.model.PhoneDetail;
import com.dataservicios.ttauditotrosagentes.model.PollDetail;
import com.dataservicios.ttauditotrosagentes.model.User;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Jaime on 28/08/2016.
 */
public class AuditUtil {
    public static final String LOG_TAG = AuditUtil.class.getSimpleName();
    //private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private Context context;

    public AuditUtil(Context context) {
        this.context = context ;
    }

    /**
     * uploadMedia send media
     * @param media Objeto media
     * @param typeSend Type send: 0 = imagen para preguntas, 1 = imagen para publicitis,  2 = imagen para soad
     * @return
     */
    public boolean uploadMedia(Media media, int typeSend){


        final String url_upload_image = GlobalConstant.dominio + "/insertImagesAlicorp";

        String imag = media.getFile();
        int id = media.getId();
        int company_id = media.getCompany_id();
        int poll_id = media.getPoll_id();
        int product_id = media.getProduct_id();
        int publicity_id = media.getPublicity_id();
        int store_id= media.getStore_id();
        int type = media.getType();
        String hora_sistema = media.getCreated_at();
        String created_at = media.getCreated_at();
        long totalSize = 0;

        File file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + imag);


        if(!file.exists()){
            return true;
        }

        Bitmap bbicon;
        HttpResponse resp;
        HttpClient httpClient = new DefaultHttpClient();
        Log.i("FOO", "Notification started");
        bbicon= BitmapFactory.decodeFile(String.valueOf(file));
        Bitmap scaledBitmap;

        if(typeSend > 0){


        }
        if(Build.MODEL.equals("MotoG3")){
            scaledBitmap = BitmapLoader.rotateImage(BitmapLoader.scaleDown(bbicon, 390 , true),0);
        } else {
            scaledBitmap = BitmapLoader.rotateImage(BitmapLoader.scaleDown(bbicon, 390 , true),90);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

        InputStream in = new ByteArrayInputStream(bos.toByteArray());

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



        httppost.setEntity(mpEntity);
        try {

            totalSize =  mpEntity.getContentLength();
            mpEntity.addPart("fotoUp", foto);
            mpEntity.addPart("archivo", new StringBody(String.valueOf(file.getName())));
            mpEntity.addPart("store_id", new StringBody(String.valueOf(store_id)));
            mpEntity.addPart("product_id", new StringBody(String.valueOf(product_id)));
            mpEntity.addPart("poll_id", new StringBody(String.valueOf(poll_id)));
            mpEntity.addPart("publicities_id", new StringBody(String.valueOf(publicity_id)));
            mpEntity.addPart("company_id", new StringBody(String.valueOf(company_id)));
            mpEntity.addPart("tipo", new StringBody(String.valueOf(type)));
            mpEntity.addPart("horaSistema", new StringBody(String.valueOf(hora_sistema)));

            Log.i("FOO", "About to call httpClient.execute");
            resp = httpClient.execute(httppost);
            // resp.getEntity().getContent();
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                Log.i(LOG_TAG, "Screw up with http - " + resp.getStatusLine().getStatusCode());
                file.delete();

            } else {

                Log.i(LOG_TAG, "Screw up with http - " + resp.getStatusLine().getStatusCode());
                return  false ;
            }
            //resp.getEntity().consumeContent();
            //return true;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  false ;
        }
        return true;
    }


    public static boolean updateDataStore(Pdv pdv) {
        int success;
        try {
            HashMap<String, String> paramsData = new HashMap<>();
            paramsData.put("address", pdv.getDireccion());
//            paramsData.put("telephone", pdv.getTelephone());
//            paramsData.put("cell", pdv.getCell());
//            paramsData.put("urbanization", pdv.getReference());
            paramsData.put("store_id", String.valueOf(pdv.getId()));
            Log.d("request", "starting");
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json", "POST", paramsData);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updateStore", "POST", paramsData);


            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean closeAuditRoadStore(Audit audit) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("audit_id"       , String.valueOf(audit.getId()));
            params.put("store_id"       , String.valueOf(audit.getStore_id()));
            params.put("company_id"     , String.valueOf(audit.getCompany_id()));
            params.put("rout_id"        , String.valueOf(audit.getRoute_id()));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/closeAudit" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public static boolean closeAuditRoadAll(Audit audit) {


        int success;
        try {


            HashMap<String, String> paramsData = new HashMap<>();

            paramsData.put("latitud_close", audit.getLatitude_close());
            paramsData.put("longitud_close", audit.getLongitude_close());
            paramsData.put("latitud_open", audit.getLatitude_open());
            paramsData.put("longitud_open",  audit.getLongitude_open());
            paramsData.put("tiempo_inicio",  audit.getTime_open());
            paramsData.put("tiempo_fin",  audit.getTime_close());
            paramsData.put("tduser", String.valueOf(audit.getUser_id()));
            paramsData.put("id", String.valueOf(audit.getStore_id()));
            paramsData.put("idruta", String.valueOf(audit.getRoute_id()));
            paramsData.put("company_id", String.valueOf(audit.getCompany_id()));
            Log.d("request", "starting");
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json", "POST", paramsData);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/insertaTiempo", "POST", paramsData);
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public static ArrayList<Pdv> getLisStores(int rout_id, int company_id){
        int success ;

        ArrayList<Pdv> listaPdv = new ArrayList<Pdv>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id", String.valueOf(rout_id));
            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("roadsDetail");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {

                            JSONObject obj = ObjJson.getJSONObject(i);
                            Pdv pdv = new Pdv();
                            pdv.setId(Integer.valueOf(obj.getString("id")));
                            pdv.setPdv(obj.getString("fullname"));
                            //pdv.setThumbnailUrl(obj.getString("image"));
                            pdv.setDireccion(obj.getString("address"));
                            pdv.setDistrito(obj.getString("district"));
                            //pdv.setType(obj.getString("type"));
//                            pdv.setRegion(obj.getString("region"));
//                            pdv.setTypeBodega(obj.getString("tipo_bodega"));
                            pdv.setType(obj.getString("comment"));
//                            pdv.setCodClient(obj.getString("codclient"));
                            pdv.setStatus(Integer.valueOf(obj.getString("status")));
                            listaPdv.add(i,pdv);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  listaPdv;
    }


    public static boolean insertPollDetail(PollDetail pollDetail) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("poll_id"                , String.valueOf(pollDetail.getPoll_id()));
            params.put("store_id"               , String.valueOf(pollDetail.getStore_id()));
            params.put("sino"                   , String.valueOf(pollDetail.getSino()));
            params.put("options"                , String.valueOf(pollDetail.getOptions()));
            params.put("limits"                 , String.valueOf(pollDetail.getLimits()));
            params.put("media"                  , String.valueOf(pollDetail.getMedia()));
            params.put("coment"                 , String.valueOf(pollDetail.getComment()));
            params.put("result"                 , String.valueOf(pollDetail.getResult()));
            params.put("limite"                 , String.valueOf(pollDetail.getLimite()));
            params.put("comentario"             , String.valueOf(pollDetail.getComentario()));
            params.put("auditor"                , String.valueOf(pollDetail.getAuditor()));
            params.put("product_id"             , String.valueOf(pollDetail.getProduct_id()));
            params.put("publicity_id"           , String.valueOf(pollDetail.getPublicity_id()));
            params.put("category_product_id"    , String.valueOf(pollDetail.getCategory_product_id()));
            params.put("company_id"             , String.valueOf(pollDetail.getCompany_id()));
            params.put("commentOptions"         , String.valueOf(pollDetail.getCommentOptions()));
            params.put("selectedOptions"        , String.valueOf(pollDetail.getSelectdOptions()));
            params.put("selectedOptionsComment" , String.valueOf(pollDetail.getSelectedOtionsComment()));
            params.put("priority"               , String.valueOf(pollDetail.getPriority()));



            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/savePollDetails" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int duplicateStore(int company_id ,int store_id ) {
        int store_id_return = 0;
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("company_id"            , String.valueOf(company_id));
            params.put("store_id"              , String.valueOf(store_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/duplicateStore" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success == 1) {

                    // store_id = json.getInt("store_id");
                    //if (store_id != 0 ) {
                    store_id_return = json.getInt("store_id");
                    Log.d(LOG_TAG, "Tienda creada ok, ID");
                    // }

                }else{
                    Log.d(LOG_TAG, "No Hay datos");
                    //return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return store_id_return;
        }
        return store_id_return;
    }

    public static boolean savePhoneDetails(PhoneDetail phoneDetail) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("user_id"       , String.valueOf(phoneDetail.getUser_id()));
            params.put("latitud"       , String.valueOf(phoneDetail.getLatitude()));
            params.put("longitud"      , String.valueOf(phoneDetail.getLongitude()));
            params.put("phone"         , String.valueOf(phoneDetail.getPhone()));
            params.put("sdk"           , String.valueOf(phoneDetail.getSdk()));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/savePhoneDetails" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean updateContact(int store_id, String contact_new, String telephone_new, String contact, String telephone) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("store_id"       , String.valueOf(store_id));
            params.put("contact_new"       , String.valueOf(contact_new));
            params.put("telephone_new"      , String.valueOf(telephone_new));
            params.put("contact"         , String.valueOf(contact));
            params.put("telephone"           , String.valueOf(telephone));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/changeContactStore" ,"POST", params);
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean updateAddress(Integer store_id, Integer company_id, Integer user_id, String address, String userName, String storeName, String reference, String comment) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();


            params.put("store_id"       , String.valueOf(store_id));
            params.put("company_id"       , String.valueOf(company_id));
            params.put("user_id"       , String.valueOf(user_id));
            params.put("direccion"      , String.valueOf(address));
            params.put("userName"      , String.valueOf(userName));
            params.put("storeName"         , String.valueOf(storeName));
            params.put("referencia"           , String.valueOf(reference));
            params.put("comentario"           , String.valueOf(comment));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/changeAddressStore" ,"POST", params);
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static User userLogin(String userName, String password , String imei){

        int success ;
        User user = new User();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("username", String.valueOf(userName));
            params.put("password", String.valueOf(password));
            params.put("imei", String.valueOf(imei));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginMovil" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nulo");
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    user.setId(json.getInt("id"));
                    user.setEmail(userName);
                    user.setName(json.getString("fullname"));
                    user.setPassword(password);
                }else{
                    Log.d(LOG_TAG, "No se pudo iniciar sesión");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  user;
    }

    public static boolean saveLatLongStore(Integer store_id, double latitude, double longitude ) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id"       , String.valueOf(store_id));
            params.put("latitud"       , String.valueOf(latitude));
            params.put("longitud"      , String.valueOf(longitude));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updatePositionStore" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

}
