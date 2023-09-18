package com.example.gps_test.ui.ActivityBuscarEventosRecycler;

public class ModelEvento {

private String nombreEvento,Ruta,descripcion,fechaEncuentro,horaEncuentro,cupoMinimo,cupoMaximo,categoria;
private String imagenEvento;

private boolean activarDesactivar;

private String userId;
private String userName;

private String publicoPrivado;
private  String activadoDescativado;

private String idEvento;

private String tokenFCM;
private int rating;

    public ModelEvento() {
    }

    public ModelEvento(String nombreEvento, String ruta, String descripcion,
                       String fechaEncuentro, String horaEncuentro, String cupoMinimo,
                       String cupoMaximo, String categoria, String imagenEvento,
                       boolean activarDesactivar, String userId, String userName,
                       String publicoPrivado, String activadoDescativado, String idEvento,
                       String tokenFCM, int rating) {
        this.nombreEvento = nombreEvento;
        Ruta = ruta;
        this.descripcion = descripcion;
        this.fechaEncuentro = fechaEncuentro;
        this.horaEncuentro = horaEncuentro;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.categoria = categoria;
        this.imagenEvento = imagenEvento;
        this.activarDesactivar = activarDesactivar;
        this.userId = userId;
        this.userName = userName;
        this.publicoPrivado = publicoPrivado;
        this.activadoDescativado = activadoDescativado;
        this.idEvento = idEvento;
        this.tokenFCM = tokenFCM;
        this.rating = rating;
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

    public int getRating() {
        return rating;
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

    public void setActivarDesactivar(boolean activarDesactivar) {
        this.activarDesactivar = activarDesactivar;
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

    public void setRating(int rating) {
        this.rating = rating;
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

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }
}


