package com.example.demo.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WireMockTest(httpPort = 8090)
@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String dummyIp = "1.1.1.1";
    private static final String errorMessage = """
            {"error":"Access denied for IP %s (%s, Dummy ISP)"}
            """;

    @BeforeEach
    void reset() {
        WireMock.reset();
    }

    @DynamicPropertySource
    static void overrideIpApiUrl(DynamicPropertyRegistry registry) {
        registry.add("ipapi.url", () -> "http://localhost:8090/json");
    }

    @Test
    public void shouldProcessCorrectFile() throws Exception {
        stubTheIpAddress("United Kingdom", "Dummy ISP");

        MvcResult result = mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-correct"))
                        .param("validate", "true")
                        .with(remoteAddr(dummyIp)))
                .andExpect(status().isOk())
                .andReturn();

        ClassPathResource resource = new ClassPathResource("file-correct-response.json");

        String expected = Files.readString(resource.getFile().toPath());
        assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    public void shouldBlockChinaAddress() throws Exception {
        shouldBlockForbiddenAddress("China");
    }

    @Test
    public void shouldBlockSpainAddress() throws Exception {
        shouldBlockForbiddenAddress("Spain");
    }

    @Test
    public void shouldBlockUnitedStatesAddress() throws Exception {
        shouldBlockForbiddenAddress("United States");
    }

    private void shouldBlockForbiddenAddress(String forbiddenCountry) throws Exception {
        stubTheIpAddress(forbiddenCountry, "Dummy ISP");

        MvcResult result = mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-correct"))
                        .with(remoteAddr(dummyIp)))
                .andExpect(status().isForbidden())
                .andReturn();

        String expected = errorMessage.formatted(dummyIp, forbiddenCountry);
        assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    public void shouldReturn400_IncorrectUUID() throws Exception {
        stubTheIpAddress("United Kingdom", "Dummy ISP");
        mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-bad-uuid"))
                        .param("validate", "true")
                        .with(remoteAddr(dummyIp)))
                .andExpect(status()
                        .is4xxClientError());
    }

    @Test
    public void shouldReturn400_IncorrectID() throws Exception {
        stubTheIpAddress("United Kingdom", "Dummy ISP");
        mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-bad-id"))
                        .param("validate", "true")
                        .with(remoteAddr(dummyIp)))
                .andExpect(status()
                        .is4xxClientError());
    }

    @Test
    public void shouldReturn200_IncorrectID_validationDisabled() throws Exception {
        stubTheIpAddress("United Kingdom", "Dummy ISP");
        mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-bad-id"))
                        .with(remoteAddr(dummyIp)))
                .andExpect(status()
                        .isOk());
    }

    @Test
    public void shouldReturn400_IncorrectName() throws Exception {
        stubTheIpAddress("United Kingdom", "Dummy ISP");
        mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-bad-name"))
                        .param("validate", "true")
                        .with(remoteAddr(dummyIp)))
                .andExpect(status()
                        .is4xxClientError());
    }

    @Test
    public void shouldReturn400_IncorrectLikes() throws Exception {
        stubTheIpAddress("United Kingdom", "Dummy ISP");
        mockMvc.perform(multipart("/v1/files/upload")
                        .file(mockMultipartFile("file-bad-likes"))
                        .param("validate", "true")
                        .with(remoteAddr(dummyIp)))
                .andExpect(status()
                        .is4xxClientError());
    }

    private MockMultipartFile mockMultipartFile(String filename) throws IOException {
        var resource = new ClassPathResource(filename);
        return new MockMultipartFile(
                "file",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(resource.getFile().toPath())
        );
    }

    private static RequestPostProcessor remoteAddr(String ip) {
        return req -> {
            req.setRemoteAddr(ip);
            return req;
        };
    }

    private static void stubTheIpAddress(String country, String isp) {
        stubFor(get(urlEqualTo("/json/" + dummyIp))
                .willReturn(okJson("""
                            {
                              "query": "%s",
                              "country": "%s",
                              "isp": "%s"
                            }
                        """.formatted(dummyIp, country, isp))));
    }

}