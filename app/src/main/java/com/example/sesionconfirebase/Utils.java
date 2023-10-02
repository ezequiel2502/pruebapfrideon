package com.example.sesionconfirebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String calcularTiempo (String fechaInicio, String fechaFinalizacion) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date dateInicio = null;
            try {
                dateInicio = sdf.parse(fechaInicio);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Date dateFinalizacion = sdf.parse(fechaFinalizacion);

            // Diferencia en milisegundos
            long diferenciaEnMillis = dateFinalizacion.getTime() - dateInicio.getTime();

            // Convertir milisegundos a horas, minutos y segundos
            long horas = TimeUnit.MILLISECONDS.toHours(diferenciaEnMillis);
            long minutos = TimeUnit.MILLISECONDS.toMinutes(diferenciaEnMillis) % 60;


            // Formato HH:mm
            return String.format("%02d:%02d", horas, minutos);


        } catch (ParseException e) {
            e.printStackTrace();
            return "Error"; // Si hay un error, devolvemos "Error" como indicador de error
        }
    }

    public static String calcularVelocidad(String distanciaRecorrida, String tiempo) {
        // Convertimos la distancia a float
        float distancia = Float.parseFloat(distanciaRecorrida);

        // Dividimos el tiempo en horas y minutos
        String[] partesTiempo = tiempo.split(":");
        int horas = Integer.parseInt(partesTiempo[0]);
        int minutos = Integer.parseInt(partesTiempo[1]);

        // Convertimos las horas y minutos a horas decimales
        float tiempoEnHoras = horas + (float) minutos / 60;

        // Calculamos la velocidad
        float velocidad = distancia / tiempoEnHoras;

        // Convertimos la velocidad a String
        return String.format("%.2f", velocidad) + " km/h";
    }

}
