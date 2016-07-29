package com.example.hirotoshin.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private Uri m_uri;
    private static final int REQUEST_CHOOSER = 1000;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int EMO = 999;
    private final int PHY = 998;
    private final int CUL = 997;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setbuttonListener();
        ImageButton stampbutton = (ImageButton)findViewById(R.id.imageButton);
        stampbutton.setImageResource(R.drawable.stampstamp2);
    }

    private void setbuttonListener() {
        Button button1 = (Button) findViewById(R.id.buttonPanel);
        Button button2 = (Button) findViewById(R.id.camera_button);
        Button save = (Button)findViewById(R.id.Savebutton);
        ImageButton stampbutton = (ImageButton)findViewById(R.id.imageButton);
        button1.setOnClickListener(button1_onClick);
        button2.setOnClickListener(button2_onClick);
        save.setOnClickListener(save_click);
        stampbutton.setOnClickListener(stampbutton_onclick);
    }

    private View.OnClickListener button1_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showGallery();
        }
    };

    private View.OnClickListener button2_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playCamera();
        }
    };

    private View.OnClickListener stampbutton_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            int requestcode = 666;
            startActivityForResult(intent, requestcode);
        }
    };

    private View.OnClickListener save_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            run();
        }
    };

    private void playCamera() {

        //カメラの起動Intentの用意
        String photoName = System.currentTimeMillis() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, photoName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        m_uri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);
        startActivityForResult( intentCamera, REQUEST_CHOOSER);

    }

    private void showGallery() {
        // ギャラリー用のIntent作成
        Intent intentGallery;
        if (Build.VERSION.SDK_INT < 19) {
            intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentGallery.setType("image/*");
        } else {
            intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
            intentGallery.setType("image/jpeg");
        }
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = Intent.createChooser(intentCamera, "画像の選択");
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentGallery});
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CHOOSER) {

                if (resultCode != RESULT_OK) {
                    // キャル時
                    return;
                }

                Uri resultUri = (data != null ? data.getData() : m_uri);

                if (resultUri == null) {
                    // 取得失敗
                    return;
                }

                // ギャラリーへスキャンを促す
                MediaScannerConnection.scanFile(
                        this,
                        new String[]{resultUri.getPath()},
                        new String[]{"image/jpeg"},

                        null
                );

                // 画像を設定
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageURI(resultUri);
            }
        if(requestCode == 666){
            if(resultCode == Activity.RESULT_OK){
                int flag = data.getIntExtra("stamp_number", -10);
                FrameLayout frame= (FrameLayout) findViewById(R.id.framelayout);
                FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(WC,WC);
                DragViewListener dvListener;
                switch (flag){
                    case 1:
                        int emo_id = View.generateViewId();
                        ImageView emotional = new ImageView(this);
                        emotional.setImageResource(R.drawable.stampemotional2);
                        emotional.setId(emo_id);
                        frame.addView(emotional, prams);
                        dvListener = new DragViewListener(emotional);
                        emotional.setOnTouchListener(dvListener);

                        break;
                    case 2:
                        int phy_id = View.generateViewId();
                        ImageView physical = new ImageView(this);
                        physical.setImageResource(R.drawable.stampphysical2);
                        physical.setId(phy_id);
                        frame.addView(physical, prams);
                        dvListener = new DragViewListener(physical);
                        physical.setOnTouchListener(dvListener);
                        break;
                    case 3:
                        int cul_id = View.generateViewId();
                        ImageView culture = new ImageView(this);
                        culture.setImageResource(R.drawable.stumpculture2);
                        culture.setId(cul_id);
                        frame.addView(culture, prams);
                        dvListener = new DragViewListener(culture);
                        culture.setOnTouchListener(dvListener);
                        break;
                }
            }
        }
    }

    public void run() {
        handler.post(new Runnable() {
            public void run() {
                final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                final Date date = new Date(System.currentTimeMillis());
                String filename = Environment.getExternalStorageDirectory() + "/HN Camera/" + df.format(date) + ".png";

                final File file = new File(filename);
                final File dir = new File(Environment.getExternalStorageDirectory() + "/HN Camera/");

                if (!dir.exists()) {
                    boolean result = dir.mkdirs();
                    System.out.println(result);
                    dir.mkdirs();
                }
                file.getParentFile().mkdir();
                saveCapture(findViewById(android.R.id.content), file);
                Toast.makeText(MainActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                String[] filePath = {filename};
                String[] mimeType = {"image/*"};
                MediaScannerConnection.scanFile(getApplicationContext(), filePath, mimeType, null);
            }
        });
    }

    private void createFolderSaveImage(Bitmap imageToSave, String fileName) {
        String folderPath = Environment.getExternalStorageDirectory() + "/NewFolder/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public void saveCapture(View view, File file) {
        Bitmap capture = getViewCapture(view);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            capture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            final Date date = new Date(System.currentTimeMillis());
            registAndroidDB("/strage/emulated/O/Pictures/" + df.format(date) + ".png");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos == null) return;
            try {
                fos.close();
            } catch (Exception ie) {
                fos = null;
            }
        }
    }

    private void registAndroidDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "ge/jpeg");
        values.put("_date", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    public Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if (cache == null) return null;
        Bitmap screen_shot = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        ImageView imageview = (ImageView) findViewById(R.id.imageView1);
        imageview.setImageBitmap(screen_shot);
        return screen_shot;
    }

}