package tr.edu.mu.ceng.ir.word2vecmicro.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

@SpringBootApplication
public class Word2VecClient implements CommandLineRunner {

    private static final String W2V_SERVICE_URL = "http://word2vec-service";

    private Logger logger = LoggerFactory.getLogger(Word2VecClient.class
            .getName());

    @Autowired
    protected RestTemplate restTemplate;

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "word2vec-client");
        SpringApplication.run(Word2VecClient.class, args);

    }



    @Override
    public void run(String... args) {


        Scanner input = new Scanner(System.in);
        System.out.print("Enter the word: ");
        String word = input.nextLine();
        System.out.print("number of closest words: ");
        int count = input.nextInt();

        String result = restTemplate.getForObject(W2V_SERVICE_URL + "/nearest?word={word}&count={count}", String.class, word, count);

        System.out.println("Result: " + result);
    }





}
