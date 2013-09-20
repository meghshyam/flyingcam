all: libcomputegist.so

JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/

gist.o: gist.c gist.h standalone_image.h
		gcc -c -fPIC -std=c99 $< -DUSE_GIST -DSTANDALONE_GIST

standalone_image.o: standalone_image.c standalone_image.h
		gcc -c -fPIC -std=c99 $< 
        
at_uniklu_itec_videosummary_InvokeGist.o: at_uniklu_itec_videosummary_InvokeGist.c at_uniklu_itec_videosummary_InvokeGist.h
		g++ -c -fPIC $< -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
		
InvokeJNICppImpl.o: InvokeJNICppImpl.cpp InvokeJNICppImpl.h
		g++ -c -fPIC $< -I/opt/ros/fuerte/include/opencv -I/opt/ros/fuerte/include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
		
libcomputegist.so: gist.o InvokeJNICppImpl.o at_uniklu_itec_videosummary_InvokeGist.o standalone_image.o
		g++ -shared -z text -z defs gist.o InvokeJNICppImpl.o at_uniklu_itec_videosummary_InvokeGist.o standalone_image.o -lm -lfftw3f -L/opt/ros/fuerte/lib -lopencv_calib3d -lopencv_contrib -lopencv_core -lopencv_features2d -lopencv_flann -lopencv_gpu -lopencv_highgui -lopencv_imgproc -lopencv_legacy -lopencv_ml -lopencv_nonfree -lopencv_objdetect -lopencv_photo -lopencv_stitching -lopencv_ts -lopencv_video -lopencv_videostab -lc -o libcomputegist.so 
		
clean:
		rm -rf *.o libcomputegist.so		