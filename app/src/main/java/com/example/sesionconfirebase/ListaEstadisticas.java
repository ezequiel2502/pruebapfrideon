package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.calcularTiempo;
import static com.example.sesionconfirebase.Utils.calcularTiempo2;
import static com.example.sesionconfirebase.Utils.calcularVelocidad;
import static com.example.sesionconfirebase.Utils.calcularVelocidad2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.DatosParticipacionEvento;
import com.example.gps_test.Ruta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaEstadisticas extends AppCompatActivity {



    RecyclerView recyclerViewEstadisticasEventos;
    ArrayList<ModelEstadistica> recycleList;

    FirebaseDatabase firebaseDatabase;

    EstadisticasAdapter recyclerAdapter;

    String userNameCustom;

    TextView tvTiempoTotal,tvDistanciaTotal,tvVelocidadPromedio;

    //Para totales y promedios
    int totalTiempo;
    double totalDistancia;
    double totalVelocidad;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_estadisticas);

        // Tomo los ocntroles de la vista

        recyclerViewEstadisticasEventos=findViewById(R.id.recyclerViewEstadisticasEventos);
        tvTiempoTotal=findViewById(R.id.tvTiempoTotal);
        tvDistanciaTotal=findViewById(R.id.tvDistanciaTotal);
        tvVelocidadPromedio=findViewById(R.id.tvVelocidadPromedio);

        recycleList = new ArrayList<>();




        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Creo una instancia del adapter
        recyclerAdapter = new EstadisticasAdapter(recycleList, ListaEstadisticas.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEstadisticasEventos.setLayoutManager(linearLayoutManager);
        recyclerViewEstadisticasEventos.addItemDecoration(new DividerItemDecoration(recyclerViewEstadisticasEventos.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewEstadisticasEventos.setNestedScrollingEnabled(false);
        recyclerViewEstadisticasEventos.setAdapter(recyclerAdapter);



        //-----Accedo al perfil del usuario participante-------

        // Obtener el ID del usuario actualmente logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Acceder al nodo de "Perfil" del usuario
        DatabaseReference perfilRef = firebaseDatabase.getReference().child("Perfil").child(userId);

        perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recupera el objeto ModelUsuario
                    ModelUsuario modelUsuario = dataSnapshot.getValue(ModelUsuario.class);

                    if (modelUsuario != null) {
                        // Obtiene el userNameCustom
                        userNameCustom = modelUsuario.getUserNameCustom();



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error de cancelación
            }
        });

        //-----Accedo al perfil del usuario participante-------







        //*****Observo cambios en Events_data

        DatabaseReference eventsDataRef = firebaseDatabase.getReference().child("Events_Data").child(userId);

        eventsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventoSnapshot : dataSnapshot.getChildren()) {

                    // Tomo la key del evento que me va a servir para buscarlo en el nodo de completados
                    String eventoId = eventoSnapshot.getKey();

                    // Recojo todos los datos de finalización para armar la estadística
                    DatabaseReference datosParticipacionRef = firebaseDatabase.getReference().child("Events_Data").child(userId).child(eventoId);

                    datosParticipacionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                // Datos de participación
                                DatosParticipacionEvento datosParticipacion = dataSnapshot.getValue(DatosParticipacionEvento.class);
                                if (datosParticipacion != null) {

                                    String comienzo = datosParticipacion.getComienzo();
                                    String finalizacion = datosParticipacion.getFinalizacion();
                                    String distanciaCubierta = datosParticipacion.getDistanciaCubierta();
                                    String Abandono =datosParticipacion.getAbandono();
                                    // Ahora entro en el nodo de completados para obtener el resto de datos para armar la estadística
                                    DatabaseReference eventosCompletadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(eventoId);

                                    eventosCompletadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot eventoSnapshot) {
                                            if (eventoSnapshot.exists()) {

                                                // Datos del evento completado
                                                ModelEvento evento = eventoSnapshot.getValue(ModelEvento.class);

                                                // Obtener el ID de la ruta
                                                String rutaId = evento.getRuta();

                                                // Acceder a los datos de la ruta
                                                DatabaseReference rutaRef = firebaseDatabase.getReference().child("Route").child(rutaId);

                                                rutaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Ruta ruta = dataSnapshot.getValue(Ruta.class);

                                                            // Obtener el nombre de la ruta
                                                            String nombreRuta = ruta.getRouteName();
                                                            String estadisticaId = eventoId + "_" + userId; // Crear un ID único para cada estadística

                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            DatabaseReference estadisticasRef = database.getReference("Estadisticas");
                                                            final ModelEstadistica[] estadistica = new ModelEstadistica[1];
                                                            estadisticasRef.child(estadisticaId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        // La estadística ya existe, puedes recuperarla si es necesario
                                                                        estadistica[0] = dataSnapshot.getValue(ModelEstadistica.class);
                                                                        // Aquí puedes hacer lo que necesites con la estadística existente
                                                                    } else {
                                                                        // La estadística no existe, puedes crear una nueva
                                                                        // Aquí se usa comienzo, finalizacion, distanciaCubierta y evento para crear ModelEstadistica
                                                                        estadistica[0] = new ModelEstadistica(
                                                                                evento.getUserId(),//del organizador
                                                                                evento.getUserName(),//del organizador
                                                                                eventoId,
                                                                                evento.getNombreEvento(),
                                                                                nombreRuta,
                                                                                userId,//del usuario que participó
                                                                                userNameCustom,
                                                                                evento.getImagenEvento(),
                                                                                distanciaCubierta,
                                                                                calcularTiempo2(comienzo, finalizacion),
                                                                                calcularVelocidad2(distanciaCubierta, calcularTiempo2(comienzo, finalizacion)),
                                                                                Abandono
                                                                        );
                                                                        estadisticasRef.child(estadisticaId).setValue(estadistica[0]);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    // Manejar errores de base de datos si es necesario
                                                                }
                                                            });
                                                           if(estadistica[0] == null)
                                                           {
                                                             estadistica[0] = new ModelEstadistica(
                                                             evento.getUserId(),//del organizador
                                                             evento.getUserName(),//del organizador
                                                             eventoId,
                                                             evento.getNombreEvento(),
                                                             nombreRuta,
                                                             userId,//del usuario que participó
                                                             userNameCustom,
                                                             evento.getImagenEvento(),
                                                             distanciaCubierta,
                                                             calcularTiempo2(comienzo, finalizacion),
                                                             calcularVelocidad2(distanciaCubierta, calcularTiempo2(comienzo, finalizacion)),
                                                             Abandono
                                                             );
                                                             estadisticasRef.child(estadisticaId).setValue(estadistica[0]);
                                                             }

                                                            // Agrega estadistica a tu lista
                                                            recycleList.add(estadistica[0]);
                                                            recyclerAdapter.notifyDataSetChanged();
                                                            // Para que se actualicen los promedios y totales de la lista
                                                            calcularTotalesYPromedios();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Manejar error de cancelación
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Manejar error de cancelación
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejar error de cancelación
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });






    }//fin onCreate()


    // Método para convertir tiempo en formato "HH:mm"
    private String convertirSegundosATiempo(int segundos) {
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int segundosRestantes = segundos % 60;
        return String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes);
    }

    // Método para calcular totales y promedios
    private void calcularTotalesYPromedios() {

        int totalTiempo = 0;
        double totalDistancia = 0;
        double totalVelocidad = 0;

        // Iterar a través de la lista de estadísticas
        for (ModelEstadistica estadistica : recycleList) {
            // Convertir tiempos a segundos
            totalTiempo += convertirTiempoASegundos(estadistica.getTiempo());

            // Convertir distancia a double
            totalDistancia += Double.parseDouble(estadistica.getDistanciaRecorrida());

            // Convertir velocidad a double
            totalVelocidad += Double.parseDouble(estadistica.getVelocidadPromEvento());
        }

        // Calcular promedio de velocidad
        double promedioVelocidad = totalVelocidad / recycleList.size();

        // Mostrar los resultados
        tvTiempoTotal.setText(convertirSegundosATiempo(totalTiempo) + " (H:m)");
        tvDistanciaTotal.setText(String.format("%.2f", totalDistancia) + " mts"); // Formatear a dos decimales
        tvVelocidadPromedio.setText(String.format("%.2f", promedioVelocidad) + " mts/s"); // Formatear a dos decimales
    }



    // Método para convertir tiempo a segundos (formato HH:mm)
    private int convertirTiempoASegundos(String tiempo) {
        String[] partes = tiempo.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        int segundos = Integer.parseInt(partes[2]);
        return horas * 3600 + minutos * 60 + segundos;
    }


}//fin App
