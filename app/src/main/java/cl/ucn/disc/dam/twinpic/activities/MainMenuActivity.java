package cl.ucn.disc.dam.twinpic.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import cl.ucn.disc.dam.twinpic.R;
import cl.ucn.disc.dam.twinpic.database.AppDatabase;
import cl.ucn.disc.dam.twinpic.logic.Adaptador;
import cl.ucn.disc.dam.twinpic.model.Picture;
import cl.ucn.disc.dam.twinpic.model.Picture_Table;
import cl.ucn.disc.dam.twinpic.model.Twins;
import cl.ucn.disc.dam.twinpic.utilities.DeviceUtils;
import cl.ucn.disc.dam.twinpic.utilities.GPSTracker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainMenuActivity extends AppCompatActivity {

    // Variables globales
    private ImageView img;
    private String deviceID = "";
    private boolean justTookPic = false;
    private boolean gotResponse = true;
    private Picture ultimaPicTomada = null;
    private String server = "http://192.168.1.100";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        deviceID = DeviceUtils.getDeviceId(this);

        // Inicializacion de button asociado al layout
        Button button = (Button) findViewById(R.id.bTomarFoto);

        // Creación de carpetas que contendrán las imágenes
        File imagesFolderTwinPic = new File(Environment.getExternalStorageDirectory(), "TwinPic");
        imagesFolderTwinPic.mkdirs();
        File imagesFolderTwin = new File(Environment.getExternalStorageDirectory(), "TwinPic/Twin");
        imagesFolderTwin.mkdirs();
        File imagesFolderPic = new File(Environment.getExternalStorageDirectory(), "TwinPic/Pic");
        imagesFolderPic.mkdirs();


        // Destroy db

        //super.getApplicationContext().deleteDatabase(AppDatabase.NAME + ".db");

        actualizarListView();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //File file = makeFile();


                DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_ss");
                String fecha = df.format(Calendar.getInstance().getTime());
                String path = Environment.getExternalStorageDirectory() + "/TwinPic/Pic/" + fecha + ".png";
                File file = new File(path);

                double latitude = 0,
                        longitude = 0;
                GPSTracker gps = new GPSTracker(MainMenuActivity.this);
                if(gps.canGetLocation()){
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }

                Picture pic = new Picture();
                pic.setDate(fecha);
                pic.setFile(path);
                pic.setIdDevice(deviceID);
                pic.setLatitud(latitude);
                pic.setLongitud(longitude);
                pic.save();

                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));


                startActivityForResult(camera_intent, 1);
                ultimaPicTomada = pic;
            }
        });

    }

    /*
        Obtiene la lista de archivos de una path especifica
     */
    private File[] getListaArchivos(String path) {
        File f = new File(path);
        File[] file = f.listFiles();
        return file;
    }

    private Picture getTwin(Picture pic){
        List<Twins> twins = SQLite.select().from(Twins.class).queryList();
        Picture twin = new Picture();
        for(int i = 0; i<twins.size();i++){
            if(twins.get(i).getId1() == pic.getId()){
                long twinId = twins.get(i).getId2();
                twin = SQLite.select().from(Picture.class).where(Picture_Table.id.is(twinId)).querySingle();
                break;
            }
        }

        return twin;
    }

    private void printPics(){
        List<Picture> pics = SQLite.select().from(Picture.class).queryList();
        for (int i = 0; i<pics.size();i++) {
            Log.d("pics", String.valueOf(i) + " Pic ID: "+String.valueOf(pics.get(i).getId())
            + " FilePath: " + String.valueOf(pics.get(i).getFile()));
        }
    }
    private void printTwins(){
        List<Twins> twins = SQLite.select().from(Twins.class).queryList();
        for (int i = 0; i<twins.size();i++) {
            Log.d("twins", String.valueOf(i) + " ID1: " + String.valueOf(twins.get(i).getId1())+ " ID2: "+ String.valueOf(twins.get(i).getId2()));
        }
    }

    private void insertTwins(long id1, long id2){
        Twins pair = new Twins();
        pair.setIdDevice(deviceID);
        pair.setId1(id1);
        pair.setId2(id2);
        pair.save();
    }

    /*
        Envia al servidor un objeto JSON, el cual contiene la pic entregada por parametro
        y su gemela
     */
    private void postPic(Picture pic, String twinID){

        String URL = server+"/json/postpic";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Codificando imagen para subirla
        Bitmap bm = BitmapFactory.decodeFile(pic.getFile());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b,Base64.DEFAULT);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("file", encodedImage);
            jsonBody.put("deviceId", deviceID);
            jsonBody.put("date",pic.getDate());
            jsonBody.put("latitude",pic.getLatitud());
            jsonBody.put("longitude",pic.getLongitud());
            jsonBody.put("id", twinID);
            /*
            jsonBody.put("warnings",pic.getWarnings());
            jsonBody.put("positives",pic.getPositives());
            jsonBody.put("negatives",pic.getNegatives());
*/

            final String mRequestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    actualizarListView();
                    pd.dismiss();
                    Log.i("VOLLEY1", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY2", error.toString());
                    actualizarListView();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        actualizarListView();
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
        Obtiene la pic con menor numero de entregas del servidor y la asocia a una pic local,
        generando un par TwinPic
     */
    private void setTwins(final Picture pic){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = server+"/json/getPic";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonArray = null;
                        try {
                            Picture twin = new Picture(); //Pic recibida por el servidor
                            JSONObject picture = response;

                            // Obteniendo la pic con menos entregas
                            String idDev = picture.getString("idDevice");
                            String encodedImage = picture.getString("img");
                            String latitude = picture.getString("latitude");
                            String longitide = picture.getString("longitude");
                            String positives = picture.getString("positives");
                            String negatives = picture.getString("negatives");
                            String warnings = picture.getString("warnings");
                            String idServidor = picture.getString("id");

                            // Guardando la pic con menos entregas en la tabla Picture local
                            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_ss");
                            String fecha = df.format(Calendar.getInstance().getTime());
                            String path = Environment.getExternalStorageDirectory() + "/TwinPic/Twin/" + fecha + ".png";

                            twin.setIdDevice(idDev);
                            twin.setFile(path);  // PATH
                            twin.setLatitud(Double.parseDouble(latitude));
                            twin.setLongitud(Double.parseDouble(longitide));
                            twin.setPositives(Integer.parseInt(positives));
                            twin.setNegatives(Integer.parseInt(negatives));
                            twin.setWarnings(Integer.parseInt(warnings));
                            twin.save();

                            // Decodificando y guardando la pic recibida
                            byte [] bytes = Base64.decode(encodedImage.toString(),0);
                            File file = new File(path);
                            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                            fileOutputStream.write(bytes);
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            // Guardando el par de fotos en la tabla Twins local

                            Twins pair = new Twins();
                            pair.setIdDevice(deviceID);
                            pair.setId1(pic.getId());
                            pair.setId2(twin.getId());
                            pair.save();
                            /*
                            printPics();
                            printTwins();
                            */

                            // Enviar la pic al servidor para hacer el par foraneo
                            postPic(pic,idServidor);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("qq", "error");
                    }
                }
        );
        requestQueue.add(jsonRequest);
        requestQueue.start();
    }
    /*
        Genera un archivo correspondiente a la foto tomada, y que tiene como nombre la fecha
        en que se tomo
     */

    private File makeFile() {
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_ss");
        String fecha = df.format(Calendar.getInstance().getTime());
        String path = Environment.getExternalStorageDirectory() + "/TwinPic/Pic/" + fecha + ".png";
        File image = new File(path);
        Picture pic = new Picture();
        pic.setDate(fecha);
        pic.setFile(path);
        pic.setIdDevice(deviceID);
        pic.setLatitud(5555);
        pic.setLongitud(4444);
        pic.save();
        return image;
    }

    /*
        Actualiza el listview con las imagenes guardadas
     */
    private void actualizarListView() {
        final ListView listView = (ListView) findViewById(R.id.listView1);
        //ArrayList<Twins> pairs = new ArrayList<Twins>();
        ArrayList<Picture> pictures = new ArrayList<Picture>();

        List<Picture> pics = SQLite.select().from(Picture.class).queryList();
        //List<Twins> twins = SQLite.select().from(Twins.class).queryList();
        for (int i = pics.size()-1;i >= 0; i--) {
            Picture pic = pics.get(i);
            if(pic.getIdDevice().equals(deviceID)) {
                pictures.add(pic);
            }
        }
        Adaptador adaptador = new Adaptador(this, pictures);
        listView.setAdapter(adaptador);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pd = new ProgressDialog(this);
        pd.setTitle("Descargando");
        pd.setMessage("Descargando imagen. Por favor, espere un momento...");
        pd.show();
        setTwins(ultimaPicTomada);
        //actualizarListView();
    }
}


