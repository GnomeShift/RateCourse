package com.gnomeshift.ratingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RatingServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RatingServiceApplication.class);
        app.addInitializers(new DotenvPropertyInitializer());
        app.run(args);
    }

}
