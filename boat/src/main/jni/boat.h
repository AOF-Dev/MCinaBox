#ifndef BOAT_H
#define BOAT_H

#include <EGL/egl.h>

#define KeyPress        2
#define KeyRelease      3
#define ButtonPress     4
#define ButtonRelease   5
#define MotionNotify    6

#define CursorDisabled  0

typedef struct {
    long long time;
    char type;

    char mouse_button;
    int x;
    int y;

    int keycode;
    int keychar;
} BoatInputEvent;

// Start: This is for a future Boat update
typedef struct {
    unsigned long time;
    unsigned int state;
    unsigned int keycode;
} BoatKeyEvent;

typedef struct {
    unsigned long time;
    int x, y;
    int x_root, y_root;
    unsigned int state;
    unsigned int button;
} BoatButtonEvent;

typedef struct {
    int type;
    union {
        BoatKeyEvent key;
        BoatButtonEvent button;
    };
} BoatEvent;
// End future Boat update

EGLNativeWindowType boatGetNativeWindow();

EGLNativeDisplayType boatGetNativeDisplay();

void boatSetCurrentEventProcessor(void (*)());

void boatGetCurrentEvent(BoatInputEvent *);

void boatSetCursorMode(int);

//void boatSetCursorPos(int, int);

#endif
