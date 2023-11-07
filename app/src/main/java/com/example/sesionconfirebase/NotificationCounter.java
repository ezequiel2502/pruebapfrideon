package com.example.sesionconfirebase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.LinkedList;

public class NotificationCounter {

    // Método para registrar una notificación para un usuario

    public boolean registrarNotificacionCancelacionEvento(String titulo,String nombreEvento,String tipoNotificacion,String idEvento, String idPostulante)
    {
        FirebaseDatabase database;
        FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(nombreEvento);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setPostulanteId(idPostulante);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }

    public boolean registrarNotificacionCreadorEvento(String titulo, String detalle,String tipoNotificacion,String idEvento,String idOrganizador,String postulanteId,String nombreEvento)
    {
      FirebaseDatabase database;
      FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(detalle);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setPostulanteId(postulanteId);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setIdOrganizador(idOrganizador);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }
    public boolean registrarNotificacionCupoMaximoAlcanzado(String titulo, String detalle,String tipoNotificacion,String idEvento,String idOrganizador,String nombreEvento)
    {
        FirebaseDatabase database;
        FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(detalle);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setIdOrganizador(idOrganizador);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }

    public boolean registrarNotificacionPostulacionCancelada(String titulo, String detalle,String tipoNotificacion,String idEvento,String postulanteId,String IdOrganizador,String nombreEvento)
    {
        FirebaseDatabase database;
        FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(detalle);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setPostulanteId(postulanteId);
        notificacion.setIdOrganizador(IdOrganizador);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }

    public boolean registrarNotificacionPostulanteEvento(String titulo, String detalle,String tipoNotificacion,String idEvento,String idPostulante,String nombreEvento)
    {
        FirebaseDatabase database;
        FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(detalle);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setPostulanteId(idPostulante);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }

    public boolean registrarNotificacionDenegacionPostulanteEvento(String titulo, String detalle,String tipoNotificacion,String idEvento,String idPostulante,String nombreEvento)
    {
        FirebaseDatabase database;
        FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(detalle);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setPostulanteId(idPostulante);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }

    // Método para obtener la cantidad de notificaciones para un usuario
    public int obtenerCantidadNotificaciones(String userId, LinkedList<ModelNotificacion> lista) {

        int cantidadNotificaciones = 0;
        for(int i=0;i< lista.size();i++)
            {
                if(lista.get(i).getIdOrganizador()!=null)
                    {
                        if(userId.equals(lista.get(i).getIdOrganizador()) || userId.equals(lista.get(i).getPostulanteId()))
                        {
                            cantidadNotificaciones++;
                        }
                    }else{
                    if(userId.equals(lista.get(i).getPostulanteId()))
                    {
                        cantidadNotificaciones++;
                    }
                }

            }
        return cantidadNotificaciones;
    }
}
