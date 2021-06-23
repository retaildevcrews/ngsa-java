package com.cse.ngsa.app.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.junit.Assert.*;
import com.cse.ngsa.app.models.NgsaConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;



@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = NgsaConfig.class)
public class VersionControllerTest {

  @Mock
  private VersionController versionController;

  @Test
  public void testVersion() {
    assertNotNull(versionController);
    WebTestClient webClient = WebTestClient.bindToController(versionController)
        .configureClient()
        .baseUrl("/")
        .build();

    assertNotNull(webClient);
    webClient
        .get()
        .uri("/version")
        .exchange()
        .expectStatus()
        .isOk();
  }


}


