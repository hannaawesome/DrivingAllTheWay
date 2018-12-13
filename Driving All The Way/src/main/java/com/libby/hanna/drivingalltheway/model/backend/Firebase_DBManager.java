package com.libby.hanna.drivingalltheway.model.backend;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libby.hanna.drivingalltheway.model.entities.*;

import java.util.ArrayList;
import java.util.List;

import static com.libby.hanna.drivingalltheway.model.backend.TripConst.ContentValuesTOTrip;

public class Firebase_DBManager implements DB_manager {


    @Override
    public boolean addTrip(ContentValues trip) {
        try {
            addStudentToFirebase(ContentValuesTOTrip(trip), new Firebase_DBManager.Action<Long>() {
                @Override
                public void onSuccess(Long obj) {
                  //  Toast.makeText(getBaseContext(), "insert id " + obj, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Exception exception) {
                   // Toast.makeText(getBaseContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProgress(String status, double percent) {

                }
            });
            return true;
        } catch (Exception e)
        {
            return false;
          //  Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * gives details on success and failure and progress of an action T
     *
     * @param <T>
     */
    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);

        void onProgress(String status, double percent);
    }

    /*public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);
        void onFailure(Exception exception);*/
    static DatabaseReference TripRef;
    static List<Trip> tripList;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        TripRef = database.getReference("trips");
        tripList = new ArrayList<>();

    }

    private static void addStudentToFirebase(final Trip t, final Action<Long> action) {
        String key = t.get_id().toString();
        TripRef.child(key).setValue(t).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(t.get_id());
                action.onProgress("upload trip data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload trip data", 100);
            }
        });
    }
}
/*
    public static void addSTrip(final Trip student, final Action<Long> action) {
        //if (student.getImageLocalUri() != null) {
                 StorageReference imagesRef = FirebaseStorage.getInstance().getReference();
                    imagesRef = imagesRef.child("images").child(System.currentTimeMillis()

             + ".jpg");          imagesRef.putFile(student.getImageLocalUri())
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          action.onProgress("upload student data", 90);
             Get a URL to the uploaded content
              Uri downloadUrl = taskSnapshot.getDownloadUrl();
                  student.setImageFirebaseUrl(downloadUrl.toString());

                      addStudentToFirebase(student, action);
                             }                 })
              .addOnFailureListener(new OnFailureListener() {
                      @Override
             public void onFailure(@NonNull Exception exception) {
                              action.onFailure(exception);
                  action.onProgress("error upload student image", 100);
                      }       }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                       @Override
                 public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                           double uploadBytes = taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                           double progress = (90.0 * uploadBytes);
                           action.onProgress("upload image", progress)
                       });
                    }
                      else
                          action.onFailure(new Exception("select image first"));

}*/

