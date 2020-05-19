# WebEventPlanner

Table of contents
=================

<!--ts-->
   * [Introduction](#Introduction)
   * [Features](#Features)
   * [Tools](#Tools)
<!--te-->

#Introduction

This project is a list of event scheduling services bundled into a Docker container that communicates with another Redis container through a reverse Nginx proxy. The server is hosted on the Google Cloud environment using virtual machines. 

# Features

## Hotswapping

The bashfile, hotswap.sh, has the following feature: switching the port location of the server, while it is running, from one redis data storage backend (the one running in Google Cloud's VM instance) to a redis backend running on another instance. This is accomplished by telling the nginx reverse proxy server to redirect all conversation between the studup server and the redis server. This involves 3 steps:

1. Connect to the running nginx container
2. Change the nginx.conf config file to redirect to the redis on our cloud node, rather than the local redis.
3. Tell the nginx reverse proxy to reload the config file.

# Tools

## Docker Containers

"A container is a standard unit of software that packages up code and all its dependencies so the application runs quickly and reliably from one computing environment to another. A Docker container image is a lightweight, standalone, executable package of software that includes everything needed to run an application: code, runtime, system tools, system libraries and settings.

Container images become containers at runtime and in the case of Docker containers - images become containers when they run on Docker Engine. Available for both Linux and Windows-based applications, containerized software will always run the same, regardless of the infrastructure. Containers isolate software from its environment and ensure that it works uniformly despite differences for instance between development and staging."

More information on Docker containers can be found [here](https://www.docker.com/resources/what-container).

## Redis

Redis is a key-value store which allows data to be stored and accessed at lightning fast speeds. These qualities make it a natural choice for use with Docker containers. Redis is one of the top three most popular Docker images.

Requirements:

* A Cloud Server running Linux (Ubuntu 14.04 or CentOS 7)
* Docker installed and running
* Familiarity with basic Docker functionality and commands

## Google Cloud Services

Setting up a new, non-business, Google Cloud account allots new users with 100 credits. These credits allow access to many Google services that allow novice application development to be made much easier. Guidance on what cloud storage is and a great resource to start one's own account can be found [here](https://cloud.google.com/). For the purposes of this project, a virtual machine was set up. This project also required setting up an online project inside the Google Console.
