package com.bobo.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ata.multiselectimage.MultiImageSelector;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PHOTO_IMAGE = 2;
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
                MultiImageSelector.create().startImageShow(MainActivity.this,lstPhotoPath,lstPhotoPath.get(i));
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_single:
                MultiImageSelector.create().single().showCamera(true).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_single_no:
                MultiImageSelector.create().single().showCamera(false).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_more:
                MultiImageSelector.create().multi().showCamera(true).origin(lstPhotoPath).count(6).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_more_no:
                MultiImageSelector.create().multi().showCamera(false).origin(lstPhotoPath).count(6).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_take_photo:
                MultiImageSelector.create().takePhoto(true).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_takephoto_path:
                File appDir =new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TestPic");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }

                String fileName = System.currentTimeMillis() + ".jpg";

                File file1 = new File(appDir, fileName);
                MultiImageSelector.create().takePhoto(true).registerFile(file1).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
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

