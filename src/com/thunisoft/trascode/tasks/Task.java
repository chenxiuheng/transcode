package com.thunisoft.trascode.tasks;


/**
 * 
 * @since V1.0 2014-4-8
 * @author chenxh
 */
public interface Task extends Runnable {

    @Override
    public void run();

    /**
     * 取消当前任务
     * 
     * @return
     * @throws Exception
     * @since V1.0 2014-4-8
     * @author chenxh
     */
    public void cancle() throws Exception;

    /**
     * 获取当前任务的状态
     * 
     * @return
     * @throws Exception
     * @since V1.0 2014-4-8
     * @author chenxh
     */
    public TaskState getState() throws Exception;
}
