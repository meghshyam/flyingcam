package at.uniklu.itec.videosummary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class InvokeGist{
	// A native method that receives nothing and returns void
	private static native void computeGist(String input, String output);

	public static double[] ComputeGist(String input) {				
		System.loadLibrary("computegist");
		File infile = new File(input);
		String filename = infile.getName();
		String [] tokens = filename.split("\\.");
		String output = tokens[0] + ".txt";		
		computeGist(input, output);  // invoke the native method
		File outFile = new File(output);
		if(outFile.exists())
		{
			try{
				double [] data = readFile(outFile);				
				return data;
			}catch(Exception e){
				System.out.println("Can not read data");
			}
			finally{
				outFile.delete();
			}
		}
		return null;
	}

	public static double[] readFile(File file) throws Exception
	{
		double [] data = null;
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			String line = br.readLine();
			String [] tokens = line.split(" ");
			int DESC_LENGTH = tokens.length;
			data = new double[DESC_LENGTH];
			int i =0;
			for(String token:tokens)
			{
				if(i<DESC_LENGTH)
				{
					data[i] = Double.parseDouble(token);
					i++;
				}
			}
		} finally {
			br.close();
		}
		return data;
	}
}
