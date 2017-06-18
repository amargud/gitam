package com.eldadlevy.hadoop;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.opencv_core.IplImage.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class DuplicateFrames {
	
	public static class ImageMd5Mapper extends Mapper<LongWritable, Text, Text, IntWritable>{
                       private final static IntWritable one = new IntWritable(1);

		public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException {
			//get the md5 for this specific file
    	               int width = 200;
                       int height = 100;
                       
                       String input_path = value.toString(); 
                       IplImage src = cvLoadImage( input_path );// its the image from which we will find the region of interest
                       IplImage tmp = cvLoadImage("/home/amar/manjusha/Image7.png");// here its the part of the image we want to match from the source image here we wanted to find the nose part 
               
                       IplImage result = cvCreateImage( cvSize(src.width() - tmp.width() + 1, src.height() - tmp.height() + 1), IPL_DEPTH_32F, 1); //here it will show the result image same here if you get the assertion error change  valsrc.nChannels()
               
                       cvZero(result);
               
                       // Match Template Function from OpenCV
                       cvMatchTemplate(src, tmp, result, CV_TM_CCORR_NORMED); // fuction used to find  or match the template from an image
               
                       // double[] min_val = new double[2];
                       // double[] max_val = new double[2];
                       DoublePointer min_val = new DoublePointer();
                       DoublePointer max_val = new DoublePointer();
               
                       CvPoint minLoc = new CvPoint();
                       CvPoint maxLoc = new CvPoint();
               
                       cvMinMaxLoc(result, min_val, max_val, minLoc, maxLoc, null);// finding min and max location
               
                       // Get the Max or Min Correlation Value
                       // System.out.println(Arrays.toString(min_val));
                       // System.out.println(Arrays.toString(max_val));
               
                       CvPoint point = new CvPoint();
                       point.x(maxLoc.x() + tmp.width());
                       point.y(maxLoc.y() + tmp.height());
                       // cvMinMaxLoc(src, min_val, max_val,0,0,result);
               
                       cvRectangle(src, maxLoc, point, CvScalar.RED, 2, 8, 0); // Draw a Rectangle for matched region
                                                                             
                       CvRect rect = new CvRect();
                       rect.x(maxLoc.x());
                       rect.y(maxLoc.y());
                       rect.width(tmp.width() + width);
                       rect.height(tmp.width() + height);
                       cvSetImageROI(src, rect);// here we are finding the region of interest from the source image
                       IplImage imageNew = cvCreateImage(cvGetSize(src), src.depth(),
                       src.nChannels());// after that we are creating the new image note that if you find the assertion error change the value of src.nChannels() to 1 
                       cvCopy(src, imageNew);
                       //cvSaveImage(args[2], imageNew);
               
                       //cvShowImage("output image", src);// show the out put image in a window  
                     
                       //cvWaitKey(0);
                       cvReleaseImage(src);
                       cvReleaseImage(tmp);
                       cvReleaseImage(result);// thats all from the coding part now lets see the image

			
                        context.write(value, one);
		}
		
	}

	public static class ImageDupsReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
               public void reduce(Text word, Iterable<IntWritable> counts, Context context) throws IOException, InterruptedException {
                   int sum = 0;
                   for (IntWritable count : counts) {
                     sum += count.get();
                   }
                   context.write(word, new IntWritable(sum));
                 }
                           
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		//This is the line that makes the hadoop run locally
		//conf.set("mapred.job.tracker", "local");

		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "image dups remover");
		job.setJarByClass(DuplicateFrames.class);
		//job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setMapperClass(ImageMd5Mapper.class);
		job.setReducerClass(ImageDupsReducer.class);
		//job.setNumReduceTasks(2);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
	}
}
