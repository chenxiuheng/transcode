package com.thunisoft.trascode.tasks.cli.ffmpeg;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 时间格式化
 *
 * @author chenxh 2013-12-20
 * @version TMS2.2
 */
public class FFmpegUtils {
    /** 
     * 格式化为时间
     *
     * @param time  HH:mm:ss.sss
     * @return -1 if error
     * @author chenxh 2013-12-20
     * @version TMS2.2
     */
    public static long parseAsSeconds(String time){
        try {
            return parseTime(time);
        } catch (Exception e) {
            return -1;
        }
    }

    private static long parseTime(String time) {
        String [] seg = StringUtils.split(time, ':');
        
        double duration = 0;
        if (seg.length > 0) {
            // seconds
            duration += Double.parseDouble(seg[seg.length - 1]);
        }
        
        if (seg.length > 1) {
            // minutes
            duration += Double.parseDouble(seg[seg.length - 2]) * 60;
        }
        
        if (seg.length > 2) {
            // hours
            duration += Double.parseDouble(seg[seg.length - 3]) * 60 * 60;
        }
        
        if (seg.length > 3) {
            // days
            duration += Double.parseDouble(seg[seg.length - 4]) * 60 * 60 * 24;
        }
        
        return (long)duration;
    }
    
    /**
     *   Duration: 00:01:11.48, start: 0.000000, bitrate: N/A
     */
    final static Pattern P_FFMPEG_OUPUT_DURATION = Pattern.compile("^(.)*duration([^\\d]*)([\\d:]+(\\.[\\d]+)?)(.*)$");
    public static long getDuration(String ffmpegLine) {
        String lowerCaseLine = ffmpegLine.toLowerCase();
        Matcher matcher = P_FFMPEG_OUPUT_DURATION.matcher(lowerCaseLine);
        if (!matcher.matches()) {
            return -1;
        }

        final int groupIndex = 3;
        String sDuration = matcher.group(groupIndex);
        return parseAsSeconds(sDuration);
    }
    
    /**
     * frame=  584 fps= 57 q=29.0 size=    1294kB time=00:00:23.39 bitrate= 453.2kbits/s    
     */
    final static Pattern P_FFMPEG_OUTPUT_TIME = Pattern.compile("^(.)*time=([^\\d]*)([\\d:]+(\\.[\\d]+)?)(.*)$");
    public final static long getTime (String ffmpegLine) {
        String lowerCaseLine = ffmpegLine.toLowerCase();
        Matcher matcher = P_FFMPEG_OUTPUT_TIME.matcher(lowerCaseLine);
        if (!matcher.matches()) {
            return -1;
        } 

        final int groupIndex = 3;
        String sDuration = matcher.group(groupIndex);
        return parseAsSeconds(sDuration);
    }
}

