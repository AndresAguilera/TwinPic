package cl.ucn.disc.dam.twinpic.database;

import com.raizlabs.android.dbflow.annotation.Database;


@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    /**
     * Key de la base de datos
     */
    public static final String NAME = "AppDatabase";

    /**
     * Version de la BD
     */
    public static final int VERSION = 1;

    /**
     * Tamanio del cache
     */
    public static final int CACHE_SIZE = 100;
}
