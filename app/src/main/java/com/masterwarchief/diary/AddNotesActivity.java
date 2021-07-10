package com.masterwarchief.diary;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.ViewPort;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AddNotesActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private BottomAppBar bottomAppBar;
    private EditText titleField,notesField;
    private ProgressDialog mProgress;
    private FloatingActionButton fab;
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private String tit=null,not=null;
    private String saveCurrentDate=null,saveCurrentTime=null,time=null,key=null;
    private  boolean isImp=false;
    private Bitmap image;
    private Menu menu;
    private Button capture,flash_light;
    private PreviewView mPreviewView;
    private View bottomSheetView;
    private BottomSheetDialog bottomSheetDialog;
    private Boolean flash;
    private boolean deviceHasCameraFlash;
    Camera camera;
    ProcessCameraProvider cameraProvider;
    private String user_id;
    private EncryptDecrypt encryptDecrypt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        flash=false;
        mProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        encryptDecrypt=new EncryptDecrypt();
        tit="";
        not="";
        user_id=mUser.getUid();
        bottomSheetDialog= new BottomSheetDialog(AddNotesActivity.this);
        LayoutInflater layoutinflate= LayoutInflater.from(AddNotesActivity.this);
        bottomSheetView = layoutinflate.inflate(R.layout.activity_o_c_r, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        capture= bottomSheetView.findViewById(R.id.capture);
        flash_light=bottomSheetView.findViewById(R.id.flash_on_off);
        mPreviewView=bottomSheetView.findViewById(R.id.camera);
        deviceHasCameraFlash = getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        mToolbar=findViewById(R.id.add_notes_bar);
        mPreviewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        bottomAppBar=findViewById(R.id.bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.add_down_menu);

        bottomAppBar.setTitle("Edited: 9 Nov");

        Intent intent =getIntent();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("New Note");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleField=findViewById(R.id.add_notes_title);
        notesField=findViewById(R.id.add_notes_note);
        fab=findViewById(R.id.fab);

        if(intent.getExtras().getString("uniqueKey").equals("from_main")) {

            getSupportActionBar().setTitle("Your Note");

            key=intent.getStringExtra("postKey");
            System.out.println(key);
            mRef= FirebaseDatabase.getInstance().getReference().child("notes").child(mUser.getUid()).child(key);

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        try {
                            tit=encryptDecrypt.decrypt(dataSnapshot.child("title").getValue().toString(), user_id);
                            not=encryptDecrypt.decrypt(dataSnapshot.child("note").getValue().toString(),user_id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        titleField.setText(tit);

                        notesField.setText(not);
                        isImp=Boolean.valueOf(dataSnapshot.child("imp").getValue().toString());
                    }else{
                        //do nothing
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{ mRef= FirebaseDatabase.getInstance().getReference().child("notes");}

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyDetails();

            }
        });

        /*if (!titleField.hasFocus() && !notesField.hasFocus()){
            bottomAppBar.getMenu().getItem(R.id.undo).setIcon(R.drawable.undo_grey);
            bottomAppBar.getMenu().getItem(R.id.redo).setIcon(R.drawable.redo_grey);
        }*/
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cameraProvider.unbindAll();
            }
        });
        final TextViewUndoRedo nTextViewUndoRedo = new TextViewUndoRedo(titleField);
        final TextViewUndoRedo mTextViewUndoRedo = new TextViewUndoRedo(notesField);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.undo){
                    if (titleField.hasFocus()){

                        if (!nTextViewUndoRedo.getCanUndo()){
                            Toast.makeText(AddNotesActivity.this, "Cannot Undo!", Toast.LENGTH_SHORT).show();
                        }else{
                            nTextViewUndoRedo.undo();
                        }
                    }

                    if (notesField.hasFocus()){

                        if (!mTextViewUndoRedo.getCanUndo()){
                            Toast.makeText(AddNotesActivity.this, "Cannot Undo!", Toast.LENGTH_SHORT).show();
                        }else{
                            mTextViewUndoRedo.undo();
                        }
                    }

                }

                if (item.getItemId()==R.id.redo){
                    if (titleField.hasFocus()){

                        if (!nTextViewUndoRedo.getCanRedo()){
                            Toast.makeText(AddNotesActivity.this, "Cannot Redo!", Toast.LENGTH_SHORT).show();
                        }else{
                            nTextViewUndoRedo.redo();
                        }
                    }

                    if (notesField.hasFocus()){
                        if (!mTextViewUndoRedo.getCanRedo()){
                            Toast.makeText(AddNotesActivity.this, "Cannot Redo!", Toast.LENGTH_SHORT).show();
                        }else{
                            mTextViewUndoRedo.redo();
                        }
                    }
                }

                if(item.getItemId()==R.id.ocr_btn){
                    startCamera();
                    //BottomSheetBehavior.from(bottomSheetView).setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetDialog.show();
                }
                return false;
            }
        });
    }

    private void verifyDetails() {
        String title=titleField.getText().toString().trim();
        String note=notesField.getText().toString().trim();

        if (title.isEmpty() || title.length() <4){
            titleField.setError("Title length should be more than 4 characters!");
            titleField.requestFocus();
            return;
        }

        if (note.isEmpty() || note.length() <4){
            notesField.setError("Note length should be more than 4 characters!");
            notesField.requestFocus();
            return;
        }

        System.out.println(title+" "+tit);

        if (title.equals(tit) && note.equals(not)){
            Toast.makeText(this, "No Changes found!", Toast.LENGTH_SHORT).show();

        }else{
            mProgress.setMessage("Submitting your notes safely :)");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();
            saveNote(title,note);
        }


    }

    private void saveNote(String title, String note) {

        long milis=System.currentTimeMillis();
        time=Long.toString(milis);

        Calendar calendarForDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd:MMMM:yyyy");
        saveCurrentDate=currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime= Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calendarForTime.getTime());

        String e_title=title;
        String e_note=note;

        try {
            e_title= encryptDecrypt.encrypt(title, user_id);
            e_note=encryptDecrypt.encrypt(note, user_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap map =new HashMap<>();
        map.put("title",e_title);
        map.put("note",e_note);
        map.put("imp", String.valueOf(isImp));
        map.put("date",saveCurrentDate);
        map.put("time",saveCurrentTime);

        if (key!=null){
            mRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mProgress.dismiss();
                        Toast.makeText(AddNotesActivity.this, "Note edited", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddNotesActivity.this,MainActivity.class));
                        finish();
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(AddNotesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            mRef.child(mUser.getUid()).child(time).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mProgress.dismiss();
                        Toast.makeText(AddNotesActivity.this, "New note added!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddNotesActivity.this,MainActivity.class));
                        finish();
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(AddNotesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.add_up_menu,menu);
        this.menu=menu;
        if(isImp){
            menu.findItem(R.id.up_star).setIcon(R.drawable.ic_star_black_24dp);}
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.up_delete){

            AlertDialog.Builder builder=new AlertDialog.Builder(AddNotesActivity.this);
            builder.setTitle("Confirm Delete!");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (key!=null){
                        mRef.removeValue();
                        onBackPressed();
                    }else{
                        onBackPressed();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }

        if (item.getItemId()==R.id.up_star){
            Drawable drawable=item.getIcon();

            if ( isImp==false){

                isImp=true;
                if (key!=null) {
                    mRef.child("imp").setValue(String.valueOf(isImp));
                }
                item.setIcon(R.drawable.ic_star_black_24dp);
                Toast.makeText(AddNotesActivity.this, "Note is marked as important.", Toast.LENGTH_SHORT).show();
                item.setTitle("Mark Important");

            }else if (isImp){

                isImp=false;
                if (key!=null){
                    mRef.child("imp").setValue(String.valueOf(isImp));
                }

                item.setIcon(R.drawable.ic_star_border_black_24dp);
                Toast.makeText(AddNotesActivity.this, "Note is unmarked from important.", Toast.LENGTH_SHORT).show();
                item.setTitle("Remove from Imp.");

            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(AddNotesActivity.this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(AddNotesActivity.this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        mPreviewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
        //bottomSheetView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        camera= cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);


        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = mPreviewView.getBitmap();
                Toast.makeText(AddNotesActivity.this, "Captured", Toast.LENGTH_SHORT).show();
                InputImage inputImage = InputImage.fromBitmap(image, 0);
                TextRecognizer recognizer = TextRecognition.getClient();
                Task<Text> result =
                        recognizer.process(inputImage)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        String resultText = visionText.getText();
                                        notesField.setText(notesField.getText()+resultText);

                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddNotesActivity.this, "Reading Failed Try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                bottomSheetDialog.cancel();
                cameraProvider.unbindAll();
            }
        });
        flash_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceHasCameraFlash){
                    try{
                        if(flash) {
                            flash = false;
                            camera.getCameraControl().enableTorch(flash);
                            flash_light.setBackgroundResource(R.drawable.ic_baseline_flash_off_24);
                        }
                        else{
                            flash=true;
                            camera.getCameraControl().enableTorch(flash);
                            flash_light.setBackgroundResource(R.drawable.ic_baseline_flash_on_24);
                        }

                    } catch (Exception e){

                    }
                }
                else{
                    Toast.makeText(AddNotesActivity.this, "No flash detected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
