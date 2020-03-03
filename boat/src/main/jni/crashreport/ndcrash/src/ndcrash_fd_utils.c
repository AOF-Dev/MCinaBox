#include "ndcrash_fd_utils.h"
#include "ndcrash_log.h"
#include <fcntl.h>
#include <android/log.h>
#include <string.h>
#include <errno.h>
#include <sys/socket.h>

bool ndcrash_set_nonblock(int fd) {
    int socket_flags = fcntl(fd, F_GETFL);
    if (socket_flags==-1) {
        NDCRASHLOG(ERROR,"Couldn't get fcntl flags, error: %s (%d)", strerror(errno), errno);
        return false;
    }
    if (socket_flags & O_NONBLOCK) return true;
    socket_flags |= O_NONBLOCK;
    if (fcntl(fd, F_SETFL, socket_flags)==-1) {
        NDCRASHLOG(ERROR,"Couldn't set fcntl flags, error: %s (%d)", strerror(errno), errno);
        return false;
    }
    return true;
}
