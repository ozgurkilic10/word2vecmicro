package tr.edu.mu.ceng.ir.word2vecmicro.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@EnableAutoConfiguration
@EnableDiscoveryClient
@ComponentScan
public class Word2VecServer {

    protected Logger logger = LoggerFactory.getLogger(Word2VecServer.class.getName());

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "word2vec-server");

        SpringApplication.run(Word2VecServer.class, args);
    }


    @Bean
    @Scope("singleton")
    public Word2VecBean word2VecSingleton() {
        return new Word2VecBean();
    }

}
