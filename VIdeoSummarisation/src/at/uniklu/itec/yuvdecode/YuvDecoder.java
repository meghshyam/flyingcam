package at.uniklu.itec.yuvdecode;

import at.uniklu.itec.videosummary.SimpleVideoSummary;
import at.uniklu.itec.videosummary.VideoSummary;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class YuvDecoder {
    int width, height, fps;
    String file;
    private int min = 0;
    private int max = 0;

    public YuvDecoder(int width, int height, int fps, String file) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.file = file;
    }

    public static void main(String[] args) {
        String file = null;
        int w = 0, h = 0, f = 0;
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.startsWith("-file")) {
                file = args[i + 1];
            } else if (s.startsWith("-w")) {
                w = Integer.parseInt(args[i + 1]);
            } else if (s.startsWith("-h")) {
                h = Integer.parseInt(args[i + 1]);
            } else if (s.startsWith("-fps")) {
                f = Integer.parseInt(args[i + 1]);
            }
        }
        if (h == 0 || w == 0 || file == null) {
            printHelp();
            System.exit(1);
        }
        YuvDecoder decoder = new YuvDecoder(w, h, f, file);
        try {
            decoder.decode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("java [...] YuvDecoder -w <width> -h <height> -file <file> [-f <fps>]\n" +
                "\twidth  ... width of the frames, e.g. 720 \n" +
                "\theight ... height of the frames, e.g. 544\n" +
                "\tfile   ... YUV 420p file, e.g. input.yuv\n" +
                "\tfps    ... frames per second, optional, e.g. 25");
    }

    private void decode() throws IOException, InterruptedException {
        FileInputStream f = new FileInputStream(new File(file));
        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        SimpleVideoSummary summary = new SimpleVideoSummary();
        Decoder d = new Decoder(f, width, height, frame, summary);
        d.run();

    }

    private int[] yuv2rgb(int y, int u, int v, int[] rgb) {
        int c = y - 16;
        int d = u - 128;
        int e = v - 128;
        rgb[0] = (298 * c + 409 * e + 128) >> 8;
        rgb[1] = (298 * c - 100 * d - 208 * e + 128) >> 8;
        rgb[2] = (298 * c + 516 * d + 128) >> 8;

        // clamp:
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = Math.min(rgb[i], 255);
            rgb[i] = Math.max(rgb[i], 0);
        }
        return rgb;
    }

    class Decoder extends Thread {
        FileInputStream f;
        int width, height;
        int pixels;
        BufferedImage frame;
        int[][][] raster;
        VideoSummary summary;

        public Decoder(FileInputStream f, int width, int height, BufferedImage frame, VideoSummary summary) {
            this.f = f;
            this.width = width;
            this.height = height;
            this.pixels = width * height;
            this.frame = frame;
            this.summary = summary;
            raster = new int[width][height][3];
        }

        public void run() {
            byte uu, yy, vv;
            int[] rgb = new int[3];
            byte[] buffer = new byte[pixels + pixels / 2];
            long time;
            int frameCount = 0;
            try {
                while (f.read(buffer) != -1) {
                    // the next frame has been read.
                    time = System.nanoTime();
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            yy = (buffer[y * width + x]);
                            uu = (buffer[pixels + (y >> 1) * (width >> 1) + (x >> 1)]);
                            vv = (buffer[pixels + (pixels >> 2) + (y >> 1) * (width >> 1) + (x >> 1)]);
                            yuv2rgb(toInt(yy), toInt(uu), toInt(vv), rgb);
                            raster[x][y][0] = rgb[0];
                            raster[x][y][1] = rgb[1];
                            raster[x][y][2] = rgb[2];
                            // This creates an actual image from the rgb data:
                            frame.getRaster().setPixel(x, y, rgb);
                        }
                    }
                    // ---> Hier is der Frame fertig dekodiert!!!!
                    // This actually does the analysis ...
                    summary.analyzeFrame(frameCount, raster);
                    System.out.println("frame: " + ++frameCount + " ... ms = " + (System.nanoTime() - time) / (1000 * 1000));
                }

                // This writes the image to a png file.
                ImageIO.write(frame, "png", new File("lastFrame.png"));

                // This is were the summary is returned:
                int[] frames = summary.getSummaryFrames();
                StringBuilder sb = new StringBuilder(25);
                for (int i = 0; i < frames.length; i++) {
                    sb.append(frames[i]);
                    if (i < frames.length - 1) sb.append(", ");

                }
                System.out.println("Summary frames: " + sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private int toInt(byte b) {
        int i = b & 0xff;
        return i;
    }
}


