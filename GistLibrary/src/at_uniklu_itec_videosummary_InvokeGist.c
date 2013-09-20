#include <jni.h>
#include <stdio.h>
#include "at_uniklu_itec_videosummary_InvokeGist.h"
#include "InvokeJNICppImpl.h"
 
JNIEXPORT void JNICALL Java_at_uniklu_itec_videosummary_InvokeGist_computeGist(JNIEnv *env, jobject thisObj, jstring input, jstring output, jint delme) {
	const char *inputfile = env->GetStringUTFChars( input, NULL);
	if (NULL == inputfile)
	{
		printf(" No Input given");
		return;
	}
	const char *outputfile = env->GetStringUTFChars(output, NULL);
	if (NULL == outputfile)
	{
		printf("Output file not given");
		return;
	}
	
    computeGistCpp(inputfile, outputfile);  // invoke C++ function
    return;
}
