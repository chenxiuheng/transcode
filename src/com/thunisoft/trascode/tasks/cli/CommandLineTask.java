package com.thunisoft.trascode.tasks.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thunisoft.trascode.tasks.Task;
import com.thunisoft.trascode.tasks.TaskState;
import com.thunisoft.trascode.utils.placehold.PlaceholderReplacer;

/**
 * 命令行任务
 * 
 * <p>
 *  通过调用命令exe完成转码任务
 *  
 * @since V1.0  2014-4-8
 * @author chenxh
 */
public class CommandLineTask implements Task, OutputWatcher {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String commandLine;

    private long timeoutMillis = -1;

    private Map<String, String> options = new HashMap<String, String>();

    private ProcessExecutor executor;
    protected TaskState state = new TaskState();



    @Override
    final public void run() {
        beforeRun();

        doRun();

        afterRun();
    }

    protected void beforeRun() {

    }


    private void doRun() {
        String commandLine = getCommandLine();
        executor = new ProcessExecutor(commandLine, this);

        if (timeoutMillis > 0) {
            executor.setTimeout(timeoutMillis);
        }

        try {
            executor.startAndWait();
        } catch (Exception e) {
            logger.warn("Error: " + commandLine, e);
        } finally {
            executor.close();
        }
    }

    protected void afterRun() {

    }

    @Override
    final public void cancle() throws Exception {
        executor.cancle();
    }

    @Override
    final public TaskState getState() throws Exception {
        return state;
    }

    public void setCommandLine(String cmd) {
        this.commandLine = cmd;
    }

    protected String getCommandLine() {
        try {
            PlaceholderReplacer placeholderReplacer = new PlaceholderReplacer(options);
            String newLine = placeholderReplacer.replace(commandLine);

            return newLine;
        } catch (IOException e) {
            // 内存读取，不会抛出 RuntimeException
            throw new RuntimeException(e.getMessage());
        }

    }

    public void setOptions(Map<String, String> options) {
        this.options.putAll(options);
    }

    public void setOptionValue(String key, String value) {
        this.options.put(key, value);
    }

    @Override
    public void onOutput(String line) {

    }

    public String getInput() {
        return options.get(CommandLineOptions.INPUT);
    }
    
    public void setInput(String input) {
        options.put(CommandLineOptions.INPUT, input);
    }
    
    public String getOutput() {
        return options.get(CommandLineOptions.OUTPUT);
    }
    
    public String getRootPath() {
        return options.get(CommandLineOptions.ROOT_PATH);
    }
}
