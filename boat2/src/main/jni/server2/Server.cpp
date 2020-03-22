#include "Server.h"
#include <stdio.h>

#include <android/log.h>

Server* Server::mServer = 0;
Server::Server(){
    char* value = getenv("BOAT");
    if (value == NULL){
        printf("BOAT not specificed!");
		abort();
    }
    sscanf(value, "%p", &this->mBoat);
	
    this->mBoat->server = this;
}


extern "C"{
bool eglSwapBuffers(){
	return Server::mServer->mBoat->client->eglSwapBuffers_func();
}
bool eglMakeCurrent(void* context){
	return Server::mServer->mBoat->client->eglMakeCurrent_func(context);
}
void* eglCreateContext(void* shared_context){
	return Server::mServer->mBoat->client->eglCreateContext_func(shared_context);
}
void* eglGetCurrentContext(){
	return Server::mServer->mBoat->client->eglGetCurrentContext_func();
}
bool eglDestroyContext(void* context){
	return Server::mServer->mBoat->client->eglDestroyContext_func(context);
}
bool eglSwapInterval(int value){
	return Server::mServer->mBoat->client->eglSwapInterval_func(value);
}
int getWindowHeight(){
   return Server::mServer->mBoat->client->height;
}
int getWindowWidth(){
   return Server::mServer->mBoat->client->width;
}


__attribute__((constructor))
void init_server(){
	Server::mServer = new Server();
	__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "Server initialized!");
			
}
}

