package Modelo;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author USER
 */
public class Ingreso {
    private String concepto;
    private double monto;
    private String fecha;
    private String tipoIngreso;

    public Ingreso(String concepto, double monto, String fecha, String tipoIngreso) {
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = fecha;
        this.tipoIngreso = tipoIngreso;
    }
    public String getConcepto() {
        return concepto;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipoIngreso() {
        return tipoIngreso;
    }
   public String mostrarDatos(){
        return "Concepto: "+ concepto +
                "\nMonto: "+ monto +
                "\nFecha: "+ fecha +
                "\nTipo Ingreso: " + tipoIngreso;
    }
}
