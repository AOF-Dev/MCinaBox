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
	
	Client::mClient->setup(win, EGL_DEFAULT_DISPLAY);
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
	
	char value[256] = {0};
	mBoat.server = 0;
	mBoat.client = 0;
    snprintf(value, sizeof(value) , "%p", &mBoat);
    setenv("BOAT", value, 1);
	
	Client::mClient = new Client();
	

}


extern "C" JNIEXPORT void JNICALL Java_cosine_boat_BoatInputEventSender_send(JNIEnv* env, jclass clazz, jlong time, jint type, jint p1, jint p2){
	
	BoatInputEvent* event = (BoatInputEvent*)malloc(sizeof(BoatInputEvent));
	
	event->time = time;
	event->type = type;
	
	if (type == ButtonPress || type == ButtonRelease){
		event->mouse_button = p1;
	}
	else if (type == KeyPress || type == KeyRelease){
		event->keycode = p1;
		event->keychar = p2;
	}
	else if (type == MotionNotify){
		event->x = p1;
		event->y = p2;
	}

	Client::mClient->send(event);
	
	free(event);
	
}


extern "C" void setCursorMode(int mode){
	
	if (Client::mClient == 0 || Client::mClient->jvm == 0){
		return;
	}
	JNIEnv* env = 0;
	
	jint result = Client::mClient->jvm->AttachCurrentThread(&env, 0);
	
	if (result != JNI_OK || env == 0){
		__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Failed to attach thread to JavaVM.");
		abort();
	}
	
	jclass class_BoatInputEventSender = Client::mClient->g_BoatInputEventSender;
	
	if (class_BoatInputEventSender == 0){
		__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Failed to find class: cosine/boat/BoatInputEventSender.");
		abort();
	}
	
	jmethodID BoatInputEventSender_setCursorMode = env->GetStaticMethodID(class_BoatInputEventSender, "setCursorMode", "(I)V");
	
	if (BoatInputEventSender_setCursorMode == 0){
		__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Failed to find static method BoatInputEventSender::setCursorMode");
		abort();
	}
	env->CallStaticVoidMethod(class_BoatInputEventSender, BoatInputEventSender_setCursorMode, mode);
	
	
	
	
	
	Client::mClient->jvm->DetachCurrentThread();
	
	
	
}


extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm,void*)
{
	if (Client::mClient == 0){
		
		abort();
	}
	Client::mClient->jvm = vm;
	Client::mClient->setCursorMode = setCursorMode;
	
	JNIEnv* env = 0;
	
	jint result = Client::mClient->jvm->AttachCurrentThread(&env, 0);
	
	if (result != JNI_OK || env == 0){
		__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Failed to attach thread to JavaVM.");
		abort();
	}
	
	jclass class_BoatInputEventSender = env->FindClass("cosine/boat/BoatInputEventSender");
	if (cls == 0){
		__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Failed to find class: cosine/boat/BoatInputEventSender.");
		abort();
	}
	Client::mClient->class_BoatInputEventSender = (jclass)env->NewGlobalRef(class_BoatInputEventSender);
	
	return JNI_VERSION_1_6;
}
