package com.example.lcq.drawable.db;

import com.example.lcq.drawable.Utils.ThreadInfo;

import java.util.List;

/**
 * 数据库访问接口方法

 */
public interface ThreadDao {
    public void insertThread(ThreadInfo threadInfo);

    public void deleteThread(String url);

    public void updateThread(String url,int thread_id, int finished);

    public List<ThreadInfo> getThreads(String url);

    public boolean isexit(String url,int thread_id);
}
