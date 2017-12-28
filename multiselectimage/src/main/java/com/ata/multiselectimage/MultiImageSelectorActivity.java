package com.ata.multiselectimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ata.multiselectimage.utils.FileProvider7;
import com.ata.multiselectimage.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.ata.multiselectimage.utils.FileUtils.judgeRollImg;

public class MultiImageSelectorActivity extends AppCompatActivity implements MultiImageSelectorFragment.Callback{

    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;

    /** Max image size，int，{@link #DEFAULT_IMAGE_SIZE} by default */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** Select mode，{@link #MODE_MULTI} by default */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** Whether show camera，true by default */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**是否是直接拍照操作*/
    public static final String EXTRA_TAKE_PHOTO="take_photo";
    /**用户自定义存图片的文件路径*/
    public static final String EXTRA_PHOTO_FILE="photo_file";
    /** Result data set，ArrayList&lt;String&gt;*/
    public static final String EXTRA_RESULT = "select_result";
    /** Original data set */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    // Default image size
    private static final int DEFAULT_IMAGE_SIZE = 9;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount = DEFAULT_IMAGE_SIZE;
    boolean isTakePhoto=false;
    private String filePath=null;
    private boolean isShow=true;
    Toolbar toolbar;
    private int mode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.mis_activity_default);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
        mode= intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        isTakePhoto=intent.getBooleanExtra(EXTRA_TAKE_PHOTO,false);
        filePath=intent.getStringExtra(EXTRA_PHOTO_FILE);

        mSubmitButton = (Button) findViewById(R.id.commit);
        if(mode == MODE_MULTI){
            updateDoneText(resultList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(resultList != null && resultList.size() >0){
                       /* for (int i = 0; i < resultList.size(); i++) {
                            String imgName=resultList.get(i).substring(resultList.get(i).lastIndexOf("/")+1,resultList.get(i).length());
                            String imgPath= FileUtils.loadBitmap(resultList.get(i), true,imgName);
                            int location = resultList.indexOf(resultList.get(i));
                            resultList.remove(resultList.get(i));
                            resultList.add(location, imgPath);
                        }*/
                        ArrayList<String> rollPath=new ArrayList<String>();
                        for(String path:resultList){
                            if(judgeRollImg(path)){
                                rollPath.add(path);
                            }
                        }

                        if(rollPath.size()>0){
                            for (int i = 0; i < rollPath.size(); i++) {
                                String imgName=rollPath.get(i).substring(rollPath.get(i).lastIndexOf("/")+1,rollPath.get(i).length());
                                String resultPath=FileUtils.haveImage(imgName);
                                if(TextUtils.isEmpty(resultPath)) {
                                    //没有被旋转过的情况
                                    Bitmap bitmap = FileUtils.loadBitmap(rollPath.get(i), true);
                                    try {
                                        String imgPath = FileUtils.storeImage(bitmap, imgName);
                                        if (!TextUtils.isEmpty(imgPath)) {
                                            int location = resultList.indexOf(rollPath.get(i));
                                            resultList.remove(rollPath.get(i));
                                            resultList.add(location, imgPath);
                                            //删除原来的照片
                                            // FileUtils.deleteFile(rollPath.get(i));
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    //以前被旋转过的情况
                                    int location = resultList.indexOf(rollPath.get(i));
                                    resultList.remove(rollPath.get(i));
                                    resultList.add(location, resultPath);
                                    //删除原来的照片
                                    // FileUtils.deleteFile(rollPath.get(i));
                                }
                            }
                        }
                            Intent data = new Intent();
                            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                            setResult(RESULT_OK, data);

                    }else{
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        }else{
            mSubmitButton.setVisibility(View.GONE);
        }

        /*if(savedInstanceState == null){
            Bundle bundle = new Bundle();
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_TAKE_PHOTO,isTakePhoto);
            if(!TextUtils.isEmpty(filePath)){
               bundle.putString(MultiImageSelectorFragment.EXTRA_PHOTO_PATH,filePath);
            }
            bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                    .commit();
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始判断权限
        if(!hasPermission(MultiImageSelectorActivity.this)) {
            toolbar.setVisibility(View.GONE);
            Toast.makeText(MultiImageSelectorActivity.this, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
            requestPermission(MultiImageSelectorActivity.this);
        }else {
                toolbar.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
                bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
                bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
                bundle.putBoolean(MultiImageSelectorFragment.EXTRA_TAKE_PHOTO,isTakePhoto);
                if(!TextUtils.isEmpty(filePath)){
                    bundle.putString(MultiImageSelectorFragment.EXTRA_PHOTO_PATH,filePath);
                }
                bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                        .commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Update done button by select image data
     * @param resultList selected image data
     */
    private void updateDoneText(ArrayList<String> resultList){
        int size = 0;
        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText(R.string.mis_action_done);
            mSubmitButton.setEnabled(false);
        }else{
            size = resultList.size();
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setText(getString(R.string.mis_action_button_string,
                getString(R.string.mis_action_done), size, mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {
            // notify system the image has change
            Uri uri=FileProvider7.getUriForFile(this, imageFile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if(judgeRollImg(imageFile.getAbsolutePath())) {
                Bitmap bitmap = FileUtils.loadBitmap(imageFile.getAbsolutePath(), true);
                try {
                    String imgPath = FileUtils.storeImage(bitmap, null);
                    if (!TextUtils.isEmpty(imgPath)) {
                        Intent data = new Intent();
                        resultList.add(imgPath);
                        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                        setResult(RESULT_OK, data);
                        finish();
                        //FileUtils.deleteFile(imageFile.getAbsolutePath());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else {
                Intent data = new Intent();
                resultList.add(imageFile.getAbsolutePath());
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, data);
                finish();
            }
        }else {
            finish();
        }
    }
    private void requestPermission(Activity activity) {
        if (isShow) {
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MultiImageSelector.REQUEST_GET_ACCOUNT);
            }else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, MultiImageSelector.REQUEST_GET_ACCOUNT);

            }
        }else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, MultiImageSelector.REQUEST_GET_ACCOUNT);

        }
    }

    private boolean hasPermission(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            // Permission was added in API Level 16
            if(isShow){
                return ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED));
            }else {
                return ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED));
            }
        }
        return true;
    }
}
