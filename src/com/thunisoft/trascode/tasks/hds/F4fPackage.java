package com.thunisoft.trascode.tasks.hds;

import java.io.File;

import com.thunisoft.trascode.tasks.cli.CommandLineTask;

public class F4fPackage extends CommandLineTask {

    @Override
    protected void beforeRun() {
        File input = getInputFile();

        // 用 ffmpeg 转成标准  mp4 格式
        
        // 
        
    }
    
    private File getInputFile() {
        String root = getRootPath();
        String input = getInput();
        
        if (null == root) {
            return new File(input);
        }
        
        File file = new File(root, input);
        if (file.exists()) {
            return file;
        }
        
        return new File(input);
    }
    
}
