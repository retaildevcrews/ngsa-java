package com.cse.ngsa.app.controllers;

import com.cse.ngsa.app.models.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;


@AutoConfigureWebTestClient(timeout = "20000")
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application.properties")
@SpringBootTest(properties = {"ngsa.keyvault.name=${KeyVaultName}", "ngsa.environment.flag=${AUTH_TYPE}"})

public class FeaturedMovieControllerTest {
  @Autowired
  private WebTestClient webClient;

    @Test
    public void testFeaturedMovie(){
    webClient
        .get()
        .uri("/api/featured/movie")
        .header(HttpHeaders.ACCEPT, "application/json")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Movie.class);
    }
}


