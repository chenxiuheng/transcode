package com.thunisoft.trascode.tasks.cli;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * web容器下  exe 执行者
 * 
 * <p>
 * <code>
 *   String cmd = "ping";
 *   ProcessExecutor executor = new ProcessExecutor(cmd);
 * 
 *   executor.start();
 *   executor.waitFor();
 * </code>
 * @since V1.0  2014-4-8
 * @author chenxh
 */
final public class ProcessExecutor implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(ProcessExecutor.class);
    private static ExecutorService service = Executors.newCachedThreadPool();


    private String cmd;
    private Process process;
    private Timer watchProcessOutputtimer;

    /**
     * 两次输出的最小间隔。
     * 
     * 对于没有输出的命令， timeout 应该不设置，或者设置成 exe 最大的运行时长。
     */
    private long timeout;

    private boolean interrupted = false;
    private long lastModified = -1;
    private OutputWatcherWrapper watcher;

    public ProcessExecutor(String cmd) {
        this(cmd, null);
    }

    public ProcessExecutor(String cmd, OutputWatcher watcher) {
        this.cmd = cmd;
        this.watcher = new OutputWatcherWrapper(watcher);
    }

    /**
     * @param milSeconds 
     * @since V1.0 2014-4-8
     * @author chenxh
     * @throws IllegalArgumentException if timeout <= 0
     */
    public void setTimeout(long milSeconds) {
        if (milSeconds <= 0) {
            throw new IllegalArgumentException("timeout must be positive!");
        }

        this.timeout = milSeconds;
    }

    public void setOutputWatcher(OutputWatcher watcher) {
        this.watcher.setWatcher(watcher);
    }

    public int startAndWait() throws IOException, InterruptedException {
        start();
        
        try {
            return waitFor();
        } finally {
            cancle();
        }
    }
    
    /**
     * 
     * @since V1.0 2014-4-8
     * @author chenxh
     * @throws IOException 
     */
    public void start() throws IOException {
        if (lastModified > 0) {
            throw new IllegalStateException(cmd + "已经开始运行");
        }
        
        // 启动 exe
        process = Runtime.getRuntime().exec(cmd);
        
        // 读取输出流
        // web 容器下，如果不主动读取的话， 会造成阻塞
        service.submit(new OutputStreamWatcher(process.getInputStream()));
        service.submit(new OutputStreamWatcher(process.getErrorStream()));

        // 通过监听输出， 判断 process 当前的活动状态
        // 如果没有设置 timeout， 则这个 watch 一直在空转。
        watchProcessOutputTimeout();
    }

    private void watchProcessOutputTimeout() {
        if (null != watchProcessOutputtimer) {
            watchProcessOutputtimer.cancel();
        }

        long delay = 1000;
        watchProcessOutputtimer = new Timer(true);
        watchProcessOutputtimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                long now = System.currentTimeMillis();

                boolean isTimeout =
                        (timeout > 0) && (lastModified > 0) && (now - lastModified > timeout);
                if (!isTimeout) {
                    logger.debug("正在运行[{}]....", cmd);
                } else if (!interrupted()){
                    logger.warn("运行超时，准备取消 [{}]!", cmd);
                    ProcessExecutor.this.cancle();
                }
            }
        }, delay, delay);
    }

    public int waitFor() throws InterruptedException {
        return process.waitFor();
    }

    public void cancle() {
        interrupted = true;

        try {
            if (null != process) {
                IOUtils.closeQuietly(process.getInputStream());
                IOUtils.closeQuietly(process.getErrorStream());

                process.destroy();
            }
        } finally {
            if (null != watchProcessOutputtimer) {
                watchProcessOutputtimer.cancel();
            }
        }
    }

    public boolean interrupted() {
        return interrupted;
    }
    
    @Override
    public void close() {
        cancle();
    }

    private final class OutputStreamWatcher implements Runnable {
        private BufferedReader reader;

        public OutputStreamWatcher(InputStream inStream) {
            this.reader = new BufferedReader(new InputStreamReader(inStream));
        }

        @Override
        public void run() {
            String line = null;

            try {
                while (!interrupted() && null != (line = reader.readLine())) {

                    // 程序最近更新时间
                    lastModified = System.currentTimeMillis();

                    watcher.onOutput(line);
                }
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }
}
