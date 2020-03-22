#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

#include "Client.h"
class Server{

public :
    struct boat* mBoat;
public :
    Server();
public :
    static Server* mServer;

};

