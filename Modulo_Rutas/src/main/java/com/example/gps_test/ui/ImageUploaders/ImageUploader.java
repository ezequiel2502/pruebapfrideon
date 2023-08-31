package com.example.gps_test.ui.ImageUploaders;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ImageUploader {

    public FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    StorageReference storageRef;



    public void cargar_bytes (String userID, Bitmap iv, String routeReference)
    {
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference().child("Usuarios").child(userID).child("routes").child(routeReference);


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        iv.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                updateStorageLink(uploadTask, routeReference);
            }
        });
    }

    public void updateStorageLink (UploadTask uploadTask, String routeReference) {

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    DatabaseReference ref = database.getReference().child("Route");
                    ref.child(routeReference).child("imgLocation").setValue(downloadUri.toString());

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

}
