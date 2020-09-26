#ifndef BOAT_H
#define BOAT_H

#include <android/native_window.h>
#include <android/native_activity.h>
#include <jni.h>
#include <android/log.h>

#define KeyPress              2
#define KeyRelease            3
#define ButtonPress           4
#define ButtonRelease	      5
#define MotionNotify          6

#define Button1               1
#define Button2               2
#define Button3               3
#define Button4               4
#define Button5               5
#define Button6               6
#define Button7               7

#define CursorEnabled         1
#define CursorDisabled        0


typedef struct {
	long long time;
	char type;
    
    char mouse_button;
    int x;
    int y;
    
    int keycode;
    int keychar;
	
} BoatInputEvent;

typedef struct {
	
	ANativeWindow* window;
	void* display;
	
	JavaVM* android_jvm;
	jclass class_BoatInput;
	
	int (*current_event_processor)();
	BoatInputEvent current_event;
	
} Boat;

Boat mBoat;


ANativeWindow* boatGetNativeWindow();
void* boatGetNativeDisplay();
void boatSetCurrentEventProcessor(void (*)());
void boatGetCurrentEvent(BoatInputEvent*);
void boatSetCursorMode(int);
//void boatSetCursorPos(int, int);

#endif
