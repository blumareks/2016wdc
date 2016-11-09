package com.ibm.watson.developer_cloud.android.library.audio;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CameraHelper {

  private final String TAG = CameraHelper.class.getName();
  public static final int REQUEST_IMAGE_CAPTURE = 1000;

  private Activity activity;
  private String currentPhotoPath;

  /**
   * Provides convenience access to device camera
   * @param activity The current activity
   */
  public CameraHelper(Activity activity) {
    this.activity = activity;
  }

  /**
   * Starts a activity using the device's onboard camera app
   */
  public void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        Log.e(TAG, "IOException", ex);
      }
      // Continue only if the File was successfully created
      if (photoFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
            Uri.fromFile(photoFile));
        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    currentPhotoPath = "file:" + image.getAbsolutePath();
    return image;
  }

  /**
   * This method returns the file of the photo that was taken. It should be called in onActivityResult.
   * @param resultCode Result Code of the previous activity
   * @return If successful, the image file. Null otherwise.
   */
  public File getFile(int resultCode) {
    if(resultCode == activity.RESULT_OK) {
      Uri targetUri = Uri.parse(currentPhotoPath);
      return new File(targetUri.getPath());
    }
    Log.e(TAG, "Result Code was not OK");
    return null;
  }

  /**
   * This method returns a bitmap of the photo that was taken. It should be called in onActivityResult.
   * Because the CameraHelper knows the path it's unnecessary to get it from the returned data.
   * @param resultCode Result Code of the previous activity
   * @return If successful, a bitmap of the image. Null otherwise.
   */
  public Bitmap getBitmap(int resultCode) {
    if(resultCode == activity.RESULT_OK) {
      Uri targetUri = Uri.parse(currentPhotoPath);
      try {
        return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(targetUri));
      } catch (FileNotFoundException e) {
        Log.e(TAG, "File Not Found", e);
        return null;
      }
    }
    Log.e(TAG, "Result Code was not OK");
    return null;
  }
}
