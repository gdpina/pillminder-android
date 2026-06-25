package com.prograavanzada.recordatoriodemedicina;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicinas")
public class Medicina {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String cantidad;
    public int inventarioTotal;
    public String fechaInicio;
    public String horaAlarma;

    // --- ¡NUEVOS CAMPOS! ---
    public int frecuencia;      // Guardará cada cuántas horas suena (ej: 8)
    public String descripcion;  // Guardará la nota o breve explicación

    // Constructor vacío requerido por Room
    public Medicina() {}

    // Constructor actualizado con todos los datos
    public Medicina(String nombre, String cantidad, int inventarioTotal, String fechaInicio, String horaAlarma, int frecuencia, String descripcion) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.inventarioTotal = inventarioTotal;
        this.fechaInicio = fechaInicio;
        this.horaAlarma = horaAlarma;
        this.frecuencia = frecuencia;
        this.descripcion = descripcion;
    }
}