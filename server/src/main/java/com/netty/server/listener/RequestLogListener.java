package com.netty.server.listener;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 柳忠国
 * @date 2020-05-08 10:29
 */
@Slf4j
public class RequestLogListener implements ServletRequestListener {

    private static final ThreadLocal<RequestLog> REQUEST_LOG_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存放每个url请求中content-path中的最后一个值（学校id）
     * 比如: localhost:8080/wisdom/manage/device/airModel/online/school-1?id=xxx&name=xxx
     * 此时存入的是school-1
     */
    public static final ThreadLocal<String> SCHOOL_ID_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest servletRequest = sre.getServletRequest();
        if (servletRequest instanceof HttpServletRequest) {
            // 解析url的content-path，把路径中的最后一个schoolId放入到ThreadLocal中
            String pathVar = StringUtils.substringAfterLast(((RequestFacade) servletRequest).getRequestURI(), "/");
            SCHOOL_ID_THREAD_LOCAL.set(pathVar);
            String url = ((HttpServletRequest) servletRequest).getRequestURL().toString();
            String method = ((HttpServletRequest) servletRequest).getMethod();
            String queryString = ((RequestFacade) servletRequest).getQueryString();

            url = method + ": " + url;
            if (null != queryString) {
                url = url + "?" + queryString;
            }
            REQUEST_LOG_THREAD_LOCAL.set(new RequestLog(url, Stopwatch.createStarted()));
            if (log.isInfoEnabled()) {
                log.info("请求开始:{}", url);
            }
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        SCHOOL_ID_THREAD_LOCAL.remove();
        RequestLog requestLog = REQUEST_LOG_THREAD_LOCAL.get();
        if (null != requestLog) {
            String url = requestLog.getUrl();
            if (log.isInfoEnabled()) {
                log.info("请求结束:{}, 请求耗时【{}】", url, requestLog.getStopwatch().stop());
            }
            REQUEST_LOG_THREAD_LOCAL.remove();
        }
    }

}