package cl.ucn.disc.dam.twinpic.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
//import com.raizlabs.android.dbflow.structure.BaseModel;

import cl.ucn.disc.dam.twinpic.database.AppDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Andrés on 20-10-2016.
 */

@Table(database = AppDatabase.class)
public class Picture extends BaseModel{

    @Getter
    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Getter
    @Setter
    @Column
     String idDevice;

    @Getter
    @Setter
    @Column
     String date;

    @Getter
    @Setter
    @Column
     double latitud;

    @Getter
    @Setter
    @Column
     double longitud;

    @Getter
    @Setter
    @Column
     int positives;

    @Getter
    @Setter
    @Column
     int negatives;

    @Getter
    @Setter
    @Column
     int warnings;

    @Getter
    @Setter
    @Column
    String file;



    public Picture(){
        this.warnings = 0;
        this.negatives = 0;
        this.positives = 0;
    }



}
