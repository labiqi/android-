package com.example.lcq.drawable;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcq.drawable.Utils.FileInfo;
import com.example.lcq.drawable.services.DownloadService;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

//@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

//    @ViewById(R.id.tvFileName)
//    TextView tvFileName;
//    @ViewById(R.id.pbProgress)
//    ProgressBar pbProgress;
//    @ViewById(R.id.btStop)
//    Button btStop;
//    @ViewById(R.id.btStart)
//    Button btStart;

    private ListView listView;
    private List<FileInfo> fileInfoList;
    private FileListAdapter fileListAdapter;


    private FileInfo fileInfo;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        fileInfoList = new ArrayList<FileInfo>();
        fileInfo = new FileInfo(0,"http://public.fzzqxf.com/dailybuild/xiaofang2/android/hs-ipo-optimize-02/2019-03-12_20:03:12/xm_dev_v7.18.0_flavorstest_03_12_19_51.apk","kuwo apk",0,0);
        FileInfo  fileInfo1 = new FileInfo(1,"http://public.fzzqxf.com/dailybuild/xiaofang2/android/develop/2019-03-14_16:49:00/xf_dev_v7.18.0_flavorstest_03_14_16_38.apk","immok apk",0,0);
        FileInfo fileInfo2 = new FileInfo(2,"http://public.fzzqxf.com/dailybuild/xiaofang2/android/feature/5tiger/simtrade_futures/2018-11-01_21:00:53/xf_dev_v7.13.0_flavorstest_11_01_20_54.apk","xiaofang apk",0,0);
        FileInfo fileInfo3 = new FileInfo(3,"http://public.fzzqxf.com/dailybuild/xiaofang2/android/feature/5tiger/simtrade_futures/2018-11-01_21:00:53/xf_dev_v7.13.0_flavorstest_11_01_20_54.apk","hello apk",0,0);
        FileInfo fileInfo4 = new FileInfo(4,"http://public.fzzqxf.com/dailybuild/xiaofang2/android/feature/5tiger/simtrade_futures/2018-11-01_21:00:53/xf_dev_v7.13.0_flavorstest_11_01_20_54.apk","heheh apk",0,0);
        fileInfoList.add(fileInfo);
        fileInfoList.add(fileInfo1);
        fileInfoList.add(fileInfo2);
        fileInfoList.add(fileInfo3);
        fileInfoList.add(fileInfo4);
        fileListAdapter = new FileListAdapter(this,fileInfoList);
        listView.setAdapter(fileListAdapter);
        //创建文件信息对象
        checkPermission();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISH);
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

//    @Click(R.id.btStart)
//    public void setBtStart() {
//        Intent intent = new Intent(MainActivity.this,DownloadService.class);
//        intent.setAction(DownloadService.ACTION_START);
//        intent.putExtra("fileInfo",fileInfo);
//        tvFileName.setText(fileInfo.getFileName());
//        startService(intent);
//    }
//
//    @Click(R.id.btStop)
//    public void setBtStop() {
//        Intent intent = new Intent(MainActivity.this,DownloadService.class);
//        intent.setAction(DownloadService.ACTION_STOP);
//        intent.putExtra("fileInfo",fileInfo);
//        startService(intent);
//    }

    BroadcastReceiver broadcastReceiver=  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                int id = intent.getIntExtra("id",0);
                //更新进度条
                int finished = intent.getIntExtra("finished",0 );
                fileListAdapter.updateProgress(id,finished);
//                pbProgress.setProgress(finished);
            } else if(DownloadService.ACTION_FINISH.equals(intent.getAction())) {
                //下载结束
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                fileListAdapter.updateProgress(fileInfo.getId(),0);
                Toast.makeText(context,fileInfoList.get(fileInfo.getId()).getFileName() +"x下载完毕",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /*
    检查是否开通文件读写权限
     */
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }

}
