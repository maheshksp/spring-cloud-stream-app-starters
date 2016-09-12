package org.springframework.cloud.stream.app.tail.source;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TailSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TailSourceApplication.class, args);
    }

}