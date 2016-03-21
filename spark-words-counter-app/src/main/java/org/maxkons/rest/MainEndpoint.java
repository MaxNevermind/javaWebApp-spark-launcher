package org.maxkons.rest;

import org.maxkons.model.JobResult;
import org.maxkons.service.SparkService;
import org.maxkons.sparkwordscounterjob.SparkJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
public class MainEndpoint {

    @Autowired
    private SparkService sparkService;

    @RequestMapping(value = "/getWordsCounter", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getWordsCount(@RequestBody Map<String, String> requestBody){
        sparkService.countWords(requestBody.get("hdfsInFilePath"));
    }

    @RequestMapping(value = "/getJobResults", method = RequestMethod.GET)
    public List<JobResult> getJobResults(){
        return sparkService.getJobResults();

    }

}
