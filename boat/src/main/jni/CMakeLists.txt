cmake_minimum_required(VERSION 3.4.1)

project(boat)

add_library(${PROJECT_NAME} SHARED boat.c loadme.c boat_activity.c)

# Export BoatActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set_target_properties(${PROJECT_NAME} PROPERTIES LINK_FLAGS "-u BoatActivity_onCreate")

target_link_libraries(${PROJECT_NAME} android log)