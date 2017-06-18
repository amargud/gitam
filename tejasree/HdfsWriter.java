//package com.hadoop.hdfs.writer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.ToolRunner;

import java.awt.image.BufferedImage;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.*;


public class HdfsWriter extends Configured implements Tool {
    
    public static final String FS_PARAM_NAME = "fs.defaultFS";
    
    public int run(String[] args) throws Exception,IOException, InterruptedException, ExecutionException
 {
        
        //if (args.length < 2) {
            //System.err.println("HdfsWriter [local input path] [hdfs output path]");
            //return 1;
        //}
        
        String localInputPath = "/home/amar/manjusha/HappyBirthday.mp4";
        //String localInputPath = args[0];
        //Path outputPath = new Path(args[1]);
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(localInputPath);
        frameGrabber.start();
        IplImage i;
        frameGrabber.getFrameNumber();     
        try {
            String [] str_list = new String[2];
            for(int ii=0;ii<frameGrabber.getLengthInFrames();ii++){

                Frame frame= frameGrabber.grabFrame();
                Java2DFrameConverter paintConverter = new Java2DFrameConverter(); 
                BufferedImage image = paintConverter.getBufferedImage(frame, 2.2/frameGrabber.getGamma());
                
                String path = "/home/amar/manjusha/Image"+ii+".png";
                Path outputPath = new Path("/user/gabbar-" +ii + "-" + System.currentTimeMillis()  + ".png");
                ImageIO.write(image,"png", new File(path));
                Configuration conf = getConf();
                System.out.println("configured filesystem = " + conf.get(FS_PARAM_NAME));
                FileSystem fs = FileSystem.get(conf);
                if (fs.exists(outputPath)) {
                    System.err.println("output path exists");
                    return 1;
                }
                OutputStream os = fs.create(outputPath);
                InputStream is = new BufferedInputStream(new FileInputStream(path));
                IOUtils.copyBytes(is, os, conf);
                System.out.println(frame.imageWidth);
            }
            frameGrabber.stop();
        } catch (Exception e) {
                e.printStackTrace();
          }

/////////////////////////////////////
        return 0;
    }
    
    public static void main( String[] args )  {
        try {
            int returnCode = ToolRunner.run(new HdfsWriter(), args);
            System.exit(returnCode);
        } catch(Exception e) {
                   //// If it fails, write the error message to screen
                   e.printStackTrace();
        }
    }
}
