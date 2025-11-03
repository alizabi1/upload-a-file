package com.example.demo.service;

import com.example.demo.model.IpInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class Ipv4ServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private IpInformation ipInformation;

    private Ipv4Service ipv4Service;

    private static final String IP_API_URL = "http://ip-api.com/json";

    @BeforeEach
    public void init() {
        when(restTemplate.getForObject(any(), any())).thenReturn(null);
        ipv4Service = new Ipv4Service(restTemplate, IP_API_URL);
    }

    @Test
    public void shouldCallExternalApi() {
        final String ipAddress = "1.1.1.1";
        ipv4Service.getInformationByIp(ipAddress);
        verify(restTemplate).getForObject(IP_API_URL + "/" + ipAddress, IpInformation.class);
    }

    @Test
    public void shouldReturnBlockedAsTrueForChina() {
        when(ipInformation.country()).thenReturn("China");
        assertThat(ipv4Service.isIpBlocked(ipInformation)).isTrue();
    }

    @Test
    public void shouldReturnBlockedAsTrueForSpain() {
        when(ipInformation.country()).thenReturn("Spain");
        assertThat(ipv4Service.isIpBlocked(ipInformation)).isTrue();
    }

    @Test
    public void shouldReturnBlockedAsTrueForUS() {
        when(ipInformation.country()).thenReturn("United States");
        assertThat(ipv4Service.isIpBlocked(ipInformation)).isTrue();
    }

    @Test
    public void shouldReturnBlockedAsFalseForNotListedCountries() {
        when(ipInformation.country()).thenReturn("United Kingdom");
        assertThat(ipv4Service.isIpBlocked(ipInformation)).isFalse();
    }
}