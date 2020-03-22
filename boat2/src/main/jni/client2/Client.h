#pragma once

#include <EGL/egl.h>
#include <GLES/gl.h>
#include <android/native_activity.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <dlfcn.h>

#include "boat.h"

class Client{

public :
     
    struct boat* mBoat;
	
	int width;
    int height;
	
    EGLDisplay display;
    EGLSurface surface;
    EGLConfig config;
	
	ANativeWindow* window;
	
	

public :
    Client();
	~Client();
	
    int initDisplay();
	void teardownDisplay();
	void setWindow(ANativeWindow*);
	bool eglSwapBuffers_func();
	bool eglMakeCurrent_func(void*);
	bool eglSwapInterval_func(int);
	bool eglDestroyContext_func(void*);
	void* eglCreateContext_func(void*);
	void* eglGetCurrentContext_func();
	
public :
    static Client* mClient;
	
};

