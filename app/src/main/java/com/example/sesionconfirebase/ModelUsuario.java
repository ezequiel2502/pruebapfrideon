package com.example.sesionconfirebase;

import java.util.ArrayList;
import java.util.List;

public class ModelUsuario {

    private String nombre, apellido, email, pass, userNameCustom, userId, tokenFcm, imagenPerfil, fechaNac, ciudad, pais, facebook, twitter, instagram;
    private List<String> privados;
    private List<String> publicos;
    private List<String> completados;
    private List<String> postulados;
    private boolean esLoginConEmailYPass;

    private float calificacionGeneral;

    private ArrayList<Float> calificaciones;

    public ModelUsuario() {
    }

    public ModelUsuario(String nombre, String apellido, String email, String pass, String userNameCustom,
                        String userId, String tokenFcm, String imagenPerfil, String fechaNac, String ciudad, String pais,
                        String facebook, String twitter, String instagram, boolean esLoginConEmailYPass) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.pass = pass;
        this.userNameCustom = userNameCustom;
        this.userId = userId;
        this.tokenFcm = tokenFcm;
        this.imagenPerfil = imagenPerfil;
        this.fechaNac = fechaNac;
        this.ciudad = ciudad;
        this.pais = pais;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
        this.esLoginConEmailYPass=esLoginConEmailYPass;
        this.privados = null;
        this.publicos = null;
        this.completados = null;
        this.postulados = null;
        this.calificaciones = null;
        calificacionGeneral = 0.0f;

    }

    public ModelUsuario(String mail, String pass, String userNameCustom,String userId, String tokenFcm,boolean esLoginConEmailYPass) {
        this.userId = userId;
        this.tokenFcm = tokenFcm;
        this.email = mail;
        this.pass = pass;
        this.userNameCustom = userNameCustom;
        this.esLoginConEmailYPass=esLoginConEmailYPass;


        // Inicializar los demás atributos como null
        this.nombre = null;
        this.apellido = null;
        this.imagenPerfil = null;
        this.fechaNac = null;
        this.ciudad = null;
        this.pais = null;
        this.facebook = null;
        this.twitter = null;
        this.instagram = null;
        this.privados = null;
        this.publicos = null;
        this.completados = null;
        this.postulados = null;
        this.calificaciones = null;
        calificacionGeneral = 0.0f;
    }


    public ModelUsuario(String email, String pass, String userNameCustom,boolean esLoginConEmailYPass) {
        this.email = email;
        this.pass = pass;
        this.userNameCustom = userNameCustom;
        this.esLoginConEmailYPass=esLoginConEmailYPass;


        //Resto de los campos en null
        this.userId = null;
        this.tokenFcm = null;
        this.nombre = null;
        this.apellido = null;
        this.imagenPerfil = null;
        this.fechaNac = null;
        this.ciudad = null;
        this.pais = null;
        this.facebook = null;
        this.twitter = null;
        this.instagram = null;
        this.privados = null;
        this.publicos = null;
        this.completados = null;
        this.postulados = null;
        this.calificaciones = null;
        calificacionGeneral = 0.0f;
    }

    public ModelUsuario(String email, String pass, String userNameCustom,String userId,boolean esLoginConEmailYPass) {
        this.email = email;
        this.pass = pass;
        this.userNameCustom = userNameCustom;
        this.userId = userId;
        this.esLoginConEmailYPass=esLoginConEmailYPass;


        //Resto de los campos en null
        this.tokenFcm = null;
        this.nombre = null;
        this.apellido = null;
        this.imagenPerfil = null;
        this.fechaNac = null;
        this.ciudad = null;
        this.pais = null;
        this.facebook = null;
        this.twitter = null;
        this.instagram = null;
        this.privados = null;
        this.publicos = null;
        this.completados = null;
        this.postulados = null;
        this.calificaciones = null;
        calificacionGeneral = 0.0f;
    }


    public void agregarCalificacion(float calificacion) {
        if (calificaciones == null) {
            calificaciones = new ArrayList<>();
        }
        calificaciones.add(calificacion);
    }

    // Método para calcular la calificación promedio y setearla en calificacionGeneral
    public void calcularYSetearCalificacionPromedio() {
        if (calificaciones == null || calificaciones.isEmpty()) {
            calificacionGeneral = 0.0f; // Si no hay calificaciones, se asigna 0.0f
            return;
        }

        float sumaCalificaciones = 0.0f;
        for (float calificacion : calificaciones) {
            sumaCalificaciones += calificacion;
        }

        calificacionGeneral = (float) sumaCalificaciones / calificaciones.size();
    }

    public void agregarEventoPrivado(String eventoId) {
        if (this.privados == null) {
            this.privados = new ArrayList<>();
        }
        this.privados.add(eventoId);
    }

    public void agregarEventoPublico(String eventoId) {
        if (this.publicos == null) {
            this.publicos = new ArrayList<>();
        }
        this.publicos.add(eventoId);
    }

    public void agregarEventoCompletado(String eventoId) {
        if (this.completados == null) {
            this.completados = new ArrayList<>();
        }
        this.completados.add(eventoId);
    }

    public void agregarEventoPostulado(String eventoId) {
        if (this.postulados == null) {
            this.postulados = new ArrayList<>();
        }
        this.postulados.add(eventoId);
    }



    //********Getters

    public float getCalificacionGeneral() {
        return calificacionGeneral;
    }

    public ArrayList<Float> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificacionGeneral(float calificacionGeneral) {
        this.calificacionGeneral = calificacionGeneral;
    }

    public void setCalificaciones(ArrayList<Float> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public boolean getEsLoginConEmailYPass() {
        return esLoginConEmailYPass;
    }

    public void setEsLoginConEmailYPass(boolean esLoginConEmailYPass) {
        this.esLoginConEmailYPass = esLoginConEmailYPass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUserNameCustom() {
        return userNameCustom;
    }

    public void setUserNameCustom(String userNameCustom) {
        this.userNameCustom = userNameCustom;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenFcm() {
        return tokenFcm;
    }

    public void setTokenFcm(String tokenFcm) {
        this.tokenFcm = tokenFcm;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    // Getter y Setter para 'nombre'
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter y Setter para 'apellido'
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }





    // Getter y Setter para Listas de punteros
    public List<String> getPrivados() {
        return privados;
    }

    public void setPrivados(List<String> privados) {
        this.privados = privados;
    }

    // Getter y Setter para 'publicos'
    public List<String> getPublicos() {
        return publicos;
    }

    public void setPublicos(List<String> publicos) {
        this.publicos = publicos;
    }

    // Getter y Setter para 'completados'
    public List<String> getCompletados() {
        return completados;
    }

    public void setCompletados(List<String> completados) {
        this.completados = completados;
    }

    // Getter y Setter para 'postulados'
    public List<String> getPostulados() {
        return postulados;
    }

    public void setPostulados(List<String> postulados) {
        this.postulados = postulados;
    }





}//fin clase
