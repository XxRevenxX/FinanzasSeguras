package Modelo;

public class Usuario {
    private int id_usuario;
    private String correo, contraseña, nombre, telefono, fechaNacimiento;

    public Usuario(int id_usuario, String correo, String contraseña, String nombre, String telefono, String fechaNacimiento) {
        this.id_usuario = id_usuario;
        this.correo = correo;
        this.contraseña = contraseña;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters
    public int getId_usuario() { return id_usuario; }
    public String getCorreo() { return correo; }
    public String getContraseña() { return contraseña; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getFechaNacimiento() { return fechaNacimiento; }
}