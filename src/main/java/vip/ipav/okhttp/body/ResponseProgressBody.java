package vip.ipav.okhttp.body;


import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;
import vip.ipav.okhttp.response.DownloadResponseHandler;

import java.io.IOException;


public class ResponseProgressBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private DownloadResponseHandler mDownloadResponseHandler;
    private BufferedSource bufferedSource;

    public ResponseProgressBody(ResponseBody responseBody, DownloadResponseHandler downloadResponseHandler) {
        this.mResponseBody = responseBody;
        this.mDownloadResponseHandler = downloadResponseHandler;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {

            long totalBytesRead;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                //这个的进度应该是读取response每次内容的进度，在写文件进度之前 所以暂时以写完文件的进度为准
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += ((bytesRead != -1) ? bytesRead : 0);
//                if (mDownloadResponseHandler != null) {
//                    mDownloadResponseHandler.onProgress(totalBytesRead, mResponseBody.contentLength());
//                }
                return bytesRead;
            }
        };
    }
}
