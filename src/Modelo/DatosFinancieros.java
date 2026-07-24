package Modelo;


import Modelo.Ingreso;
import Modelo.Gasto;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author USER
 */
public class DatosFinancieros {
    private static final ArrayList<Ingreso> ingresos = new ArrayList<>();
    private static final ArrayList<Gasto> gastos = new ArrayList<>();

    public static void agregarIngreso(Ingreso ingreso) {
        ingresos.add(ingreso);
    }

    public static void agregarGasto(Gasto gasto) {
        gastos.add(gasto);
    }

    public static List<Ingreso> obtenerIngresos() {
        return ingresos;
    }

    public static List<Gasto> obtenerGastos() {
        return gastos;
    }

    public static double totalIngresos() {
        double total = 0;
        for (Ingreso ingreso : ingresos) {
            total += ingreso.getMonto();
        }
        return total;
    }

    public static double totalGastos() {
        double total = 0;
        for (Gasto gasto : gastos) {
            total += gasto.getMonto();
        }
        return total;
    }

    public static double saldoActual() {
        return totalIngresos() - totalGastos();
    }
}
