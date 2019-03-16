package com.example.lcq.drawable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcq.drawable.Utils.FileInfo;
import com.example.lcq.drawable.services.DownloadService;
import com.example.lcq.drawable.services.SimpleDownLoadServices;

public class SimpleDownActivity extends Activity {

    public final static String LOAD_START = "LOAD_START";
    public final static String LOAD_STOP = "LOAD_STOP";

    private TextView tvFileName;
    private Button btStop;
    private Button btStart;
    private ProgressBar pbProgress;
    private FileInfo fileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        fileInfo = new FileInfo(0,"http://public.fzzqxf.com/dailybuild/xiaofang2/android/hs-ipo-optimize-02/2019-03-12_20:03:12/xm_dev_v7.18.0_flavorstest_03_12_19_51.apk","XF app",0,0);
        initView();
        tvFileName.setText(fileInfo.getFileName());
        pbProgress.setMax(100);
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleDownActivity.this,SimpleDownLoadServices.class);
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(LOAD_STOP);
                startService(intent);
            }

        });

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleDownActivity.this,SimpleDownLoadServices.class);
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(LOAD_START);
                startService(intent);
            }
        });

        Button more = findViewById(R.id.more_thread_load);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SimpleDownActivity.this,MainActivity.class));
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISH);
        registerReceiver(broadcastReceiver,filter);
    }

    private void initView() {
        tvFileName = findViewById(R.id.tvFileName);
        btStart = findViewById(R.id.btStart);
        btStop = findViewById(R.id.btStop);
        pbProgress = findViewById(R.id.pbProgress);
    }

    BroadcastReceiver broadcastReceiver=  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                int id = intent.getIntExtra("id",0);
                //更新进度条
                int finished = intent.getIntExtra("finished",0 );
                pbProgress.setProgress(finished);
                tvFileName.setText(fileInfo.getFileName() + "下载完成" + String.valueOf(finished) + "%");
                if(finished == 100) {
                    Toast.makeText(context,fileInfo.getFileName() +"x下载完毕",Toast.LENGTH_SHORT).show();
                }
            } else if(DownloadService.ACTION_FINISH.equals(intent.getAction())) {
                //下载结束
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                Toast.makeText(context,fileInfo.getFileName() +"x下载完毕",Toast.LENGTH_SHORT).show();
            }
        }
    };

}
