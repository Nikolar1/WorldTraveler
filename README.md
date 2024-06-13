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
For the map to work GeoServer needs to be set up.
In GeoServer a new data store needs to be added.

GeoServer username and password are admin and geoserver by default

Chose PostGis and set postgres_db as the host and worldtraveler as the database. Leave the rest as default and save.

PostGis username and password are root and root by default

Now a new layer should be created from this newly added database. 
Publish country table set the layer name as country and compute the bounding boxes from native bounds.



> **Note:** Make sure that docker engine is running before executing the command.

