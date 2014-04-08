package com.thunisoft.trascode.tasks;

/**
 * 任务状态
 *
 * @since V1.0  2014-4-8
 * @author chenxh
 */
public class TaskState {
    /***
     * 等待运行
     */
    public static final int WAITING = 0;
    
    /**
     * 正在运行
     */
    public static final int RUNNING = 1;
    
    /**
     * 运行完成
     */
    public static final int FINISHED = 2;

    /**
     * @see #WAITING
     * @see #RUNNING
     * @see #FINISHED
     */
    private int state;

    /**
     * 当前的进度
     */
    private double progress;
    
    public void setState(int state) {
        this.state = state;
    }
    
    public int getState() {
        return state;
    }
    
    public double getProgress() {
        return progress;
    }
    
    public void setProgress(double progress) {
        this.progress = progress;
    }
}
