package cl.ucn.disc.dam.twinpic.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import cl.ucn.disc.dam.twinpic.R;
import cl.ucn.disc.dam.twinpic.logic.Adaptador;

public class TwinsInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twins_info2);
        ImageView detailedPicture = (ImageView) findViewById(R.id.detailedPicture);
        EditText tDate = (EditText) findViewById(R.id.tFecha);
        EditText tLongitud = (EditText) findViewById(R.id.tLongitud);
        EditText tLatitud = (EditText) findViewById(R.id.tLatitud);
        EditText tPositivos = (EditText) findViewById(R.id.tPositivos);
        EditText tNegativos = (EditText) findViewById(R.id.tNegativos);
        EditText tWarnings = (EditText) findViewById(R.id.tWarnings);
        Button bPositive = (Button) findViewById(R.id.bPositive);
        Button bNegative = (Button) findViewById(R.id.bNegative);
        Button bWarning = (Button) findViewById(R.id.bWarning);

        Intent intent = getIntent();
        String picFile = intent.getStringExtra("file");
        /*
        .putExtra("file",pic.getFile())
                .putExtra("latitud",String.valueOf(pic.getLatitud()))
                .putExtra("longitud",String.valueOf(pic.getLongitud()))
                .putExtra("fecha",String.valueOf(pic.getDate()))
                .putExtra("positives",String.valueOf(pic.getPositives()))
                .putExtra("negatives",String.valueOf(pic.getNegatives()))
                .putExtra("warnings",String.valueOf(pic.getWarnings()));
         */

        tDate.setText(intent.getStringExtra("fecha"));
        tLongitud.setText(intent.getStringExtra("longitud"));
        tLatitud.setText(intent.getStringExtra("latitud"));
        tPositivos.setText(intent.getStringExtra("positives"));
        tNegativos.setText(intent.getStringExtra("negatives"));
        tWarnings.setText(intent.getStringExtra("warnings"));

        //double latitud = intent.getDoubleExtra("latitud");


        Uri uri = Uri.fromFile(new File(picFile));
        Picasso.with(this)
                .load(uri)
                .resize(650,650)
                .centerCrop()
                .into(detailedPicture);
    }
}
