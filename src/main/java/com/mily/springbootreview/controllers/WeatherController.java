package com.mily.springbootreview.controllers;

import com.mily.springbootreview.data.request.WeatherRequest;
import com.mily.springbootreview.data.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Api(tags = "取得氣象資訊")
@RestController
@RequestMapping(value = "/api")
public class WeatherController {

    //API Key
    private static final String API_KEY = "CWB-1A2A209B-C7A2-4212-BCBF-8D7C51CE037E";

    //API 的網址
    private static final String API_URL = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001";

    @ApiOperation(value = "取得氣象資訊", notes = "列出所有氣象資訊")
    @GetMapping("/weather")
    public String getWeatherForecast(WeatherRequest weatherRequest) {

        //URL
        String query = "?Authorization=" + API_KEY;
        String url = API_URL + query;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
}
