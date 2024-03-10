package com.paperized.shopapi;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.shopapi.dquery.sort.DComparable;
import com.paperized.shopapi.dquery.sort.DSort;
import com.paperized.shopapi.dquery.sort.SortPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableRetry
@EnableScheduling
public class ShopapiApplication implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(ShopapiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ShopapiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<AmazonProductDto> dtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AmazonProductDto temp = new AmazonProductDto();
            temp.setCurrentPrice(new Random().nextDouble() * 100);
            temp.setSuggestedPrice(new Random().nextDouble() * 1000);
            dtos.add(temp);
        }

        //logger.info("Prima: {}", dtos);

        // I want cheaper prices that should be higher in price, not a real metric but for the tests sake
        DSort sort = new DSort()
                .addSort("currentPrice", SortPair.SortDirection.ASC)
                .addSort("suggestedPrice", SortPair.SortDirection.DESC);

        dtos.sort(new DComparable(sort));

        //logger.info("Dopo: {}", dtos);
    }
}
