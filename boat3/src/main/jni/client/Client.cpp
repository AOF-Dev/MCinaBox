

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
void Client::setup(ANativeWindow* win, void* dis){
	this->window = win;
	this->display = dis;
}
void Client::send(BoatInputEvent* event){
	if (this->mBoat->server != 0 && this->mBoat->server->current_event_processor != 0){
		this->mBoat->server->current_event_processor(event);
	}
	
}
//__android_log_print(ANDROID_LOG_ERROR, "Boat", "Request: %d", this->request);
			

//若x不存在，则对任意x均具有性质P

