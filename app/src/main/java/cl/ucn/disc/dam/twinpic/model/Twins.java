package cl.ucn.disc.dam.twinpic.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import cl.ucn.disc.dam.twinpic.database.AppDatabase;
import lombok.Getter;
import lombok.Setter;

//import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Andrés on 20-10-2016.
 */

@Table(database = AppDatabase.class)
public class Twins extends BaseModel{

    @Getter
    @Setter
    @Column
    @PrimaryKey
    String idDevice;

    @Getter
    @Setter
    @Column
    @PrimaryKey
    long id1;

    @Getter
    @Setter
    @Column
    long id2;


    public Twins(){

    }



}
