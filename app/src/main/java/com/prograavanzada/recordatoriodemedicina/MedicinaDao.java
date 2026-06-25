package com.prograavanzada.recordatoriodemedicina;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MedicinaDao {

    @Insert
    void insertar(Medicina medicina);

    @Update
    void actualizar(Medicina medicina);

    @Delete
    void eliminar(Medicina medicina);

    @Query("SELECT * FROM medicinas ORDER BY horaAlarma ASC")
    List<Medicina> obtenerTodas();
}
