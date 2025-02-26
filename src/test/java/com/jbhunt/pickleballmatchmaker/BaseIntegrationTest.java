//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jbhunt.pickleballmatchmaker;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith({SpringExtension.class})
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT
)
@Testcontainers
public abstract class BaseIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(BaseIntegrationTest.class);
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @LocalServerPort
    protected int port;
    @Autowired
    protected TestRestTemplate restTemplate;

    public BaseIntegrationTest() {
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        MongoDBContainer var10002 = mongoDBContainer;
        Objects.requireNonNull(var10002);
        registry.add("spring.data.mongodb.uri", var10002::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {
        this.restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("user", "password"));
    }
}
