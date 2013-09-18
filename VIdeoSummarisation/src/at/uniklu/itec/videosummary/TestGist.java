package at.uniklu.itec.videosummary;

public class TestGist {

	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println("Usage: java TestGist <image_file>");
			System.exit(-1);
		}
		double [] data = InvokeGist.ComputeGist(args[0]);
		if ( data == null){
			System.out.println("Can not read input file");
			System.exit(-1);
		}
		System.out.println("Desc length:"+data.length);
		for(double desc:data){
			System.out.print(desc+" ");
		}
	}

}
