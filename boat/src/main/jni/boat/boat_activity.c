


#include "boat.h"


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
	
}

void onWindowFocusChanged(ANativeActivity* activity, int hasFocus){
	
    
}

void onNativeWindowCreated(ANativeActivity* activity, ANativeWindow* win){
	__android_log_print(ANDROID_LOG_ERROR, "Boat", "onNativeWindowCreated : %p", win);
    
	mBoat.window = win;
	mBoat.display = 0;
}

void onNativeWindowDestroyed(ANativeActivity* activity, ANativeWindow* win){
	
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

}

