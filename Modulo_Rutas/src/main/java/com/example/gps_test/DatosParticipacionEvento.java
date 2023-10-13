package com.example.gps_test;

public class DatosParticipacionEvento {

    private String comienzo;
    private String finalizacion;
    private String distanciaCubierta;

    public DatosParticipacionEvento()
    {}

    public DatosParticipacionEvento(String comienzo, String finalizacion, String distanciaCubierta) {
        this.setComienzo(comienzo);
        this.setFinalizacion(finalizacion);
        this.setDistanciaCubierta(distanciaCubierta);
    }

    public String getComienzo() {
        return comienzo;
    }

    public void setComienzo(String comienzo) {
        this.comienzo = comienzo;
    }

    public String getFinalizacion() {
        return finalizacion;
    }

    public void setFinalizacion(String finalizacion) {
        this.finalizacion = finalizacion;
    }

    public String getDistanciaCubierta() {
        return distanciaCubierta;
    }

    public void setDistanciaCubierta(String distanciaCubierta) {
        this.distanciaCubierta = distanciaCubierta;
    }
}
