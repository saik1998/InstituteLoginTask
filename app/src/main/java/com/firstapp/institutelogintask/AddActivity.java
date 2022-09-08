package com.firstapp.institutelogintask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    AlertDialog alertDialog;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    DataAdapter dataAdapter;
    ArrayList<DataModel> modelArrayList = new ArrayList<>();

    ProgressDialog progressDialog;
    ImageView setimage;
    Uri uri;
    StorageReference storageReference;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.listview1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(modelArrayList, this);
        recyclerView.setAdapter(dataAdapter);

        setimage = findViewById(R.id.setImage);

        firebaseStorage = FirebaseStorage.getInstance();
        //Storage location Created in Firebase Storage
        storageReference = firebaseStorage.getReference("Moivee");

        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);



//fetching the data in the firebase using collection path
        firebaseFirestore.collection("Moives").get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AddActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
            //Fetching the data using complete listener
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(!task.isSuccessful())
                {
                    Toast.makeText(AddActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    for(QueryDocumentSnapshot qb:task.getResult())
                    {
                        String name=qb.getString("name");
                        String content=qb.getString("content");

                        DataModel dataModel=new DataModel(name,content);
                        modelArrayList.add(dataModel);
                        dataAdapter.notifyDataSetChanged();
                    }
                }


            }
        });
    }



//click plus symbol popup will come using ADDContent method
    public void ADDContent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        View root = getLayoutInflater().inflate(R.layout.createcontent, null);
        builder.setView(root);
        builder.setCancelable(false);

        Button save = root.findViewById(R.id.save);
        Button cancel = root.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText title = root.findViewById(R.id.title1);
                EditText content = root.findViewById(R.id.content1);
                String titleStr = title.getText().toString();
                String contentStr = content.getText().toString();

                if (TextUtils.isEmpty(titleStr)) {
                    title.setError("please enter the data");
                }
                if (TextUtils.isEmpty(contentStr)) {
                    content.setError("please enter the data");
                }

                addContent(titleStr, contentStr);

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }




    private void addContent(String titleStr, String contentStr) {
            CollectionReference collectionReference=firebaseFirestore.collection("Moives");

            DataModel data=new DataModel(titleStr,contentStr);
            collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
//                DataAdapter dataAdapter=new DataAdapter(modelArrayList,getApplicationContext());
//                recyclerView.setAdapter(dataAdapter);
                    Toast.makeText(AddActivity.this, "successfully loaded", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            DataAdapter dataAdapter=new DataAdapter(modelArrayList,this);
            recyclerView.setAdapter(dataAdapter);
            alertDialog.dismiss();
        }


        public void pickImageFromGallery(View view) {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 202);
        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {

            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case 202:
                    if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                        uri=data.getData();
                        setimage.setImageURI(uri);

//                        setimage.setImageURI(uri);
//                        uri = data.getData();
//                        setimage.setImageURI(uri);
                        //Log.d("Name0",""+uri);

//                    path.setText("Select Path is " + uri);
                        // getfileName(uri);
                    } else {
                        Toast.makeText(this, "File not Choose", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        }
        }


//
//    public void photolist(View view) {
//        startActivity(new Intent(AddActivity.this);
//    }














