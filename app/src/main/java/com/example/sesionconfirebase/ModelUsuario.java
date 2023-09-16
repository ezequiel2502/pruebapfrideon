package com.example.sesionconfirebase;

import java.util.ArrayList;
import java.util.List;

public class ModelUsuario {

    private String nombre, apellido, email, pass, userNameCustom, userId, tokenFcm, imagenPerfil, fechaNac, ciudad, pais, facebook, twitter, instagram;
    private List<ModelEvento> privados;
    private List<ModelEvento> publicos;
    private List<ModelEvento> completados;
    private List<ModelEvento> postulados;

    public ModelUsuario(String nombre, String apellido, String email, String pass, String userNameCustom,
                        String userId, String tokenFcm, String imagenPerfil, String fechaNac, String ciudad, String pais,
                        String facebook, String twitter, String instagram) {
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
        this.privados = null;
        this.publicos = null;
        this.completados = null;
        this.postulados = null;
    }

    public ModelUsuario(String userId, String tokenFcm, String mail, String pass, String userNameCustom) {
        this.userId = userId;
        this.tokenFcm = tokenFcm;
        this.email = mail;
        this.pass = pass;
        this.userNameCustom = userNameCustom;

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
    }


    public ModelUsuario(String email, String pass, String userNameCustom) {
        this.email = email;
        this.pass = pass;
        this.userNameCustom = userNameCustom;


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
    }

    public ModelUsuario(String email, String pass, String userNameCustom,String userId) {
        this.email = email;
        this.pass = pass;
        this.userNameCustom = userNameCustom;
        this.userId = userId;


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
    }


    public void agregarEventoPrivado(ModelEvento evento) {
        if (this.privados == null) {
            this.privados = new ArrayList<>();
        }
        this.privados.add(evento);
    }

    public void agregarEventoPublico(ModelEvento evento) {
        if (this.publicos == null) {
            this.publicos = new ArrayList<>();
        }
        this.publicos.add(evento);
    }

    public void agregarEventoCompletado(ModelEvento evento) {
        if (this.completados == null) {
            this.completados = new ArrayList<>();
        }
        this.completados.add(evento);
    }

    public void agregarEventoPostulado(ModelEvento evento) {
        if (this.postulados == null) {
            this.postulados = new ArrayList<>();
        }
        this.postulados.add(evento);
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



    // Getter y Setter para 'privados'
    public List<ModelEvento> getPrivados() {
        return privados;
    }

    public void setPrivados(List<ModelEvento> privados) {
        this.privados = privados;
    }

    // Getter y Setter para 'publicos'
    public List<ModelEvento> getPublicos() {
        return publicos;
    }

    public void setPublicos(List<ModelEvento> publicos) {
        this.publicos = publicos;
    }

    // Getter y Setter para 'completados'
    public List<ModelEvento> getCompletados() {
        return completados;
    }

    public void setCompletados(List<ModelEvento> completados) {
        this.completados = completados;
    }

    // Getter y Setter para 'postulados'
    public List<ModelEvento> getPostulados() {
        return postulados;
    }

    public void setPostulados(List<ModelEvento> postulados) {
        this.postulados = postulados;
    }





}//fin clase
