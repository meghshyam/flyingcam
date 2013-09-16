package at.uniklu.itec.videosummary;

import net.semanticmetadata.lire.imageanalysis.*;

import java.io.File;
import java.io.IOException;


public class SummarizeImages {

	public static void main(String[] args) {
		String file = null;
		String feature = "cedd";
		String outfile = null;
		String datafile = null;
		String contextfile = null;
		int numClusters = 3;
		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if (s.equals("-file") || s.startsWith("-i")) {
				file = args[i + 1];
			} else if (s.equals("-f")) {
				feature = args[i + 1].toLowerCase();
			} else if (s.equals("-n")) {
				numClusters = Integer.parseInt(args[i + 1]);
			} else if (s.equals("-o")) {
				outfile = args[i + 1];
			} else if (s.equals("-d")) {
				datafile = args[i + 1];
			} else if (s.equals("-c")) {
				contextfile = args[i + 1];
			} 
		}
		if (file == null) {
			printHelp();
			System.exit(1);
		} else if (!new File(file).exists()) {
			System.out.println("File does not exist.");
			printHelp();
			System.exit(1);
		}
		if (outfile == null) {
			outfile = new File(file).getName();
			try {				
				outfile = new File(file).getCanonicalPath() + File.separator + "output";				
			} catch (IOException e) {
				e.printStackTrace();
			}			
			System.out.println("outfile = " + outfile);
		}
		try {
			Class featureClass = CEDD.class;
			if (feature.startsWith("tamura")) {
				featureClass = Tamura.class;
			} else if (feature.startsWith("fcth")) {
				featureClass = FCTH.class;
			} else if (feature.startsWith("acc")) {
				featureClass = AutoColorCorrelogram.class;
			} else if (feature.startsWith("gabor")) {
				featureClass = Gabor.class;
			} else if (feature.startsWith("colorhist")) {
				featureClass = SimpleColorHistogram.class;
			}
			System.out.println("Starting a new summarization using feature " + featureClass.getName());
			new Summarize(file, datafile, contextfile, featureClass, outfile, numClusters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printHelp() {
		String help = "Running video summary generator:\n" +
				"================================\n" +
				"\n" +
				"java Summarize.jar -i <directory> [-f <feature>] [-o <output_dir>] [-n <number>] \n" +
				"-i ... input directory where frames are stored, \n" +
				"-f ... feature, the lire feature to use for summarization: \n" +
				"       cedd (default), tamura, fcth, gabor, acc, colorhist\n" +
				"-o ... output directory where result are stored \n" +
				"-n ... number of clusters, summary generated with > 2 clusters\n";
		System.out.println(help);
		System.exit(0);
	}	

}
