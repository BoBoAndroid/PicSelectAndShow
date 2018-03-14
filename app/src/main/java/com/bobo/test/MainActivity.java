package com.bobo.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ata.multiselectimage.MultiImageSelector;

import java.io.File;
import java.util.ArrayList;



public class MainActivity extends Activity {
    private static final int REQUEST_PHOTO_IMAGE = 2;
    private static final int REQUEST_CODE_IMAGE_PATH=3;
    public static final String INTENT_EXTRA_IMGPATH="image_path";
    private ArrayList<String> lstPhotoPath=new ArrayList<>();
    private GridViewAdapter adapter;
    GridView grid_show;
    TextView tv_tip;
    private String image_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        grid_show= (GridView) findViewById(R.id.grid_show);
        tv_tip=findViewById(R.id.tv_tip);
        grid_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /**
                 * 点击预览图片
                 * lstPhotoPath:   要预览的数据源
                 * lstPhotoPath.get(i)：当前点击的图片地址
                 * */
                if(lstPhotoPath.get(i).endsWith("mp4")||lstPhotoPath.get(i).endsWith("3gp")){
                    Intent intent=new Intent(MainActivity.this,PlayVideoActivity.class);
                    intent.putExtra(PlayVideoActivity.VIDEO_PATH,lstPhotoPath.get(i));
                    startActivity(intent);
                }else {
                    MultiImageSelector.create().startImageShow(MainActivity.this, lstPhotoPath, lstPhotoPath.get(i));
                }
            }
        });
        grid_show.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,InfoActivity.class);
                intent.putExtra(INTENT_EXTRA_IMGPATH,lstPhotoPath.get(i));
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lstPhotoPath.size()>0){
            tv_tip.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_single:
                /**
                 * 调用选单张图片的方法
                 * .single():表明是单选
                 * .showCamera(true):选择界面含有拍照功能,false 选择界面不包含拍照只是选择图片
                 * .start():跳转
                 * */
                MultiImageSelector.create().single().showCamera(true).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_single_no:
                /**
                 *选单张图片不包含拍照功能的方法调用
                 * */
                MultiImageSelector.create().single().showCamera(false).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_more:
                /**
                 * 调用选多张图片的方法
                 * .multi():表明是多选
                 * .origin(lstPhotoPath):如果已经选过图片了，把选过的数据源带进去
                 * .count():一次最多选择几张图片
                 * */
                MultiImageSelector.create().multi().showCamera(true).origin(lstPhotoPath).count(6).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_more_no:
                /**
                 * 调用选多张图片不带拍照功能的方法
                 * */
                MultiImageSelector.create().multi().showCamera(false).origin(lstPhotoPath).count(6).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_take_photo:
                /**
                 * 调用拍照功能，直接调起拍照，不指定存储路径
                 * */
                MultiImageSelector.create().takePhoto(true).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
                break;
            case R.id.bt_takephoto_path:
                /**
                 * 调用拍照功能，直接调起拍照，指定图片存储路径
                 * */
                /*File appDir =new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TestPic");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }

                String fileName = System.currentTimeMillis() + ".jpg";

                File file1 = new File(appDir, fileName);
               MultiImageSelector.create().takePhoto(true).registerFile(file1).start(MainActivity.this,REQUEST_PHOTO_IMAGE);*/
                Intent intent=new Intent(MainActivity.this,PathSelectActivity.class);
                startActivityForResult(intent,REQUEST_CODE_IMAGE_PATH);
                break;
            case R.id.bt_select_video:
                MultiImageSelector.create().selectVideo(true).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
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
        }else if(requestCode==REQUEST_CODE_IMAGE_PATH){
            if(resultCode==RESULT_OK){
                image_path=data.getStringExtra(INTENT_EXTRA_IMGPATH);
                String fileName = System.currentTimeMillis() + ".jpg";
                File appDir=new File(image_path);
                File file1 = new File(appDir, fileName);
                MultiImageSelector.create().takePhoto(true).registerFile(file1).start(MainActivity.this,REQUEST_PHOTO_IMAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MultiImageSelector.REQUEST_GET_ACCOUNT){

        }
    }


}

