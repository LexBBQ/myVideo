package com.half.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMPeg {

    private String ffmpegEXE;

    public FFMPeg(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convert(String in,String mp3,Integer seconds,String out) throws IOException {
        //ffmpeg.exe -i test.mp4  -i bgm.mp3 -t 3 -y new.mp4
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
        for (String s:cmd
             ) {
            System.out.print(s);
        }

        ProcessBuilder processBuilder =new ProcessBuilder(cmd);
        Process process = processBuilder.start();

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

//    public static void main(String[] args) {
//        FFMPeg ffmPeg =new FFMPeg("F:\\ffmpeg\\bin\\ffmpeg.exe");
//        try {
//            ffmPeg.convert("F:\\ffmpeg\\bin\\test.mp4","F:\\ffmpeg\\bin\\bgm.mp3",4,"F:\\ffmpeg\\bin\\lex.mp4");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
