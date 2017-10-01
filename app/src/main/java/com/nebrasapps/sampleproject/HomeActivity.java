package com.nebrasapps.sampleproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nebrasapps.sampleproject.storage.SharedData;
import com.nebrasapps.sampleproject.utils.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by NebrasApps.com on 01/10/2017
 * https://github.com/nebrasapps/Sample-Android/
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_AUDIO_CODE = 101;
    private int REQUEST_CAMERA = 101;
    private ImageView mImgUser;
    private String userImagePath = null;
    private Bitmap thumbnail;
    private LinearLayout imgLay;
    private ImageView selectedImg;
    private Dialog dialog;
    private Button mBtnCamera;
    private Button mBtnMsg;
    private Button mBtnRecord;
    public static int RECORD_REQUEST = 0;
    // . before directory name hides the folder
    private static final String IMAGE_DIRECTORY_NAME = ".SampleImges";
    public static final int MEDIA_TYPE_IMAGE = 2;
    Uri audioFileUri;
    private Uri fileUri;
    private LinearLayout audioLay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        selectedImg = (ImageView) findViewById(R.id.selected_img);
        imgLay = (LinearLayout) findViewById(R.id.img_lay);
        audioLay = (LinearLayout) findViewById(R.id.audio_lay);
        mBtnCamera = (Button) findViewById(R.id.camera);
        mBtnRecord = (Button) findViewById(R.id.record);
        mBtnMsg = (Button) findViewById(R.id.write_msg);

        //onClickListeners inside OnCreate Method
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent for loading url
                Uri uri = Uri.parse("http://www.nebrasapps.com");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
        findViewById(R.id.logo_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent for loading url
                Uri uri = Uri.parse("http://www.nebrasapps.com");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        findViewById(R.id.saveimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumbnail != null) {

                    saveImage();
                }
                mBtnCamera.setBackgroundResource(R.drawable.box_corner);
            }
        });
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioFileUri!=null ) {
                    try {
                        Intent viewMediaIntent = new Intent();
                        viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
                        viewMediaIntent.setDataAndType(audioFileUri, "audio/*");
                        viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(viewMediaIntent);
                    }catch (ActivityNotFoundException e)
                    {
                        Config.customToast(HomeActivity.this,"No app available to play audio");
                    }
                }}
        });

        findViewById(R.id.saveaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioFileUri != null) {

                    saveAudio();
                }
                mBtnRecord.setBackgroundResource(R.drawable.box_corner);
            }
        });

        findViewById(R.id.saveaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioFileUri != null) {

                    saveAudio();
                }
                mBtnRecord.setBackgroundResource(R.drawable.box_corner);
            }
        });
        findViewById(R.id.cancelaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioFileUri = null;
                audioLay.setVisibility(View.GONE);
                mBtnRecord.setBackgroundResource(R.drawable.box_corner);
            }
        });

        //registering OnClickListeners to implement outside onCreate()
        mBtnMsg.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        mBtnRecord.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera:
                cameraOnClick();
                break;
            case R.id.record:
                recordOnClick();
                break;
            case R.id.write_msg:
                writeMsgOnClick();
                break;
        }
    }

    /**
     * Checking for Dynamic permissions if Version > 23(Marshmallow) and result will CallBack to onRequestPermissionsResult()
     */
    private void cameraOnClick() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // requesting Dynamic permissions to continue if Version >23.
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // if Version <23 no need of Dynamic permissions to continue.
                cameraIntent();
            }
        } else {
            // Your app will  have the requested permission to continue.
            cameraIntent();
        }
    }

    /**
     * Checking for Dynamic permissions if Version > 23(Marshmallow) and result will CallBack to onRequestPermissionsResult()
     */
    private void recordOnClick() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // requesting Dynamic permissions to continue if Version >23.
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_AUDIO_CODE);
            } else {
                // if Version <23 no need of Dynamic permissions to continue.
                audioIntent();
            }
        } else {
            // Your app will  have the requested permission to continue.
            audioIntent();
        }
    }

    /**
     * Showing popup dialog on click of write msg with edittext and save button.
     * higlighting selected button using Drawable and hiding img,audio layouts
     */
    private void writeMsgOnClick() {
        showWriteMessageDialog();

        //highlighting selected option using drawables
        mBtnCamera.setBackgroundResource(R.drawable.box_corner);
        mBtnRecord.setBackgroundResource(R.drawable.box_corner);
        mBtnMsg.setBackgroundResource(R.drawable.option_selected_corner);

        //hiding content layouts of options
        imgLay.setVisibility(View.GONE);
        thumbnail = null;
        audioFileUri = null;
        audioLay.setVisibility(View.GONE);
    }

    //showing dialog on writeMsg onClick
    private void showWriteMessageDialog() {
        dialog = new Dialog(this, R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.write_message);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparency);
        final EditText message = (EditText) dialog.findViewById(R.id.message);
        Button btnSend = (Button) dialog.findViewById(R.id.send_btn);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageVal = message.getText().toString();
                /**
                 * validating msg is empty or not .
                 * if empty showing error msg with Toast.
                 * If not empty showing entered msg and dismiss dialog
                 */
                if (TextUtils.isEmpty(messageVal)) {
                    Config.customToast(HomeActivity.this, "Please enter message");
                } else {
                    Config.customToast(HomeActivity.this, messageVal);
                    mBtnMsg.setBackgroundResource(R.drawable.box_corner);
                    dialog.dismiss();

                }
            }
        });
        /**
         * callBack when dialog dismiss to dehighilght the option
         */
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                mBtnMsg.setBackgroundResource(R.drawable.box_corner);
            }
        });

        dialog.show();
    }

    /**
     * Intent for capturing audio and higlighting cameraOption using Drawable
     */
    private void audioIntent() {
        //highlighting selected option using drawables
        mBtnCamera.setBackgroundResource(R.drawable.box_corner);
        mBtnMsg.setBackgroundResource(R.drawable.box_corner);
        mBtnRecord.setBackgroundResource(R.drawable.option_selected_corner);

        //intent for recording audio
        Intent intent = new Intent(
                MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        audioFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, audioFileUri);
        startActivityForResult(intent, RECORD_REQUEST);

        //hiding content layouts of options
        imgLay.setVisibility(View.GONE);
        thumbnail = null;
        audioFileUri = null;
        audioLay.setVisibility(View.GONE);

    }

    /**
     * Intent for capturing image and higlighting cameraOption using Drawable
     */
    private void cameraIntent() {
        //highlighting selected option using drawables
        mBtnRecord.setBackgroundResource(R.drawable.box_corner);
        mBtnMsg.setBackgroundResource(R.drawable.box_corner);
        mBtnCamera.setBackgroundResource(R.drawable.option_selected_corner);

        //intent for capturiing image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name
        startActivityForResult(intent, REQUEST_CAMERA);

        //hiding content layouts of options
        imgLay.setVisibility(View.GONE);
        thumbnail = null;
        audioFileUri = null;
        audioLay.setVisibility(View.GONE);

    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set Message to show to user
        String msg = getResources().getString(R.string.logout);
        builder.setMessage(msg);

        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //  dismiss the dialog ,clearing sharedprefrence ,closing HomePage and launching login page
                SharedData.reset();
                Intent i1 = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i1);
                finish();
                dialog.dismiss();
            }

        });

        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing dismiss dialog
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    private void onCaptureImageResult(Intent data) {
        if (fileUri != null) {
            try {
                String path = Config.getFilePath(HomeActivity.this, fileUri);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                thumbnail = BitmapFactory.decodeFile(path, bmOptions);
                thumbnail = Bitmap.createScaledBitmap(thumbnail, 200, 200, true);
                if (thumbnail != null) {
                    selectedImg.setImageBitmap(thumbnail);
                    imgLay.setVisibility(View.VISIBLE);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }


    public void saveImage() {

        // reading data from thumbnail and saving to Devise Storage
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = Config.saveFinalImage(thumbnail);
        Config.customToast(HomeActivity.this, "Image saved in " + path);
        // clearing values and dehighlight option
        thumbnail = null;
        imgLay.setVisibility(View.GONE);

    }

    // reading data from audio uri and saving to Devise Storage
    public void saveAudio() {
        String audiopath = "";
        try {
            audiopath = Config.getFilePath(HomeActivity.this, audioFileUri);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File source = new File(audiopath);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".mp3");
        try {
            Config.copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioFileUri = null;
        audioLay.setVisibility(View.GONE);
        Config.customToast(HomeActivity.this, "Recording saved in " + destination);
        // deleting file from source path
        try {
            if (audiopath != null && audiopath.length() > 0) {
                File out = new File(audiopath);
                if (out.exists()) {
                    out.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                }
            case PERMISSION_REQUEST_AUDIO_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    audioIntent();
                }
                break;
        }
    }

    /**
     * Callback received after capturing image or audio.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // block after capturing image / recording audio successfull
            if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
            if (requestCode == RECORD_REQUEST) {
                onAudioCaptureResult(data);
            }
        } else {
            // block after capturing image / recording audio failure

            mBtnCamera.setBackgroundResource(R.drawable.box_corner);
            mBtnRecord.setBackgroundResource(R.drawable.box_corner);
        }

    }

    private void onAudioCaptureResult(Intent data) {
        if (data != null) {
            audioFileUri = data.getData();
            audioLay.setVisibility(View.VISIBLE);
        }
    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG" + timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }


}
