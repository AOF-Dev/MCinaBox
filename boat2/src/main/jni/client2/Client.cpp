

#include "Client.h"
Client* Client::mClient = 0;
Client::Client(){
	
	char* value = getenv("BOAT");
	if (value == NULL){
		printf("BOAT not specificed!");
		abort();
	}
	sscanf(value, "%p", &this->mBoat);
	this->mBoat->client = this;
	
	
}
Client::~Client(){
	
}
void Client::setWindow(ANativeWindow* win){
	this->window = win;
}
int Client::initDisplay(){
	
    const EGLint
    attribs[] = {
		EGL_SURFACE_TYPE, 
		EGL_WINDOW_BIT, 
		EGL_BLUE_SIZE, 8, 
		EGL_GREEN_SIZE, 8, 
		EGL_RED_SIZE, 8, 
		EGL_ALPHA_SIZE, 8,  
        EGL_DEPTH_SIZE, 24, //Request depth test buffer.
		EGL_NONE};
    EGLint w;
    EGLint h;
    EGLint format;
    EGLint numConfigs;
    EGLConfig  config;
    EGLSurface surface;
	EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
	
    eglInitialize(display, 0, 0);
    eglChooseConfig(display, attribs, &config, 1, &numConfigs );
    eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format );

    ANativeWindow_setBuffersGeometry(this->window, 0, 0, format );
	
    surface = eglCreateWindowSurface(display, config, this->window, NULL );
	
    eglQuerySurface(display, surface, EGL_WIDTH, &w );
    eglQuerySurface(display, surface, EGL_HEIGHT, &h );

    this->display = display;
    this->surface = surface;
    this->width = w;
    this->height = h;
	this->config = config;
	
	
	/*
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
	glViewport(0, 0, w, h);
	glClear(GL_COLOR_BUFFER_BIT);
	eglSwapBuffers( this->display, this->surface );
	*/
    return 0;
}

void Client::teardownDisplay(){
	this->window = 0;
	if ( this->display != EGL_NO_DISPLAY )
    {
        eglMakeCurrent(this->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT );
        
        if ( this->surface != EGL_NO_SURFACE )
        {
            eglDestroySurface( this->display, this->surface );
        }

        eglTerminate( this->display );
    }

    this->display = EGL_NO_DISPLAY;
    this->surface = EGL_NO_SURFACE;
}

bool Client::eglSwapBuffers_func(){
	//__android_log_print(ANDROID_LOG_ERROR, "Boat", "eglSwapBuffers");
	return eglSwapBuffers( this->display, this->surface );	
}
bool Client::eglMakeCurrent_func(void* context){
	
	bool retval = eglMakeCurrent(this->display, this->surface, this->surface, (EGLContext)context );
	
	__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Try to eglMakeCurrent : %d", eglGetError());
	
	return retval;
}

void* Client::eglGetCurrentContext_func(){
	
    return eglGetCurrentContext();
}

void* Client::eglCreateContext_func(void* shared_context){
	
	const EGLint
    contextAttribs[] = {
		EGL_CONTEXT_CLIENT_VERSION, 2,
		EGL_NONE};
    void* context = eglCreateContext(this->display, this->config, (EGLContext)shared_context, contextAttribs );
	
	__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Try to eglCreateContext : %d", eglGetError());
	
	return context;
   
}
bool Client::eglDestroyContext_func(void* context){
	
    bool retval = eglDestroyContext(this->display, (EGLContext)context );
	
	__android_log_print(ANDROID_LOG_ERROR, "BoatClient", "Try to eglDestroyContext : %d", eglGetError());
	
	return retval;
}

bool Client::eglSwapInterval_func(int value){
	return eglSwapInterval(this->display, value);	
}

//__android_log_print(ANDROID_LOG_ERROR, "Boat", "Request: %d", this->request);
			

