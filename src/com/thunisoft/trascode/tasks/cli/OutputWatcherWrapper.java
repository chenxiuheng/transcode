package com.thunisoft.trascode.tasks.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OutputWatcherWrapper implements OutputWatcher{
    private OutputWatcher watcher;

    private Logger logger = LoggerFactory.getLogger(OutputWatcherWrapper.class);

    OutputWatcherWrapper(OutputWatcher watcher) {
        this.watcher = watcher;
    }

    @Override
    public void onOutput(String line) {
        logger.debug("{}", line);

        if (null != watcher) {
            try {
                watcher.onOutput(line);
            } catch (Exception e) {
                logger.warn(watcher.getClass() + " error for: " + e.getMessage(), e);
            }
        }
    }
    
    public void setWatcher(OutputWatcher watcher) {
        this.watcher = watcher;
    }
}
