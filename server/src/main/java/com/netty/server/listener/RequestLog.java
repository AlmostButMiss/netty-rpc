package com.netty.server.listener;

import com.google.common.base.Stopwatch;
import lombok.Data;

/**
 * @author : liuzg
 * @description todo
 * @date : 2020-10-22 15:05
 * @since 1.0
 **/
@Data
public class RequestLog {
    private String url;
    private Stopwatch stopwatch;

    public RequestLog() {
    }

    public RequestLog(String url, Stopwatch stopwatch) {
        this.url = url;
        this.stopwatch = stopwatch;
    }
}
