package com.prograavanzada.recordatoriodemedicina;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Medicina.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MedicinaDao medicinaDao();

}