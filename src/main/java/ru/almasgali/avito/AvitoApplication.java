package ru.almasgali.avito;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import ru.almasgali.avito.util.SpringScriptUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
@EnableCaching
@Slf4j
public class AvitoApplication {

	public static void main(String[] args) {
		String initPath = "/resources/db/init.sql";
		String insertPath = "/resources/db/insert.sql";
		String jdbcUrl = "jdbc:postgresql://localhost:5432/banners?currentSchema=banners";
		try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
			SpringScriptUtil.runScript(initPath, connection);
			SpringScriptUtil.runScript(insertPath, connection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		SpringApplication.run(AvitoApplication.class, args);
	}

}
