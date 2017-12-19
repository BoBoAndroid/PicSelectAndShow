package com.bobo.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ata.multiselectimage.MultiImageSelector;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PHOTO_IMAGE = 2;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private ArrayList<String> lstPhotoPath=new ArrayList<>();
    private GridViewAdapter adapter;
    GridView grid_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        grid_show= (GridView) findViewById(R.id.grid_show);
        grid_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MultiImageSelector.create(MainActivity.this).startImageShow(MainActivity.this,lstPhotoPath,lstPhotoPath.get(i));
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_single:
                MultiImageSelector.create(MainActivity.this).single().start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_more:
                MultiImageSelector.create(MainActivity.this).multi().origin(lstPhotoPath).count(6).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_take_photo:
                MultiImageSelector.create(MainActivity.this).takePhoto(true).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_takephoto_path:
                File appDir =new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TestPic");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }

                String fileName = System.currentTimeMillis() + ".jpg";

                File file1 = new File(appDir, fileName);
                Log.e("DDZTAG", "onClick:这个文件的名字" + file1.getAbsolutePath());
                MultiImageSelector.create(MainActivity.this).takePhoto(true).registerFile(file1).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PHOTO_IMAGE){
            if(resultCode == RESULT_OK){
                ArrayList<String> paths=new ArrayList<>();
                paths = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                lstPhotoPath.addAll(paths);
                adapter=new GridViewAdapter(MainActivity.this,lstPhotoPath,5);
                grid_show.setAdapter(adapter);
            }
        }
    }
}

