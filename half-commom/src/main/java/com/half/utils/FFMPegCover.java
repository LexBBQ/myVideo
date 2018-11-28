package com.half.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMPegCover {

    private String ffmpegEXE;

    public FFMPegCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convert(String in,String out) throws IOException {
        //ffmpeg.exe -ss 00:00:01 -y -i lex.mp4 -vframes 1 lex.jpg
        List<String> cmd=new ArrayList<String>();
        cmd.add(ffmpegEXE);
        cmd.add("-ss");
        cmd.add("00:00:01");

        cmd.add("-y");
        cmd.add("-i");

        cmd.add(in);
        cmd.add("-vframes");

        cmd.add("1");
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

    public static void main(String[] args) {
        FFMPegCover ffmPegCover = new FFMPegCover("F:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmPegCover.convert("F:\\ffmpeg\\bin\\lex.mp4","F:\\ffmpeg\\bin\\lex.jpg");
        } catch (IOException e) {
            e.printStackTrace();
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
