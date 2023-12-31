package com.example.sesionconfirebase;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CrearEvento extends AppCompatActivity {
    Spinner spnRuta,spnCategoria;
    EditText txt_FechaEncuentro,txt_HoraEncuentro,txt_CupoMinimo,txt_CupoMaximo,txt_Descripcion,txt_NombreEvento;
    ImageView imvEvento;
    Button btn_FechaEncuentro,btn_CrearEvento,btn_HoraEncuentro;

    Switch switchActivar_Desactivar;

    ArrayList<String> categoriasList=new ArrayList<String>();
    ArrayList<String> rutasList=new ArrayList<String>();


    private int dia,mes,ano,hora,minutos;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    boolean activar_desactivar;

    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);


        //Creamos las instancias de para la rtdb y el storage
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        //ProgressDialog
        dialog=new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setTitle("Creando Evento");
        dialog.setCanceledOnTouchOutside(false);

        //Obtengo el ID del usuario logueado en Firebase Authentication:
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();


        //Controles de la vista
        btn_FechaEncuentro=findViewById(R.id.btn_FechaEncuentro);
        btn_HoraEncuentro=findViewById(R.id.btn_HoraEncuentro);
        btn_CrearEvento=findViewById(R.id.btn_CrearEvento);

        txt_FechaEncuentro=findViewById(R.id.txt_FechaEncuentro);
        txt_HoraEncuentro=findViewById(R.id.txt_HoraEncuentro);

        txt_CupoMinimo=findViewById(R.id.editTextCupoMinimo);
        txt_CupoMaximo=findViewById(R.id.editTextCupoMaximo);
        txt_Descripcion=findViewById(R.id.editTextDescripcion);
        txt_NombreEvento=findViewById(R.id.txt_NombreEvento);

        switchActivar_Desactivar=findViewById(R.id.sw_Activar_Desactivar);

        imvEvento=findViewById(R.id.imvEvento);

        // Establecer el estado inicial del Switch como activado (true)
        switchActivar_Desactivar.setChecked(true);

        //>>>>>>>>>>spnCategorias
        spnCategoria=findViewById(R.id.spnCategoria);

        //Lleno la lista de categorias
        categoriasList.add("Inicial");
        categoriasList.add("Intermedio");
        categoriasList.add("Avanzado");
        categoriasList.add("Experto");

        // Crear un ArrayAdapter utilizando la lista de categorías y un diseño simple para el spinner
        ArrayAdapter<String> categoriasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriasList);

        // Especificar el diseño para el menú desplegable
        categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Establecer el adaptador en el Spinner
        spnCategoria.setAdapter(categoriasAdapter);
        //spnCategorias<<<<<<<<<<



        //>>>>>>>>>>spnRutas
        spnRuta=findViewById(R.id.spnRuta);

        //Lleno la lista de rutas
        rutasList.add("Ruta_1");
        rutasList.add("Ruta_2");
        rutasList.add("Ruta_3");
        rutasList.add("Ruta_4");

        // Crear un ArrayAdapter utilizando la lista de rutas y un diseño simple para el spinner
        ArrayAdapter<String> rutasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rutasList);

        // Especificar el diseño para el menú desplegable
        rutasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establecer el adaptador en el Spinner
        spnRuta.setAdapter(rutasAdapter);
        //spnRutas<<<<<<<<<<


        //DatePicker
        btn_FechaEncuentro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                ano = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CrearEvento.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayofMonth) {
                        txt_FechaEncuentro.setText(dayofMonth+"/"+(monthOfYear+1)+"/"+year);
                    }
                }, ano, mes, dia); // Pasar los valores de año, mes y día para mostrar la fecha actual al abrir el diálogo
                datePickerDialog.show();
            }
        });

        //TimePicker
        btn_HoraEncuentro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                hora = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CrearEvento.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                        // Por ejemplo, actualizar un TextView con la hora seleccionada
                        txt_HoraEncuentro.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hora, minutos, true); // true indica que el cuadro de diálogo muestra el formato de 24 horas

                timePickerDialog.show(); // Mostrar el cuadro de diálogo
            }
        });




        // Agregar un listener para el cambio de estado del Switch
        switchActivar_Desactivar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Aquí puedes manejar el cambio de estado del Switch
                if (isChecked) {
                    activar_desactivar=true;
                } else {
                    // El Switch no está seleccionado, guardar "false"
                    activar_desactivar=false;
                }
            }
        });

        //Para elegir la imagen
        imvEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });



        //Crear Evento
        btn_CrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ProgressDialog
                dialog.show();



                // Obtener los valores seleccionados del Spinner
                String categoriaSeleccionada = spnCategoria.getSelectedItem().toString();
                String rutaSeleccionada = spnRuta.getSelectedItem().toString();

                // Obtener los valores ingresados en los EditText
                String cupoMinimo = txt_CupoMinimo.getText().toString();
                String cupoMaximo = txt_CupoMaximo.getText().toString();
                String descripcion = txt_Descripcion.getText().toString();
                String fechaEncuentro = txt_FechaEncuentro.getText().toString();
                String horaEncuentro = txt_HoraEncuentro.getText().toString();
                String nombreEvento = txt_NombreEvento.getText().toString();
                Boolean activadoDesactivado = activar_desactivar;

                // Formatea la fecha como una cadena legible
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = dateFormat.format(System.currentTimeMillis());

                // Crea una clave identificadora utilizando el nombre y la fecha
                String eventKey = nombreEvento + "_" + formattedDate;

                // Referencia al Storage para la imagen
                final StorageReference imageRef = firebaseStorage.getReference().child("Usuarios").child(userId).child(eventKey).child(System.currentTimeMillis() + "");

                imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Aca obtengo la Uri del storage
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ModelEvento evento = new ModelEvento();
                                evento.setCategoria(categoriaSeleccionada);
                                evento.setRuta(rutaSeleccionada);
                                evento.setNombreEvento(nombreEvento);
                                evento.setDescripcion(descripcion);
                                evento.setCupoMinimo(cupoMinimo);
                                evento.setCupoMaximo(cupoMaximo);
                                evento.setFechaEncuentro(fechaEncuentro);
                                evento.setHoraEncuentro(horaEncuentro);
                                evento.setActivarDesactivar(activadoDesactivado);
                                evento.setImagenEvento(uri.toString());

                                // Crea una referencia a la base de datos usando el ID del usuario
                                DatabaseReference userEventosRef = database.getReference().child("Usuarios").child(userId).child("Eventos");

                                // Crea un nuevo nodo para el evento
                                DatabaseReference nuevoEventoRef = userEventosRef.push();

                                // Guardar los datos en Firebase Realtime Database
                                nuevoEventoRef.setValue(evento).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // La inserción fue exitosa
                                        Toast.makeText(CrearEvento.this, "Evento registrado exitosamente", Toast.LENGTH_SHORT).show();

                                        //ProgressDialog
                                        dialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //ProgressDialog
                                        dialog.dismiss();

                                        // La inserción falló
                                        Toast.makeText(CrearEvento.this, "Error al registrar el evento en la base de datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La subida de la imagen falló
                        Toast.makeText(CrearEvento.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }//fin onCreate()

    private void requestStoragePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        openImagePicker();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        // Handle permission denied
                        Toast.makeText(CrearEvento.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imvEvento); // Usar Glide o cualquier otra librería de carga de imágenes
        }
    }

}//fin App