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



//    Ahora, el método acepta fechas en el formato "dd/MM/yyyy HH:mm:ss"
//    y devuelve la diferencia de tiempo en horas, minutos y segundos en el formato "HH:mm:ss".
public static String calcularTiempo2(String fechaInicio, String fechaFinalizacion) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    try {
        Date dateInicio = sdf.parse(fechaInicio);
        Date dateFinalizacion = sdf.parse(fechaFinalizacion);

        long diferenciaEnMillis = dateFinalizacion.getTime() - dateInicio.getTime();

        long horas = TimeUnit.MILLISECONDS.toHours(diferenciaEnMillis);
        long minutos = TimeUnit.MILLISECONDS.toMinutes(diferenciaEnMillis) % 60;
        long segundos = TimeUnit.MILLISECONDS.toSeconds(diferenciaEnMillis) % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segundos);

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

        // Calculamos la velocidad en m/s
        float velocidadMs = distancia / (tiempoEnHoras * 3600); // 1 hora = 3600 segundos


        // Convertimos la velocidad a String
//        return String.format("%.2f", velocidad) + " km/h";
            return String.format("%.2f", velocidadMs);
    }


//    Con esta modificación, el método calcularVelocidad ahora acepta un tiempo en formato "HH:mm:ss"
//    y calcula la velocidad en metros por segundo (m/s) en base a esa entrada.
    public static String calcularVelocidad2(String distanciaRecorrida, String tiempo) {
        // Convertimos la distancia a float

        float distancia = Float.parseFloat(distanciaRecorrida);

        // Dividimos el tiempo en horas, minutos y segundos
        String[] partesTiempo = tiempo.split(":");
        int horas = Integer.parseInt(partesTiempo[0]);
        int minutos = Integer.parseInt(partesTiempo[1]);
        int segundos = Integer.parseInt(partesTiempo[2]);

        // Convertimos las horas, minutos y segundos a segundos totales
        int tiempoTotalEnSegundos = horas * 3600 + minutos * 60 + segundos;

        // Calculamos la velocidad en m/s
        float velocidadMs = distancia / tiempoTotalEnSegundos;

        // Convertimos la velocidad a String
        return String.format("%.2f", velocidadMs);
    }


}
