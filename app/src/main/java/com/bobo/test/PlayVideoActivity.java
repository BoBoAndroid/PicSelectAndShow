package com.bobo.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

/**
 * Created by bobo on 2018/3/14.
 */

public class PlayVideoActivity extends Activity implements View.OnClickListener{
    public static String VIDEO_PATH="video_path";
    private ScalableVideoView mScalableVideoView;
    private ImageView mPlayImageView;
    private ImageView mThumbnailImageView;
    private String filePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        filePath = getIntent().getStringExtra(VIDEO_PATH);
        initView();
    }

    private void initView() {
        mScalableVideoView = (ScalableVideoView) findViewById(R.id.video_view);
        try {
            // 这个调用是为了初始化mediaplayer并让它能及时和surface绑定
            mScalableVideoView.setDataSource("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayImageView = (ImageView) findViewById(R.id.playImageView);
        mThumbnailImageView = (ImageView) findViewById(R.id.thumbnailImageView);
        Glide.with(PlayVideoActivity.this).load(filePath).into(mThumbnailImageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_view:

                    mScalableVideoView.stop();
                    mPlayImageView.setVisibility(View.VISIBLE);
                    mThumbnailImageView.setVisibility(View.VISIBLE);

                break;
            case R.id.playImageView:
                try {
                    mScalableVideoView.setDataSource(filePath);
                    mScalableVideoView.setLooping(true);
                    mScalableVideoView.prepare();
                    mScalableVideoView.start();
                    mPlayImageView.setVisibility(View.GONE);
                    mThumbnailImageView.setVisibility(View.GONE);
                } catch (IOException e) {
                    Log.e("TAG", e.getLocalizedMessage());
                    Toast.makeText(this, "播放视频异常", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_video:
                finish();

                break;
        }
    }
}
