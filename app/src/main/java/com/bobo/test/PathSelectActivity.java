package com.bobo.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobo on 2018/2/27.
 */

public class PathSelectActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout ll_names;
    ListView lv_show;
    ArrayList<BaseInfo> list=new ArrayList<>();
    ArrayList<String> listPath=new ArrayList<>();
    LvAdapter lvAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.activity_image_path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        initView();
        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.action_ok:
                    if(ll_names.getChildCount()>0) {
                        PositionView positionView = (PositionView) ll_names.getChildAt(ll_names.getChildCount() - 1);
                        String path = positionView.getPath();
                        Intent intent=new Intent();
                        intent.putExtra(MainActivity.INTENT_EXTRA_IMGPATH,path);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    break;
            }
            return true;
        }
    };

    private void initEvent() {
        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(list.get(i).type==0) {
                    final PositionView positionView = new PositionView(PathSelectActivity.this);
                    positionView.refreshView(list.get(i));
                    positionView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int count=ll_names.getChildCount();
                            int poi=0;
                            for (int i = 0; i < count; i++) {
                                PositionView positionView1= (PositionView) ll_names.getChildAt(i);
                                if(positionView1.getPath().equals(positionView.getPath())){
                                    scanFile(new File(positionView.getPath()));
                                    poi=i;
                                }
                            }
                            for (int i = ll_names.getChildCount() - 1; i >poi ; i--) {
                                ll_names.removeViewAt(i);
                            }
                        }
                    });
                    ll_names.addView(positionView);
                    //遍历文件夹

                    scanFile(new File(list.get(i).path));

                }
            }
        });
    }

    /**
     * 点击listview的item更新listview的内容方法
     * */
    private void scanFile(File file) {
        list.clear();
        if (file.exists()) {
            File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {
                    if(files[i].isDirectory()){
                        BaseInfo baseInfo=new BaseInfo();
                        baseInfo.type=0;
                        baseInfo.name=files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("/")+1,files[i].getAbsolutePath().length());
                        baseInfo.path=files[i].getAbsolutePath();
                        list.add(baseInfo);
                    }
            }
        }else {
            Toast.makeText(PathSelectActivity.this,"文件不存在",Toast.LENGTH_SHORT).show();
        }
        lvAdapter.notifyDataSetChanged();
      }

    private void initView() {
        ll_names= (LinearLayout) findViewById(R.id.ll_names);
        lv_show= (ListView) findViewById(R.id.lv_show);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setTitle("路径选择");

            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        //开始查找路径
        listPath=new ArrayList<>();
        listPath= (ArrayList<String>) getExtSDCardPathList();
        for (int i = 0; i < listPath.size(); i++) {
            if("/storage/emulated/0".equals(listPath.get(i))){
                BaseInfo baseInfo=new BaseInfo();
                baseInfo.type=0;
                baseInfo.name="内部存储";
                baseInfo.path="/storage/emulated/0";
                list.add(baseInfo);
            }else {
                BaseInfo baseInfo=new BaseInfo();
                baseInfo.type=0;
                baseInfo.name=listPath.get(i).substring(listPath.get(i).lastIndexOf("/")+1,listPath.get(i).length());
                baseInfo.path=listPath.get(i);
                list.add(baseInfo);
            }
        }
        lvAdapter=new LvAdapter();
        lv_show.setAdapter(lvAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                if(ll_names.getChildCount()>0) {
                    ll_names.removeViewAt(ll_names.getChildCount() - 1);
                }
                if(ll_names.getChildCount()>0){
                    PositionView positionView= (PositionView) ll_names.getChildAt(ll_names.getChildCount()-1);
                    scanFile(new File(positionView.getPath()));
                }else {
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class BaseInfo{
        public int type;
        public String name;
        public String path;
    }

    public class LvAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size()==0?0:list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder=null;
            if(view==null){
                viewHolder=new ViewHolder();
                view= LayoutInflater.from(PathSelectActivity.this).inflate(R.layout.item_image_path,null);
                viewHolder.tv_show=view.findViewById(R.id.tv_show);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            if(list.get(i).type==0){
                viewHolder.tv_show.setTextColor(Color.RED);
            }else {
                viewHolder.tv_show.setTextColor(Color.BLACK);
            }
            viewHolder.tv_show.setText(list.get(i).name);
            return view;
        }

        public class ViewHolder{
            TextView tv_show;
        }
    }


    /**
     * 获取外置SD卡路径以及TF卡的路径
     * <p>
     * 返回的数据：paths.get(0)肯定是外置SD卡的位置，因为它是primary external storage.
     *
     * @return 所有可用于存储的不同的卡的位置，用一个List来保存
     */
    public static List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                //扩展存储卡即TF卡或者SD卡路径
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    @Override
    public void onBackPressed() {
        if(ll_names.getChildCount()>0) {
            ll_names.removeViewAt(ll_names.getChildCount() - 1);
        }
        if(ll_names.getChildCount()>0){
            PositionView positionView= (PositionView) ll_names.getChildAt(ll_names.getChildCount()-1);
            scanFile(new File(positionView.getPath()));
        }else {
            super.onBackPressed();
        }
    }
}
