package com.paperized.shopapi;

import com.paperized.shopapi.dquery.EqualsCondition;
import com.paperized.shopapi.dquery.QueryCondition;
import com.paperized.shopapi.model.RegisteredProductTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

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
		QueryCondition queryCondition = new QueryCondition()
				.addOrCondition(EqualsCondition.fromBinaryCondition("id", "abc").toQueryCondition())
				.addOrCondition(EqualsCondition.fromBinaryCondition("webhookUrl", "http://localhost:8080/hook").toQueryCondition());

		RegisteredProductTracking ent = new RegisteredProductTracking();
		ent.setId("aabbcc");
		ent.setWebhookUrl("http://localhost:8080/hook");

		logger.info("Evaluation: {}", queryCondition.evaluate(ent));
	}
}
