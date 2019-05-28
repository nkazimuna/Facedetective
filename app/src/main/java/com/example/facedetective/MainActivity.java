package com.example.facedetective;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;

import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity {
    private Button btn, btnUpload;
    private ImageView imageView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private SpotsDialog mDialog;


    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        btnUpload = (Button) findViewById(R.id.upload);
        imageView = (ImageView) findViewById(R.id.imageview);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                pickImageFromGallery();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Log.i("Info", "Clicked on process");
                //AlertDialog dialog = new AlertDialog.Builder(v.getContext()).setMessage("Loading...").show();
                android.app.AlertDialog myDialog = new SpotsDialog.Builder()
                        .setMessage("Loading...")
                        .setCancelable(false)
                        .setContext(getApplicationContext())
                        .build();
                //myDialog.show();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.face,
                        options);




                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5);
                myRectPaint.setColor(Color.BLUE);
                myRectPaint.setStyle(Paint.Style.STROKE);

                //Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);

                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                /*FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                 .build();*/

                FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false).build();

                if (!faceDetector.isOperational()) {
                    new AlertDialog.Builder(v.getContext()).setMessage("Cannot set up face detection").show();
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                //SparseArray<Face> face = faceDetector.detect(frame);
                SparseArray<com.google.android.gms.vision.face.Face> face = faceDetector.detect(frame);

                for (int i = 0; i < face.size(); i++) {
                    com.google.android.gms.vision.face.Face thisFace = face.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();

                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

                }
                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                imageView.getDrawable();


            }
        });
    }

    private void pickImageFromGallery() {
        //Intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of Runtime permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    pickImageFromGallery();
                } else {
                    //permission denied
                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Welcome to AndroidHive", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to imageView
            imageView.setImageURI(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
