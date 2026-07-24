package Modelo;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author USER
 */
public class Gasto {
    private String concepto;
    private String categoria;
    private double monto;
    private String fecha;
    private String tipoPago;

    public Gasto(String concepto, String categoria, double monto, String fecha, String tipoPago) {
        this.concepto = concepto;
        this.categoria = categoria;
        this.monto = monto;
        this.fecha = fecha;
        this.tipoPago = tipoPago;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipoPago() {
        return tipoPago;
    }
}
