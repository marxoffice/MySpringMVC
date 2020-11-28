package test;

import annotation.Bean;
import annotation.ComponentScan;
import annotation.Configuration;

@Configuration
@ComponentScan
public class App {
    @Bean
    MsgService mockMessageService() {
        return new MsgService() {
            @Override
            public String getMessage() {
                return "mockMessageService: Hello World!";
            }
        };
    }

    @Bean
    String buffer() {
        return "Application: buffer";
    }

    @Bean
    Integer integer() {
        return 123456;
    }
}
