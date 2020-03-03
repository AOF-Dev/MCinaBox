#ifndef BOAT_SERVER_H
#define BOAT_SERVER_H

#include <android/native_window.h>

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
typedef struct {
	long long time;
	char type;
    
    char mouse_button;
    int x;
    int y;
    
    int keycode;
    int keychar;
	
} BoatInputEvent;


ANativeWindow* boat_server_GetNativeWindow(void);

void* boat_server_GetNativeDisplay(void);

void boat_server_SetCurrentEventProcessor(void*, void (*)(BoatInputEvent*));

void* boat_server_GetCurrentWindow(void);

#endif
