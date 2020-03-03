#pragma once

#include <EGL/egl.h>
#include <android/native_activity.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <dlfcn.h>


#include "boat.h"
#include "boat_server.h"
#include "Server.h"

class Client{

public :
     
    struct boat* mBoat;
	
    void* display;
	ANativeWindow* window;
	void (*setCursorMode)(int);
	
	// client-only
	JavaVM* jvm;
	jclass g_BoatInputEventSender;
	

public :
    Client();
	~Client();
	void setup(ANativeWindow* , void*);
    void send(BoatInputEvent*);
	
	
public :
    static Client* mClient;
	
};

