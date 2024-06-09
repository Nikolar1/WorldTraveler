package com.nikolar.worldtraveler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeoServerConfigurationService {
    private final RestTemplate restTemplate;
    private final String geoServerUrl;
    private final String geoServerAdminUsername;
    private final String geoServerAdminPassword;
    private final String postgisUrl;
    private final String postgisUsername;
    private final String postgisPassword;
    private final String postgisSchema;
    private final String workspaceName = "worldtraveler";
    private final String storeName = "postgis";
    private final String layerName = "countries";

    private final Logger logger = LoggerFactory.getLogger(GeoServerConfigurationService.class);

    @Autowired
    public GeoServerConfigurationService(RestTemplate restTemplate,
                                         @Value("${service.geoserver.address}") String geoServerUrl,
                                         @Value("${service.geoserver.admin.username}") String geoServerAdminUsername,
                                         @Value("${service.geoserver.admin.password}") String geoServerAdminPassword,
                                         @Value("${spring.datasource.url}") String postgisUrl,
                                         @Value("${spring.datasource.username}") String postgisUsername,
                                         @Value("${spring.datasource.password}") String postgisPassword,
                                         @Value("${postgis.schema}") String postgisSchema){
        this.restTemplate = restTemplate;
        this.geoServerUrl = geoServerUrl;
        this.geoServerAdminUsername = geoServerAdminUsername;
        this.geoServerAdminPassword = geoServerAdminPassword;
        this.postgisUrl = postgisUrl;
        this.postgisUsername = postgisUsername;
        this.postgisPassword = postgisPassword;
        this.postgisSchema = postgisSchema;
    }

    public void configureGeoServer(){
        logger.info("Starting GeoServer configuration");
        createWorkspace();
        createPostGISDataStore();
        createLayer();
        logger.info("Successfully configured GeoServer");
    }
    private void createWorkspace() {
        logger.info("Creating workspace in GeoServer");
        String createWorkspaceUrl = geoServerUrl + "/rest/workspaces";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(geoServerAdminUsername, geoServerAdminPassword);
        headers.setContentType(MediaType.APPLICATION_XML);

        String requestBody = "<workspace><name>" + workspaceName + "</name></workspace>";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> response = restTemplate.exchange(
                createWorkspaceUrl,
                HttpMethod.POST,
                requestEntity,
                String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            logger.info("Workspace created successfully in GeoServer");
        } else {
            logger.error("Failed to create workspace in GeoServer. Status code: " + response.getStatusCode());
        }
    }

    private void createLayer() {
        logger.info("Creating countries layer in GeoServer");
        String createLayerUrl = geoServerUrl + "/rest/workspaces/" + workspaceName + "/datastores/" + storeName + "/featuretypes";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(geoServerAdminUsername, geoServerAdminPassword);
        headers.setContentType(MediaType.APPLICATION_XML);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", layerName);
        // Add more configuration properties as needed

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createLayerUrl,
                HttpMethod.POST,
                requestEntity,
                String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            logger.info("Layer created successfully in GeoServer");
        } else {
            logger.error("Failed to create layer in GeoServer. Status code: " + response.getStatusCode());
        }
    }

    private void createPostGISDataStore() {
        logger.info("Creating postGis data store in GeoServer");
        String[] url = postgisUrl.split("/");
        String database = url[3];
        String[] databaseHostAndPort = url[2].split(":");
        String createStoreUrl = geoServerUrl + "/rest/workspaces/" + workspaceName + "/datastores";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(geoServerAdminUsername, geoServerAdminPassword);
        headers.setContentType(MediaType.APPLICATION_XML);

        String requestBody = "<dataStore>" +
                "<name>" + storeName + "</name>" +
                "<connectionParameters>" +
                "<host>" + databaseHostAndPort[0] + "</host>" +
                "<port>" + databaseHostAndPort[1] + "</port>" +
                "<database>" + database + "</database>" +
                "<user>" + postgisUsername + "</user>" +
                "<passwd>" + postgisPassword + "</passwd>" +
                "<dbtype>postgis</dbtype>" +
                "<schema>" + postgisSchema + "</schema>" +
                "</connectionParameters>" +
                "</dataStore>";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createStoreUrl,
                HttpMethod.POST,
                requestEntity,
                String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            logger.info("PostGIS data store created successfully in GeoServer");
        } else {
            logger.error("Failed to create PostGIS data store in GeoServer. Status code: " + response.getStatusCode());
        }
    }
}
