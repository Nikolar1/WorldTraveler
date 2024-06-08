# WorldTraveler


## Project Description
This is a web game in which you need to guess the countries on the shortest path between two countries.
It uses SpringBoot for its backend and React on the frontend.
Postgres with the PostGIS addon is used as the database for the spatial data of the world map boarders.
GeoServer is used to help with the visualization of the spatial data.


## Getting Started

The app is dockerized, so it can easily be run using docker.

### Starting the app
To run the project you will need docker and docker-compose. In the project root folder run the following command:

	docker-compose up --build

The app will start and be accessible at `http://localhost:3000`.

> **Note:** Make sure that docker engine is running before executing the command.

