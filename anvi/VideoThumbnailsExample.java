import java.awt.image.BufferedImage;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;

public class VideoThumbnailsExample {
    
    public static final double SECONDS_BETWEEN_FRAMES = 3;

    private static final String inputFilename = "/home/amar/test/HappyBirthday.mp4";
    private static final String outputFilePrefix = "cartoon.png";
    //private static final String outputFilePrefix = "/home/amar/test/cartoon.png";
    
    // The video stream index, used to ensure we display frames from one and
    // only one video stream from the media container.
    private static int mVideoStreamIndex = -1;
    
    // Time of last frame write
    private static long mLastPtsWrite = Global.NO_PTS;
    private static String output_dir = "/user/frames-" + System.currentTimeMillis() +"/";
    
    public static final long MICRO_SECONDS_BETWEEN_FRAMES = 
        (long)(Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);

    public static void main(String[] args)  throws Exception {

        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);

        // stipulate that we want BufferedImages created in BGR 24bit color space
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        
        mediaReader.addListener(new ImageSnapListener());

        // read out the contents of the media file and
        // dispatch events to the attached listener
        while (mediaReader.readPacket() == null) ;

    }

    private static class ImageSnapListener extends MediaListenerAdapter {

        public void onVideoPicture(IVideoPictureEvent event) {

            if (event.getStreamIndex() != mVideoStreamIndex) {
                // if the selected video stream id is not yet set, go ahead an
                // select this lucky video stream
                if (mVideoStreamIndex == -1)
                    mVideoStreamIndex = event.getStreamIndex();
                // no need to show frames from this video stream
                else
                    return;
            }

            // if uninitialized, back date mLastPtsWrite to get the very first frame
            if (mLastPtsWrite == Global.NO_PTS)
                mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;

            // if it's time to write the next frame
            if (event.getTimeStamp() - mLastPtsWrite >= 
                    MICRO_SECONDS_BETWEEN_FRAMES) {
                                
                String outputFilename = dumpImageToFile(event.getImage());

                // indicate file written
                double seconds = ((double) event.getTimeStamp()) / 
                    Global.DEFAULT_PTS_PER_SECOND;
                System.out.printf(
                        "at elapsed time of %6.3f seconds wrote: %s\n",
                        seconds, outputFilename);

                // update last write time
                mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
            }

        }
        
        private String dumpImageToFile(BufferedImage image)  {
        //private String dumpImageToFile(BufferedImage image)   throws Exception {
            try {
                String outputFilename = outputFilePrefix + 
                System.currentTimeMillis() + ".png";
                String [] str_list = new String [2];
                ImageIO.write(image, "png", new File(outputFilename));
                str_list[0] =outputFilename ;
                str_list[1] = output_dir + outputFilename ;
                try {
                   // Try reading the file
                   int returnCode = ToolRunner.run(new HdfsWriter(), str_list);
                 } catch(Exception e) {
                   // If it fails, write the error message to screen
                   e.printStackTrace(); 
                 } 

                return outputFilename;
            } 
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
