package at.uniklu.itec.videosummary;

/**
 * User: mlux
 * Date: 17.04.2008
 * Time: 11:22:20
 */
public interface VideoSummary {
    /**
     * Takes a specific frame and its data and analyzes it.
     *
     * @param frameNumber the number of the frame in the video.
     * @param rgbValues   color values ... [width][height][red, green, blue]
     */
    public void analyzeFrame(int frameNumber, int[][][] rgbValues);

    /**
     * Determines which frames are most important for a video summary and returns a ranked list of frames.
     *
     * @return a ranked list of frames important for the video summary. Most important on first position.
     */
    public int[] getSummaryFrames();
}
