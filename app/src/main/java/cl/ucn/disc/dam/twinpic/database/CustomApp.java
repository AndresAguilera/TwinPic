package cl.ucn.disc.dam.twinpic.database;

import android.app.Application;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.leakcanary.LeakCanary;

import java.util.List;

import cl.ucn.disc.dam.twinpic.model.Picture;

/**
 * Created by Andrés on 12-11-2016.
 */

public class CustomApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*

        // LeakCanary solo en aplicacion principal
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
*/
        // This instantiates DBFlow
         FlowManager.init(new FlowConfig.Builder(this).build());

        /*

        List<Picture> fotos = SQLite.select().from(Picture.class).queryList();
        String query = fotos.get(2).getIdDevice();
        Log.d("asd",String.valueOf(query) + " size: " + String.valueOf(fotos.size()));
        */
    }
}
