package cl.ucn.disc.dam.twinpic.logic;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cl.ucn.disc.dam.twinpic.R;
import cl.ucn.disc.dam.twinpic.activities.MainMenuActivity;
import cl.ucn.disc.dam.twinpic.activities.TwinsInfo;
import cl.ucn.disc.dam.twinpic.model.Picture;
import cl.ucn.disc.dam.twinpic.model.Picture_Table;
import cl.ucn.disc.dam.twinpic.model.Twins;
import cl.ucn.disc.dam.twinpic.model.Twins_Table;
import cl.ucn.disc.dam.twinpic.utilities.DeviceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Adaptador extends BaseAdapter {

    private Activity activity;
    private ArrayList<Picture> pictures;

    public Adaptador(Activity activity, ArrayList<Picture> pictures){
        this.activity = activity;
        this.pictures = pictures;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    // Retorna la imagen gemela
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


    public void displayTwinsInfo(Picture pic){
        Intent intent = new Intent(this.activity, TwinsInfo.class);
        intent
                .putExtra("file",pic.getFile())
                .putExtra("latitud",String.valueOf(pic.getLatitud()))
                .putExtra("longitud",String.valueOf(pic.getLongitud()))
                .putExtra("fecha",String.valueOf(pic.getDate()))
                .putExtra("positives",String.valueOf(pic.getPositives()))
                .putExtra("negatives",String.valueOf(pic.getNegatives()))
                .putExtra("warnings",String.valueOf(pic.getWarnings()));
        activity.startActivity(intent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(convertView == null){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.picture_pair,null);
        }
        final Picture pic = pictures.get(position);

        final Picture twin = getTwin(pic);

        final ImageButton imgLeft = (ImageButton) v.findViewById(R.id.imageButton1);
        final ImageButton imgRight = (ImageButton) v.findViewById(R.id.imageButton2);

        imgLeft.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                    /*
                    log.debug("PRIMER LOG:     " + imgLeft.getTransitionName());
                    final Pic imagenSegunId = SQLite.select().from(Pic.class).where(Pic_Table.id.is(Long.valueOf(imageButtonEnviada.getTransitionName()))).querySingle();
                    log.debug("Latitud:     " + imagenSegunId.getLatitude());
                    log.debug("Longitud:    " + imagenSegunId.getLongitude());
                    */
                displayTwinsInfo(pic);
            }
        });

        imgRight.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                    /*
                    log.debug("PRIMER LOG:     " + imgLeft.getTransitionName());
                    final Pic imagenSegunId = SQLite.select().from(Pic.class).where(Pic_Table.id.is(Long.valueOf(imageButtonEnviada.getTransitionName()))).querySingle();
                    log.debug("Latitud:     " + imagenSegunId.getLatitude());
                    log.debug("Longitud:    " + imagenSegunId.getLongitude());
                    */
                displayTwinsInfo(twin);
            }
        });


        Uri uri = Uri.fromFile(new File(pic.getFile()));
        Picasso.with(this.activity)
                .load(uri)
                .resize(450,450)
                .centerCrop()
                .noPlaceholder()
                .into(imgLeft);
        //pic.getTwinId()
        /*
        Condition.In in = Condition.column(Picture_Table.twinId.getNameAlias()).in()
        List<Picture> pics = SQLite.select()
                .from(Picture.class)
                .where(in);
              */
        Uri uri2 = Uri.fromFile(new File(twin.getFile()));
        Picasso.with(this.activity)
                .load(uri2)
                .resize(450,450)
                .centerCrop()
                .noPlaceholder()
                .into(imgRight);
        return v;

    }
}