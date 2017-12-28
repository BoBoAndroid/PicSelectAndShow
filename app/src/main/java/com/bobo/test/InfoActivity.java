package com.bobo.test;

import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bumptech.glide.Glide;

import java.io.IOException;

/**
 * Created by bobo on 2017/12/26.
 */

public class InfoActivity extends Activity {
    private String imgPath;
    private FrameLayout fl_back;
    private ImageView iv_show;
    private TextView tv_show,tv_addr;
    //搜索用 根据
    GeoCoder mSearch = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_info);
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(listener);
        initView();
        initEvent();
    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            }

            //获取地理编码结果
        }

        @Override

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }else {
                tv_addr.setText("地址："+result.getPoiList().get(0).address);
                Log.e("DDZTAG", "onGetReverseGeoCodeResult:" + result.getPoiList().get(0).address);
            }

            //获取反向地理编码结果
        }
    };



    private void initEvent() {
        fl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void analysInfo() {
        try {
            ExifInterface exifInterface = new ExifInterface(imgPath);
            String latitude=changeMethod(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            String longitude=changeMethod(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            if(TextUtils.isEmpty(latitude)||TextUtils.isEmpty(longitude)){
                Toast.makeText(InfoActivity.this,"对不起，该照片不支持分析",Toast.LENGTH_SHORT).show();

            }else {
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude))));
            }
            Log.e("DDZTAG", "analysInfo:" + latitude+","+longitude);
            tv_show.setText("日期："+exifInterface.getAttribute(ExifInterface.TAG_DATETIME)+"\n"+"纬度："+latitude+"\n经度："+longitude+"\n"+"海拔高度："
                    +exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)+"\n"+"拍摄设备："+exifInterface.getAttribute(ExifInterface.TAG_MODEL));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //度分秒的转换
    private String changeMethod(String attribute) {
        String result=null;
        if(!TextUtils.isEmpty(attribute)){
            String[] arrys=attribute.split(",");
            //先把秒转换成分
            String[] seconds=arrys[2].split("/");
            float second=(Float.parseFloat(seconds[0])/Float.parseFloat(seconds[1]))/60;
            //再把分转换成度
            String[] degrees=arrys[1].split("/");
            float degree=((Float.parseFloat(degrees[0])/Float.parseFloat(degrees[1]))+second)/60;
            //最后把度相加
            String[] results=arrys[0].split("/");
            float result1=(Float.parseFloat(results[0])/Float.parseFloat(results[1]))+degree;
            result=String.valueOf(result1);
        }
        return result;

    }

    private void initView() {
        imgPath=getIntent().getStringExtra(MainActivity.INTENT_EXTRA_IMGPATH);
        fl_back=findViewById(R.id.fl_back);
        iv_show=findViewById(R.id.iv_show);
        tv_show=findViewById(R.id.tv_show);
        tv_addr=findViewById(R.id.tv_addr);
        Glide.with(InfoActivity.this).load(imgPath).into(iv_show);
        analysInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearch.destroy();

    }
}
