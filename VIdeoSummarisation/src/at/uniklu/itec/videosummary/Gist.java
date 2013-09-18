package at.uniklu.itec.videosummary;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



import at.lux.imageanalysis.VisualDescriptor;
import net.semanticmetadata.lire.imageanalysis.LireFeature;

public class Gist implements LireFeature {

	double []data = null;
	int dimension = 0;
	@Override
	public float getDistance(VisualDescriptor arg0) {
		if(! (arg0 instanceof Gist))
			return -1;
		else{
			Gist t = (Gist)arg0;
			int other_dim = t.dimension;
			if(dimension != other_dim){
				return -1;
			}
			double [] other_data = t.data;
			double distance = 0;
			for(int i=0; i<dimension; i++)
			{
				double diff = data[i] - other_data[i];
				distance += diff * diff;
			}
			return (float)Math.sqrt(distance);
		}
	}

	@Override
	public String getStringRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStringRepresentation(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extract(BufferedImage img) {
		String input = "temp.jpg";
		try {
			File temp = new File(input);
			ImageIO.write(img, "jpg", temp);
			data = InvokeGist.ComputeGist(input);
			dimension = data.length;
			temp.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
