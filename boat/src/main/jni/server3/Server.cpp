#include "Server.h"
#include <stdio.h>

#include <android/log.h>
#include <android/native_window.h>

Server* Server::mServer = 0;
Server::Server(){
    char* value = getenv("BOAT");
    if (value == NULL){
        printf("BOAT not specificed!");
		abort();
    }
    sscanf(value, "%p", &this->mBoat);
	
    this->mBoat->server = this;
	this->current_event_processor = 0;
}


extern "C"{

ANativeWindow* boat_server_GetNativeWindow(void){
	//__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "GetNativeWindow()");		
   return Server::mServer->mBoat->client->window;
}

void* boat_server_GetNativeDisplay(void){
	//__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "GetNativeDisplay()");	
	return Server::mServer->mBoat->client->display;
}
void* boat_server_GetCurrentWindow(void){
	//__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "GetCurrentWindow()");	
	return Server::mServer->current_window;
}
void boat_server_SetCurrentEventProcessor(void* win, void (*pcs)(BoatInputEvent*)){
	//__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "SetCurrentEventProcessor()");	
	Server::mServer->current_window = win;
	Server::mServer->current_event_processor = pcs;
}
void boat_server_SetCursorMode(int mode){
	__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "SetCursorMode()");	
	Server::mServer->mBoat->client->setCursorMode(mode);
}

__attribute__((constructor))
void init_server(){
	Server::mServer = new Server();
	__android_log_print(ANDROID_LOG_ERROR, "BoatServer", "Server initialized!");
			
}
}

