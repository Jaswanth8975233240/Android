package com.steppschuh.intelliq.api;

import android.app.Application;
import android.util.Log;

import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import roboguice.util.temp.Ln;

public class JsonSpiceService extends SpringAndroidSpiceService {

    public JsonSpiceService() {
        super();
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    @Override
    public CacheManager createCacheManager(Application application) {
        CacheManager cacheManager = new CacheManager();
        JacksonObjectPersisterFactory jacksonObjectPersisterFactory = null;
        try {
            jacksonObjectPersisterFactory = new JacksonObjectPersisterFactory(application);
        } catch (CacheCreationException e) {
            e.printStackTrace();
        }
        cacheManager.addPersister(jacksonObjectPersisterFactory);
        return cacheManager;
    }

    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        //find more complete examples in RoboSpice Motivation app
        //to enable Gzip compression and setting request timeouts.

        // web services support json responses
        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        final List<HttpMessageConverter< ? >> listHttpMessageConverters = restTemplate.getMessageConverters();

        listHttpMessageConverters.add( jsonConverter );
        listHttpMessageConverters.add( formHttpMessageConverter );
        listHttpMessageConverters.add( stringHttpMessageConverter );
        restTemplate.setMessageConverters( listHttpMessageConverters );
        return restTemplate;
    }
}