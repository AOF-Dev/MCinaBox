cmake_minimum_required(VERSION 3.4.1)

project(boat)

add_library(${PROJECT_NAME} SHARED boat.c loadme.c boat_activity.c)

target_link_libraries(${PROJECT_NAME} android log)