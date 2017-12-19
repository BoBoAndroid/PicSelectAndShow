package com.ata.multiselectimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hakeezfg on 2016/8/20.
 */
public class MultiImageSelector{

    public static final String EXTRA_RESULT = MultiImageSelectorActivity.EXTRA_RESULT;

    private boolean mShowCamera = true;
    private int mMaxCount = 9;
    private int mMode = MultiImageSelectorActivity.MODE_MULTI;
    private ArrayList<String> mOriginData;
    private static MultiImageSelector sSelector;
    private boolean isTakePhoto=false;
    private File file;
    @Deprecated
    private MultiImageSelector(Context context){

    }

    private MultiImageSelector(){}

    @Deprecated
    public static MultiImageSelector create(Context context){
        if(sSelector == null){
            sSelector = new MultiImageSelector(context);
        }
        return sSelector;
    }

    public static MultiImageSelector create(){
        if(sSelector == null){
            sSelector = new MultiImageSelector();
        }
        return sSelector;
    }

    public MultiImageSelector showCamera(boolean show){
        mShowCamera = show;
        return sSelector;
    }

    public MultiImageSelector count(int count){
        mMaxCount = count;
        return sSelector;
    }

    public MultiImageSelector single(){
        mMode = MultiImageSelectorActivity.MODE_SINGLE;
        isTakePhoto=false;
        return sSelector;
    }

    public MultiImageSelector multi(){
        mMode = MultiImageSelectorActivity.MODE_MULTI;
        isTakePhoto=false;
        return sSelector;
    }

    public MultiImageSelector origin(ArrayList<String> images){
        mOriginData = images;
        return sSelector;
    }

    public MultiImageSelector takePhoto(boolean flag){
        mMode = MultiImageSelectorActivity.MODE_SINGLE;
        isTakePhoto=flag;
        return sSelector;
    }

    public MultiImageSelector registerFile(File file){
        this.file=file;
        return sSelector;
    }


    public void startImageShow(Activity activity,ArrayList<String> imagePath,String imgSelected){
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putStringArrayListExtra(ImageDetailActivity.EXTRA_IMAGE_PATHS,imagePath);
        intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_SELECTED,imgSelected);
        activity.startActivity(intent);
    }

    public void start(Activity activity, int requestCode){
        final Context context = activity;
        if(hasPermission(context)) {
            activity.startActivityForResult(createIntent(context), requestCode);
        }else{
            Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    public void start(Fragment fragment, int requestCode){
        final Context context = fragment.getContext();
        if(hasPermission(context)) {
            fragment.startActivityForResult(createIntent(context), requestCode);
        }else{
            Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermission(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            // Permission was added in API Level 16
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private Intent createIntent(Context context){
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if(mOriginData != null){
            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, mMode);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_TAKE_PHOTO,isTakePhoto);
        if(file!=null){
            intent.putExtra(MultiImageSelectorActivity.EXTRA_PHOTO_FILE,file.getAbsolutePath());
        }
        return intent;
    }
}
