package com.example.sesionconfirebase;

import java.util.ArrayList;
import java.util.List;

public class ModelEvento {

private String nombreEvento,Ruta,descripcion,fechaEncuentro,
        horaEncuentro,cupoMinimo,cupoMaximo,categoria,fechaFinalizacion,horaFinalizacion;
private String imagenEvento;

private boolean activarDesactivar;

private String userId;
private String userName;

private String publicoPrivado;
private  String activadoDescativado;

private String idEvento;

private String tokenFCM;
private ArrayList<ModelCalificacion> calificaciones;

private float calificacionGeneral;



    public ModelEvento() {
    }


    public ModelEvento(String nombreEvento, String ruta, String descripcion,
                       String fechaEncuentro, String horaEncuentro, String cupoMinimo,
                       String cupoMaximo, String categoria, String fechaFinalizacion,
                       String horaFinalizacion, String imagenEvento,
                       boolean activarDesactivar, String userId, String userName,
                       String publicoPrivado, String activadoDescativado, String idEvento,
                       String tokenFCM) {
        this.nombreEvento = nombreEvento;
        Ruta = ruta;
        this.descripcion = descripcion;
        this.fechaEncuentro = fechaEncuentro;
        this.horaEncuentro = horaEncuentro;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.categoria = categoria;
        this.fechaFinalizacion = fechaFinalizacion;
        this.horaFinalizacion = horaFinalizacion;
        this.imagenEvento = imagenEvento;
        this.activarDesactivar = activarDesactivar;
        this.userId = userId;
        this.userName = userName;
        this.publicoPrivado = publicoPrivado;
        this.activadoDescativado = activadoDescativado;
        this.idEvento = idEvento;
        this.tokenFCM = tokenFCM;
        this.calificaciones = null;
        calificacionGeneral = 0.0f;
    }

    public ModelEvento(String nombreEvento, String ruta, String descripcion,
                       String fechaEncuentro, String horaEncuentro, String cupoMinimo,
                       String cupoMaximo, String categoria, String fechaFinalizacion,
                       String horaFinalizacion, String imagenEvento,
                       boolean activarDesactivar, String userId, String userName,
                       String publicoPrivado, String activadoDescativado, String idEvento,
                       String tokenFCM, ArrayList<ModelCalificacion>calificaciones,Float calificacionGeneral) {
        this.nombreEvento = nombreEvento;
        Ruta = ruta;
        this.descripcion = descripcion;
        this.fechaEncuentro = fechaEncuentro;
        this.horaEncuentro = horaEncuentro;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.categoria = categoria;
        this.fechaFinalizacion = fechaFinalizacion;
        this.horaFinalizacion = horaFinalizacion;
        this.imagenEvento = imagenEvento;
        this.activarDesactivar = activarDesactivar;
        this.userId = userId;
        this.userName = userName;
        this.publicoPrivado = publicoPrivado;
        this.activadoDescativado = activadoDescativado;
        this.idEvento = idEvento;
        this.tokenFCM = tokenFCM;
        this.calificaciones = calificaciones;
        this.calificacionGeneral=calificacionGeneral;
    }



    public void agregarCalificacion(ModelCalificacion calificacion) {
        if (calificaciones == null) {
            calificaciones = new ArrayList<>();
        }
        calificaciones.add(calificacion);
    }


    // Método para calcular la calificación promedio
    public float calcularYRetornarCalificacionPromedio() {
        if (calificaciones == null || calificaciones.isEmpty()) {
            return 0.0f;
        }

        float sumaCalificaciones = 0.0f;
        for (ModelCalificacion calificacion : calificaciones) {
            sumaCalificaciones += calificacion.getRating();
        }

        return (float) sumaCalificaciones / calificaciones.size();
    }

    // Método para calcular la calificación promedio y setearla en calificacionGeneral
    public void calcularYSetearCalificacionPromedio() {
        if (calificaciones == null || calificaciones.isEmpty()) {
            calificacionGeneral = 0.0f; // Si no hay calificaciones, se asigna 0.0f
            return;
        }

        float sumaCalificaciones = 0.0f;
        for (ModelCalificacion calificacion : calificaciones) {
            sumaCalificaciones += calificacion.getRating();
        }

        calificacionGeneral = (float) sumaCalificaciones / calificaciones.size();
    }


    // Método para calcular la calificación promedio, setearla en calificacionGeneral y luego retornarla
    public float calcularSetearYRetornarCalificacionPromedio() {
        if (calificaciones == null || calificaciones.isEmpty()) {
            calificacionGeneral = 0.0f; // Si no hay calificaciones, se asigna 0.0f
            return calificacionGeneral; // Se retorna el valor calculado
        }

        float sumaCalificaciones = 0.0f;
        for (ModelCalificacion calificacion : calificaciones) {
            sumaCalificaciones += calificacion.getRating();
        }

        calificacionGeneral = (float) sumaCalificaciones / calificaciones.size(); // Se asigna la calificación general
        return calificacionGeneral; // Se retorna el valor calculado
    }

    //********Getters


    public float getCalificacionGeneral() {
        return calificacionGeneral;
    }

    public String getTokenFCM() {
        return tokenFCM;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getPublicoPrivado() {
        return publicoPrivado;
    }

    public String getActivadoDescativado() {
        return activadoDescativado;
    }



    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }



    public boolean isActivarDesactivar() {
        return activarDesactivar;
    }



    public String getNombreEvento() {
        return nombreEvento;
    }

    public String getRuta() {
        return Ruta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaEncuentro() {
        return fechaEncuentro;
    }

    public String getHoraEncuentro() {
        return horaEncuentro;
    }

    public String getCupoMinimo() {
        return cupoMinimo;
    }

    public String getCupoMaximo() {
        return cupoMaximo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagenEvento() {
        return imagenEvento;
    }

    public String getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public String getHoraFinalizacion() {
        return horaFinalizacion;
    }

    public ArrayList<ModelCalificacion> getCalificaciones() {
        return calificaciones;
    }

    //******Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public void setRuta(String ruta) {
        Ruta = ruta;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaEncuentro(String fechaEncuentro) {
        this.fechaEncuentro = fechaEncuentro;
    }

    public void setHoraEncuentro(String horaEncuentro) {
        this.horaEncuentro = horaEncuentro;
    }

    public void setCupoMinimo(String cupoMinimo) {
        this.cupoMinimo = cupoMinimo;
    }

    public void setCupoMaximo(String cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setImagenEvento(String imagenEvento) {
        this.imagenEvento = imagenEvento;
    }

    public void setPublicoPrivado(String publicoPrivado) {
        this.publicoPrivado = publicoPrivado;
    }

    public void setActivadoDescativado(String activadoDescativado) {
        this.activadoDescativado = activadoDescativado;
    }

    public void setIdEvento(String idEvento) {

        this.idEvento = idEvento;
    }

    public void setActivarDesactivar(boolean activarDesactivar) {
        this.activarDesactivar = activarDesactivar;
    }

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }

    public void setFechaFinalizacion(String fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public void setHoraFinalizacion(String horaFinalizacion) {
        this.horaFinalizacion = horaFinalizacion;
    }

    public void setCalificaciones(ArrayList<ModelCalificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public void setCalificacionGeneral(float calificacionGeneral) {
        this.calificacionGeneral = calificacionGeneral;
    }
}


