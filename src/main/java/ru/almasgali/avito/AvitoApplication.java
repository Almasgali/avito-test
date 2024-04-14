package ru.almasgali.avito;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.almasgali.avito.util.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
@EnableCaching
@Slf4j
@EnableJpaRepositories("ru.almasgali.avito.*")
@ComponentScan(basePackages = { "ru.almasgali.avito.*" })
@EntityScan("ru.almasgali.avito.*")
public class AvitoApplication {

	public static void main(String[] args) {
		try {
			Util.initDB();
		} catch (SQLException e) {
			log.error(e.getMessage());
			return;
		}
		SpringApplication.run(AvitoApplication.class, args);
	}

}
