#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <dlfcn.h>

#include "boat.h"

class Client{

public :
     
    struct boat* mBoat;
	
	int width;
    int height;
	
	
	

public :
    Client();
	~Client();
	
	bool eglSwapBuffers_func();
	bool eglMakeCurrent_func(void*);
	bool eglSwapInterval_func(int);
	bool eglDestroyContext_func(void*);
	void* eglCreateContext_func(void*);
	void* eglGetCurrentContext_func();

	
};

