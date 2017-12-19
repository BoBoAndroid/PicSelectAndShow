package com.ata.multiselectimage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.ata.multiselectimage.adapter.ImageDetailAdapter;

import java.util.ArrayList;

/**
 * Created by bobo on 2017/12/19.
 */

public class ImageDetailActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_PATHS = "image_paths";
    public static final String EXTRA_IMAGE_SELECTED="image_selected";
    private ArrayList<String> imgPaths;
    private String selectedPath;
    private ImageDetailAdapter adapter;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.activity_image_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        initIntent();
        initView();
    }

    private void initView() {
        viewPager= (ViewPager) findViewById(R.id.vp_image);
        if(imgPaths!=null&&imgPaths.size()>0){
            adapter=new ImageDetailAdapter(ImageDetailActivity.this,imgPaths);
            viewPager.setAdapter(adapter);
        }else {
            Toast.makeText(ImageDetailActivity.this,"数据源为空",Toast.LENGTH_SHORT).show();
        }
        if(!TextUtils.isEmpty(selectedPath)){
            for (int i = 0; i < imgPaths.size(); i++) {
                if(selectedPath.equals(imgPaths.get(i))){
                    viewPager.setCurrentItem(i);
                }
            }
        }
    }

    private void initIntent() {
        Intent intent=getIntent();
        imgPaths=intent.getStringArrayListExtra(EXTRA_IMAGE_PATHS);
        selectedPath=intent.getStringExtra(EXTRA_IMAGE_SELECTED);

    }
}
