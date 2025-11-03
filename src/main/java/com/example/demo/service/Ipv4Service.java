package com.example.demo.service;

import com.example.demo.model.IpInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
public class Ipv4Service {

    private final RestTemplate restTemplate ;
    private final String ipApiUrl;
    private static final Set<String> BLOCKED_COUNTRIES = Set.of("China", "Spain", "United States");

    @Autowired
    public Ipv4Service(RestTemplate restTemplate, @Value("${ipapi.url}") String ipApiUrl){
        this.restTemplate = restTemplate;
        this.ipApiUrl = ipApiUrl + "/";
    }

    public IpInformation getInformationByIp(String ip) {
        return restTemplate.getForObject(ipApiUrl + ip, IpInformation.class);
    }

    public boolean isIpBlocked(IpInformation ipInformation) {
        if (BLOCKED_COUNTRIES.contains(ipInformation.country())) {
            return true;
        }
        return false;
    }

}
