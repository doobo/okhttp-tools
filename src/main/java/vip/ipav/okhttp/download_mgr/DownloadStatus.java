package vip.ipav.okhttp.download_mgr;

/**
 * Created by tsy on 2016/11/25.
 */

public interface DownloadStatus {
    static final int STATUS_DEFAULT = -1;        //初始状态
    static final int STATUS_WAIT = 0;            //队列等待中
    static final int STATUS_PAUSE = 1;           //暂停
    static final int STATUS_DOWNLOADING = 2;     //下载中
    static final int STATUS_FINISH = 3;          //下载完成
    static final int STATUS_FAIL = 4;            //下载失败
}
