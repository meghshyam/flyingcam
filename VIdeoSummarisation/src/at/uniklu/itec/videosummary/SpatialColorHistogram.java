package at.uniklu.itec.videosummary;

import net.semanticmetadata.lire.imageanalysis.*;

import at.lux.imageanalysis.VisualDescriptor;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.StringTokenizer;
import net.semanticmetadata.lire.imageanalysis.utils.Quantization;

public class SpatialColorHistogram
  implements LireFeature
{
  public static final int DEFAULT_NUMBER_OF_BINS = 256;
  public static final int GRID_ROWS = 3;
  public static final int GRID_COLUMNS = 3;
  public static final HistogramType DEFAULT_HISTOGRAM_TYPE = HistogramType.RGB;
  public static final DistanceFunction DEFAULT_DISTANCE_FUNCTION = DistanceFunction.L2;
  private static final int[] quantTable = { 1, 32, 4, 8, 16, 4, 16, 4, 16, 4, 1, 16, 4, 4, 8, 4, 8, 4, 8, 4, 1, 8, 4, 4, 4, 4, 8, 2, 8, 1, 1, 8, 4, 4, 4, 4, 4, 1, 4, 1 };
  private int[] pixel = new int[3];
  private float[][] histogram;
  private HistogramType histogramType;
  private DistanceFunction distFunc;

  public SpatialColorHistogram()
  {
    this.histogramType = DEFAULT_HISTOGRAM_TYPE;
    int numCells = GRID_ROWS*GRID_COLUMNS;
    this.histogram = new float[numCells][DEFAULT_NUMBER_OF_BINS];
    this.distFunc = DEFAULT_DISTANCE_FUNCTION;
  }

  public SpatialColorHistogram(HistogramType paramHistogramType, DistanceFunction paramDistanceFunction)
  {
    this.histogramType = paramHistogramType;
    this.distFunc = paramDistanceFunction;
    this.histogram = new float[9][256];
  }

  public void extract(BufferedImage paramBufferedImage)
  {
    if (paramBufferedImage.getColorModel().getColorSpace().getType() != 5)
      throw new UnsupportedOperationException("Color space not supported. Only RGB.");
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    
    /*
    for (int i = 0; i < paramBufferedImage.getWidth(); i++)
      for (int j = 0; j < paramBufferedImage.getHeight(); j++)
      {
        localWritableRaster.getPixel(i, j, this.pixel);
        if (this.histogramType == HistogramType.HSV)
          rgb2hsv(this.pixel[0], this.pixel[1], this.pixel[2], this.pixel);
        else if (this.histogramType == HistogramType.Luminance)
          rgb2yuv(this.pixel[0], this.pixel[1], this.pixel[2], this.pixel);
        else if (this.histogramType == HistogramType.HMMD)
          this.histogram[quantHmmd(rgb2hmmd(this.pixel[0], this.pixel[1], this.pixel[2]), 256)] += 1;
        if (this.histogramType != HistogramType.HMMD)
          this.histogram[quant(this.pixel)] += 1;
      }
      */
    int width = paramBufferedImage.getWidth();
    int height = paramBufferedImage.getHeight();
    int grid_width = width/GRID_COLUMNS;
    int grid_height = height/GRID_ROWS;    		
    for(int i=0; i<width; i++)
    {
    	int column_index = i/grid_width;
    	if(column_index== GRID_COLUMNS)
    		column_index--;
    	for(int j=0; j<height; j++)
    	{
    		int row_index = j/grid_height;
    		if(row_index == GRID_ROWS)
    			row_index--;
    		localWritableRaster.getPixel(i, j, this.pixel);
    		int quant_output = quant(this.pixel);
    		int cellNo = row_index * GRID_COLUMNS + column_index;
    		this.histogram[cellNo][quant_output] += 1;
    	}
    }
    for(int i=0; i<9; i++)
    {
    	normalize(this.histogram[i]);
    }
  }

  private void normalize(float[] paramArrayOfInt)
  {
    float i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++)
      i = Math.max(paramArrayOfInt[j], i);
    for (int j = 0; j < paramArrayOfInt.length; j++)
      paramArrayOfInt[j] = (paramArrayOfInt[j] / i);
  }

  private int quant(int[] paramArrayOfInt)
  {
    int i = 16777216 / this.histogram.length;
    if (this.histogramType == HistogramType.HSV)
    {
      int j = paramArrayOfInt[0] * 8 / 360;
      int k = paramArrayOfInt[2] * 4 / 100;
      return j * (k + 1);
    }
    if (this.histogramType == HistogramType.HMMD)
      return quantHmmd(rgb2hmmd(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2]), 256);
    if (this.histogramType == HistogramType.Luminance)
      return paramArrayOfInt[0] * this.histogram.length / 256;
    return Quantization.quantUniformly(paramArrayOfInt, this.histogram.length, 256.0D);
  }

  public float getDistance(VisualDescriptor paramVisualDescriptor)
  {
    if (!(paramVisualDescriptor instanceof SpatialColorHistogram))
      throw new UnsupportedOperationException("Wrong descriptor.");
    SpatialColorHistogram localSimpleColorHistogram = (SpatialColorHistogram)paramVisualDescriptor;
    if ((localSimpleColorHistogram.histogram.length != this.histogram.length) || (localSimpleColorHistogram.histogramType != this.histogramType))
      throw new UnsupportedOperationException("Histogram lengths or color spaces do not match");
    double d = 0.0D;
    /*
    if (this.distFunc == DistanceFunction.JSD)
      return (float)jsd(this.histogram, localSimpleColorHistogram.histogram);
    if (this.distFunc == DistanceFunction.TANIMOTO)
      return (float)tanimoto(this.histogram, localSimpleColorHistogram.histogram);
    if (this.distFunc == DistanceFunction.L1)
      return (float)distL1(this.histogram, localSimpleColorHistogram.histogram);
      */
    return (float)distL2(this.histogram, localSimpleColorHistogram.histogram);
  }

  private static double distL1(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    double d = 0.0D;
    for (int i = 0; i < paramArrayOfInt1.length; i++)
      d += Math.abs(paramArrayOfInt1[i] - paramArrayOfInt2[i]);
    return d / paramArrayOfInt1.length;
  }

  private static double distL2(float[][] paramArrayOfInt1, float[][] paramArrayOfInt2)
  {
    double d = 0.0D;
    for(int cellNo=0; cellNo<9; cellNo++)
    {
    for (int i = 0; i < paramArrayOfInt1.length; i++)
      d += (paramArrayOfInt1[cellNo][i] - paramArrayOfInt2[cellNo][i]) * (paramArrayOfInt1[cellNo][i] - paramArrayOfInt2[cellNo][i]);
    }
    return Math.sqrt(d);
  }

  private static double jsd(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    double d = 0.0D;
    for (int i = 0; i < paramArrayOfInt1.length; i++)
      d += (0 + paramArrayOfInt2[i] > 0 ? paramArrayOfInt2[i] * Math.log(2.0D * paramArrayOfInt2[i] / (paramArrayOfInt1[i] + paramArrayOfInt2[i])) : paramArrayOfInt1[i] > 0 ? paramArrayOfInt1[i] * Math.log(2.0D * paramArrayOfInt1[i] / (paramArrayOfInt1[i] + paramArrayOfInt2[i])) : 0.0D);
    return d;
  }

  private static double tanimoto(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    for (int i = 0; i < paramArrayOfInt1.length; i++)
    {
      d2 += paramArrayOfInt1[i];
      d3 += paramArrayOfInt2[i];
    }
    if ((d2 == 0.0D) || (d3 == 0.0D))
      d1 = 100.0D;
    if ((d2 == 0.0D) && (d3 == 0.0D))
      d1 = 0.0D;
    if ((d2 > 0.0D) && (d3 > 0.0D))
    {
      for (int i = 0; i < paramArrayOfInt1.length; i++)
      {
        d4 += paramArrayOfInt1[i] / d2 * (paramArrayOfInt2[i] / d3);
        d5 += paramArrayOfInt2[i] / d3 * (paramArrayOfInt2[i] / d3);
        d6 += paramArrayOfInt1[i] / d2 * (paramArrayOfInt1[i] / d2);
      }
      d1 = 100.0D - 100.0D * (d4 / (d5 + d6 - d4));
    }
    return d1;
  }

  public String getStringRepresentation()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.histogram.length * 4);
    localStringBuilder.append(this.histogramType.name());
    localStringBuilder.append(' ');
    localStringBuilder.append(this.histogram.length);
    localStringBuilder.append(' ');
    for (int i = 0; i < this.histogram.length; i++)
    {
      localStringBuilder.append(this.histogram[i]);
      localStringBuilder.append(' ');
    }
    return localStringBuilder.toString().trim();
  }

  public void setStringRepresentation(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    this.histogramType = HistogramType.valueOf(localStringTokenizer.nextToken());
    this.histogram = new float[9][];
    for(int cellNo =0; cellNo <9; cellNo++)
    {
    	this.histogram[cellNo] = new float[Integer.parseInt(localStringTokenizer.nextToken())];
    	for (int i = 0; i < this.histogram[cellNo].length; i++)
    	{
    		if (!localStringTokenizer.hasMoreTokens())
    			throw new IndexOutOfBoundsException("Too few numbers in string representation!");
    		this.histogram[cellNo][i] = Integer.parseInt(localStringTokenizer.nextToken());
    	}
    }
  }

  public static void rgb2yuv(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    int i = (int)(0.299D * paramInt1 + 0.587D * paramInt2 + 0.114D * paramInt3);
    int j = (int)((paramInt3 - i) * 0.492F);
    int k = (int)((paramInt1 - i) * 0.877F);
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
    paramArrayOfInt[2] = k;
  }

  public static void rgb2hsv(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    int i = Math.min(paramInt1, paramInt2);
    i = Math.min(i, paramInt3);
    int j = Math.max(paramInt1, paramInt2);
    j = Math.max(j, paramInt3);
    int k = j - i;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = j / 255.0F;
    if (k == 0)
    {
      f1 = 0.0F;
      f2 = 0.0F;
    }
    else
    {
      f2 = k / 255.0F;
      if (paramInt1 == j)
      {
        if (paramInt2 >= paramInt3)
          f1 = (paramInt2 / 255.0F - paramInt3 / 255.0F) / k / 255.0F * 60.0F;
        else
          f1 = (paramInt2 / 255.0F - paramInt3 / 255.0F) / k / 255.0F * 60.0F + 360.0F;
      }
      else if (paramInt2 == j)
        f1 = (2.0F + (paramInt3 / 255.0F - paramInt1 / 255.0F) / k / 255.0F) * 60.0F;
      else if (paramInt3 == j)
        f1 = (4.0F + (paramInt1 / 255.0F - paramInt2 / 255.0F) / k / 255.0F) * 60.0F;
    }
    paramArrayOfInt[0] = ((int)f1);
    paramArrayOfInt[1] = ((int)(f2 * 100.0F));
    paramArrayOfInt[2] = ((int)(f3 * 100.0F));
  }

  private static int[] rgb2hmmd(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = new int[5];
    float f1 = Math.max(Math.max(paramInt1, paramInt2), Math.max(paramInt2, paramInt3));
    float f2 = Math.min(Math.min(paramInt1, paramInt2), Math.min(paramInt2, paramInt3));
    float f3 = f1 - f2;
    float f4 = (float)((f1 + f2) / 2.0D);
    float f5 = 0.0F;
    if (f3 == 0.0F)
      f5 = 0.0F;
    else if ((paramInt1 == f1) && (paramInt2 - paramInt3 > 0))
      f5 = 60 * (paramInt2 - paramInt3) / (f1 - f2);
    else if ((paramInt1 == f1) && (paramInt2 - paramInt3 <= 0))
      f5 = 60 * (paramInt2 - paramInt3) / (f1 - f2) + 360.0F;
    else if (paramInt2 == f1)
      f5 = (float)(60.0D * (2.0D + (paramInt3 - paramInt1) / (f1 - f2)));
    else if (paramInt3 == f1)
      f5 = (float)(60.0D * (4.0D + (paramInt1 - paramInt2) / (f1 - f2)));
    f3 /= 2.0F;
    arrayOfInt[0] = ((int)f5);
    arrayOfInt[1] = ((int)f1);
    arrayOfInt[2] = ((int)f2);
    arrayOfInt[3] = ((int)f3);
    arrayOfInt[4] = ((int)f4);
    return arrayOfInt;
  }

  private int quantHmmd(int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (paramArrayOfInt[3] < 7)
      k = 0;
    else if ((paramArrayOfInt[3] > 6) && (paramArrayOfInt[3] < 21))
      k = 1;
    else if ((paramArrayOfInt[3] > 19) && (paramArrayOfInt[3] < 61))
      k = 2;
    else if ((paramArrayOfInt[3] > 59) && (paramArrayOfInt[3] < 111))
      k = 3;
    else if ((paramArrayOfInt[3] > 109) && (paramArrayOfInt[3] < 256))
      k = 4;
    if (paramInt == 256)
    {
      j = 0;
      i = paramArrayOfInt[0] / paramInt * quantTable[(j + k)] + paramArrayOfInt[4] / paramInt * quantTable[(j + k + 1)];
    }
    else if (paramInt == 128)
    {
      j = 10;
      i = paramArrayOfInt[0] / paramInt * quantTable[(j + k)] + paramArrayOfInt[4] / paramInt * quantTable[(j + k + 1)];
    }
    else if (paramInt == 64)
    {
      j = 20;
      i = paramArrayOfInt[0] / paramInt * quantTable[(j + k)] + paramArrayOfInt[4] / paramInt * quantTable[(j + k + 1)];
    }
    else if (paramInt == 32)
    {
      j = 30;
      i = paramArrayOfInt[0] / paramInt * quantTable[(j + k)] + paramArrayOfInt[4] / paramInt * quantTable[(j + k + 1)];
    }
    return i;
  }

  static enum DistanceFunction
  {
    L1, L2, TANIMOTO, JSD;
  }

  static enum HistogramType
  {
    RGB, HSV, Luminance, HMMD;
  }
}

/* Location:           /home/meghshyam/Downloads/videosummary/lib/lire.jar
 * Qualified Name:     net.semanticmetadata.lire.imageanalysis.SimpleColorHistogram
 * JD-Core Version:    0.6.2
 */