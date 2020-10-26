#ifndef BOAT_H
#define BOAT_H

#include <EGL/egl.h>

#define KeyPress              2
#define KeyRelease            3
#define ButtonPress           4
#define ButtonRelease         5
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

EGLNativeWindowType boatGetNativeWindow();

EGLNativeDisplayType boatGetNativeDisplay();

void boatSetCurrentEventProcessor(void (*)());

void boatGetCurrentEvent(BoatInputEvent *);

void boatSetCursorMode(int);

//void boatSetCursorPos(int, int);

#endif
