package com.demo.hbslenovo_3.androidimagegallerypluscamerasample.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.hbslenovo_3.androidimagegallerypluscamerasample.R;
import com.demo.hbslenovo_3.androidimagegallerypluscamerasample.utility.MarshMallowPermission;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends BaseActivity implements ImageChooserListener {


   /*init imageview throw butterknife*/
    @Bind(R.id.profile_image)
    SimpleDraweeView mImage;

/*for image*/


    private MarshMallowPermission marshMallowPermission;


    String imageProfilePic_base64="";


    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String filePath;
    private boolean isActivityResultOver = false;
    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;
    private final static String TAG = "ICA";
    File imageFile, downloadedImage;
    private String is_image_updated = "0";
    private String str_set_response = "";

    private ChosenImage mChosenImageObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
/*init butter knife*/
        ButterKnife.bind(this);
/*permission dangerous initialization*/
        marshMallowPermission = new MarshMallowPermission(CameraActivity.this);
    }

/*handle image picker*/

    @OnClick(R.id.profile_image)
    public void methodProfileImage(View view) {
        pickimage();
    }



    /*pick image*/
    void pickimage() {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(CameraActivity.this);
        alertbox.setMessage("Please Select The Image");
        alertbox.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
/*method handle dangerous permission*/
                        pickImageFromGallery();
                    }
                });
        alertbox.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        /*method handle dangerous permission*/
                        takeImageFromCamera();

                    }
                });


        AlertDialog alert_box_show = alertbox.create();

        alert_box_show.show();
    }

    void pickImageFromGallery(){
        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        } else {
            chooseImage();

        }
    }


    void takeImageFromCamera(){
        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        } else {
            if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                marshMallowPermission.requestPermissionForExternalStorage();
            } else {
                takePicture();
            }

        }
    }




    private void chooseImage() {


        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        CameraActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mChosenImageObj = image;

                Log.i(TAG, "Chosen Image: O - " + image.getFilePathOriginal());
                Log.i(TAG, "Chosen Image: T - " + image.getFileThumbnail());
                Log.i(TAG, "Chosen Image: Ts - " + image.getFileThumbnailSmall());
                isActivityResultOver = true;
                originalFilePath = image.getFilePathOriginal();


                int orientation = getCameraPhotoOrientation(CameraActivity.this, Uri.parse(originalFilePath), originalFilePath);

                thumbnailFilePath = image.getFileThumbnail();
                thumbnailSmallFilePath = image.getFileThumbnailSmall();
                // pbar.setVisibility(View.GONE);
                if (image != null) {
                    Log.i(TAG, "Chosen Image: Is not null");
                    //textViewFile.setText(image.getFilePathOriginal());
                    loadImage(mImage, image.getFileThumbnail());
                    // loadImage(imageViewThumbSmall, image.getFileThumbnailSmall());
                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }
        });
    }

    String convertBase64(String fileName) {
        InputStream inputStream = null;//You can get an inputStream using any IO API
        try {
            inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;

    }

    private void loadImage(ImageView iv, final String path) {
        is_image_updated = "1";
        System.out.println("pathhh" + path);
        File dir = Environment.getExternalStorageDirectory();
        imageProfilePic_base64 = convertBase64(path);
        imageFile = new File(path);
        System.out.println("fileeeee" + imageFile);

        try {

            mImage.setImageURI(Uri.fromFile(new File(path)));
        } catch (Exception e) {
            e.printStackTrace();
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.placeholder).build();

            mImage.setImageURI(imageRequest.getSourceUri());
        }
    }

    @Override
    public void onError(final String reason) {
        CameraActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "OnError: " + reason);
                Toast.makeText(CameraActivity.this, reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "OnActivityResult");
        Log.i(TAG, "File Path : " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        if (resultCode == -1
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        } else {
        }
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }


    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }






}
