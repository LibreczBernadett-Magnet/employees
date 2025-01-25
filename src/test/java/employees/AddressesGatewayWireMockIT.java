package employees;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.boot.web.client.RestTemplateBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

class AddressesGatewayWireMockIT {

    static final String host = "localhost";

    static final int port = 8081;

    static WireMockServer wireMockServer;

    @BeforeAll
    static void startServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(port));
        WireMock.configureFor(host, port);
        wireMockServer.start();
    }

    @AfterAll
    static void stopServer() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetServer() {
        wireMockServer.resetAll();
    }

    @Test
    void testFindAddress() throws JsonProcessingException {
        String resource = "/api/addresses";

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new AddressDto("Budapest", "Fő út"));

        stubFor(get(urlPathEqualTo(resource))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)
                )
        );

        AddressesGateway addressesGateway = new AddressesGateway(new RestTemplateBuilder());
        AddressDto addressDto = addressesGateway.findAddressByName("John Doe");

        verify(getRequestedFor(urlPathEqualTo(resource)).withQueryParam("name", equalTo("John Doe")));

        assertEquals("Budapest", addressDto.getCity());
    }
}