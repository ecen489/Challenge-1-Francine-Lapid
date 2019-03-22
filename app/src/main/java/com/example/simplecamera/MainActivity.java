package com.example.simplecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;

    private ImageView imageView;
    private Button takePicButton, savePicButton, displayPicButton, deletePicButton;
    private Camera camera;
    private Uri file;
    private EditText Name;
    private Bitmap picBitmap;
    DatabaseAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        takePicButton = (Button) findViewById(R.id.button1);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dispatchTakePictureIntent();
            }
        });

        savePicButton = (Button) findViewById(R.id.button2);
        savePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                addPicture(view);
            }
        });

        displayPicButton = (Button) findViewById(R.id.button3);
        displayPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewdata(v);
            }
        });

        Name = (EditText) findViewById(R.id.editText);
        helper = new DatabaseAdapter(this);

        deletePicButton = (Button) findViewById(R.id.button4);
        deletePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                delete(v);
            }
        });
    }

    public void addPicture (View view) {
        String t1 = Name.getText().toString();
        byte[] bytePic = getBytes(picBitmap);
        if (t1.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please Enter a name", Toast.LENGTH_LONG).show();
        }
        else {
            // long id = helper.insertData(t1);
            long id = helper.insertData(t1, bytePic);
            if (id <= 0) {
                Toast.makeText(getApplicationContext(), "Insertion unsuccessful", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "insertion successful", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void viewdata (View view) {
        String data = helper.getDataString();
        String name = Name.getText().toString();
        //byte[] imagetemp = helper.getDataImage();
        //Bitmap bitmap
        byte[] imagetemp = helper.getDataImage(name);
        picBitmap = getImage(imagetemp);
        imageView.setImageBitmap(picBitmap);
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public void delete(View view) {
        String uname = Name.getText().toString();
        if(uname.isEmpty()) {
            Toast.makeText(this, "pls enter name", Toast.LENGTH_LONG).show();
        }
        else {
            int a = helper.delete(uname);
            if (a <=0) {
                Toast.makeText(this, "unsuccessful", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "DELETED", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        //file = Uri.fromFile(getOutputMediaFile());
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        /*
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {

            }

            // Continue oly if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.simplecamera", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);

            // Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
            picBitmap = (Bitmap) intent.getExtras().get("data");
            //imageView.setImageBitmap(bitmap);
            imageView.setImageBitmap(picBitmap);

            //imageView.setImageURI(file);
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytes (Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage (byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

/*
    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
*/

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
}
