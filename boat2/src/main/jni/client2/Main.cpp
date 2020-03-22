#include "Main.h"

struct boat mBoat;
//ANativeActivity callbacks

void onStart(ANativeActivity* activity){
	
}
void onResume(ANativeActivity* activity){
	
}

void* onSaveInstanceState(ANativeActivity* activity, size_t* outSize){
	
}

void onPause(ANativeActivity* activity){
	
}

void onStop(ANativeActivity* activity){
	
}

void onDestroy(ANativeActivity* activity){
	delete Client::mClient;
	Client::mClient = 0;
}

void onWindowFocusChanged(ANativeActivity* activity, int hasFocus){
	
    
}



void onNativeWindowCreated(ANativeActivity* activity, ANativeWindow* win){
	
	Client::mClient->setWindow(win);
	Client::mClient->initDisplay();
}

void onNativeWindowDestroyed(ANativeActivity* activity, ANativeWindow* win){
	Client::mClient->teardownDisplay();
}
void onNativeWindowRedrawNeeded(ANativeActivity* activity, ANativeWindow* win){
	
}
void onNativeWindowResized(ANativeActivity* activity, ANativeWindow* win){
	
}

void onInputQueueCreated(ANativeActivity* activity, AInputQueue* queue) {

	
}

void onInputQueueDestroyed(ANativeActivity* activity, AInputQueue* queue) {
    
	
}
void onConfigurationChanged(ANativeActivity* activity){
	
}

void onLowMemory(ANativeActivity* activity)
{
	
}

void ANativeActivity_onCreate(ANativeActivity* activity, void* savedState, size_t savedStateSize) {
    
	activity->callbacks->onStart = onStart;
    activity->callbacks->onResume = onResume;
    activity->callbacks->onSaveInstanceState = onSaveInstanceState;
    activity->callbacks->onPause = onPause;
    activity->callbacks->onStop = onStop;
    activity->callbacks->onDestroy = onDestroy;
    activity->callbacks->onWindowFocusChanged = onWindowFocusChanged;
    activity->callbacks->onNativeWindowCreated = onNativeWindowCreated;
    activity->callbacks->onNativeWindowDestroyed = onNativeWindowDestroyed;
    activity->callbacks->onInputQueueCreated = 0;
    activity->callbacks->onInputQueueDestroyed = 0;
    activity->callbacks->onConfigurationChanged = onConfigurationChanged;
    activity->callbacks->onLowMemory = onLowMemory;
	
	char value[256] = {0};
	mBoat.server = 0;
	mBoat.client = 0;
    snprintf(value, sizeof(value) , "%p", &mBoat);
    setenv("BOAT", value, 1);
	
	Client::mClient = new Client();

}

