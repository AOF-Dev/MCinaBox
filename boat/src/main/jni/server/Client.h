#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <dlfcn.h>
#include <android/native_window.h>

#include "boat.h"

class Client{

public :
     
    struct boat* mBoat;
	
    void* display;
	ANativeWindow* window;
	void (*setCursorMode)(int);
	
};

