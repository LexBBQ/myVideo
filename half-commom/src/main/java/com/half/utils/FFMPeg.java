package com.half.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类为对音频视频进行合成处理的工具类
 */
public class FFMPeg {

    private String ffmpegEXE;

    public FFMPeg(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convert(String in,String mp3,Integer seconds,String out) throws IOException {
        //ffmpeg.exe -i test.mp4  -i bgm.mp3 -t 3 -y new.mp4
        //把一段命令通过空格的方式分割，并存入list
        List<String> cmd=new ArrayList<String>();
        cmd.add(ffmpegEXE);
        cmd.add("-i");

        cmd.add(in);
        cmd.add("-i");

        cmd.add(mp3);
        cmd.add("-t");

        cmd.add(String.valueOf(seconds));
        cmd.add("-y");
        cmd.add(out);

        //该类为java中操作cmd命令的一个类，参数中需要传入一个list列表
        ProcessBuilder processBuilder =new ProcessBuilder(cmd);
        Process process = processBuilder.start();
        //下面的操作是对FFMPeg中错误流的处理，如果不做该处理，最后合成的视频会不完整且无法播放
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ( (line = br.readLine()) != null ) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }


}
