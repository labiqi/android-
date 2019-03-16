package com.example.lcq.drawable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lcq.drawable.Utils.FileInfo;
import com.example.lcq.drawable.services.DownloadService;

import java.util.List;

public class FileListAdapter extends BaseAdapter {
    private Context context;
    private List<FileInfo> list;
    public FileListAdapter(Context context,List<FileInfo> fileInfos) {
        this.context =context;
        this.list = fileInfos;
    }

    @Override
    public int getCount() {
        return list.size();
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
        ViewHolder viewHolder = null;
        final FileInfo fileInfo = list.get(i);
        if(view == null) {
           view = LayoutInflater.from(context).inflate(R.layout.list_item,null);
           viewHolder = new ViewHolder();
           viewHolder.textView = view.findViewById(R.id.tvFileName);
           viewHolder.btStart = view.findViewById(R.id.btStart);
            viewHolder.btStop = view.findViewById(R.id.btStop);
            viewHolder.progressBar = view.findViewById(R.id.pbProgress);
            viewHolder.textView.setText(fileInfo.getFileName());
            viewHolder.progressBar.setMax(100);
            viewHolder.btStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("fileInfo",fileInfo);
                    context.startService(intent);
                }
            });
            viewHolder.btStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,DownloadService.class);
                    intent.setAction(DownloadService.ACTION_STOP);
                    intent.putExtra("fileInfo",fileInfo);
                    context.startService(intent);
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.progressBar.setProgress(fileInfo.getFinished());


        return view;
    }

    /*
    更新列表项中的进度条
     */
    public void updateProgress(int id,int progress) {
        FileInfo fileInfo = list.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView textView;
        Button btStart,btStop;
        ProgressBar progressBar;

    }
}
