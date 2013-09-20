#include "InvokeJNICppImpl.h"
#include <iostream>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

extern "C" {
#include "gist.h"
}

using namespace cv;
using namespace std;
 
void computeGistCpp(const char *in, const char * out) {
	Mat img = imread(in, 1); 

	int width =  img.cols;
	int height =  img.rows;
	printf("Image width %d\n", width);
	printf("Image height %d\n", height);
	color_image_t *im=color_image_new(width,height);	
	for(int i=0; i<height;	i++)
 	{	
		for(int j=0; j<width; j++)
		{
			int k = i * width + j;
			Vec3b pix = img.at<Vec3b>(i,j);
			im->c1[k] = pix[0];
			im->c2[k] = pix[1];
			im->c3[k] = pix[2];
 		}
  	}
	int nblocks=4;
	int n_scale=3;
  	int orientations_per_scale[50]={8,8,4};
  
  	float *desc=color_gist_scaletab(im,nblocks,n_scale,orientations_per_scale);

	int i;
  
	int descsize=0;
  /* compute descriptor size */
	for(i=0;i<n_scale;i++) 
    	descsize+=nblocks*nblocks*orientations_per_scale[i];

  	descsize*=3; /* color */

  	/* print descriptor */
	ofstream outfile;
	outfile.open(out);
  	for(i=0;i<descsize;i++) 
	{
		outfile<<desc[i];
		if(i < descsize-1)
			outfile<<" ";
	}	
	outfile<<"\n";
  
  	free(desc);

  	color_image_delete(im);
	
	outfile.close();	
    return;
}
