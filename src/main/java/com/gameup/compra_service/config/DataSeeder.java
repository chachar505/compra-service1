package com.gameup.compra_service.config;

import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder {

    private final CompraRepository compraRepository;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            if (compraRepository.count() > 0) {
                log.info("La base de datos ya contiene datos. Omitiendo seed.");
                return;
            }

            Faker faker = new Faker();
            for (int i = 0; i < 20; i++) {
                Compra compra = new Compra();
                compra.setUsuarioId((long) faker.number().numberBetween(1, 10));
                compra.setJuegoId((long) faker.number().numberBetween(1, 30));
                compra.setPrecioPagado(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 80)));
                compra.setFechaCompra(LocalDateTime.ofInstant(
                        faker.date().past(30, TimeUnit.DAYS).toInstant(),
                        ZoneId.systemDefault()));
                compraRepository.save(compra);
            }

            log.info("Se insertaron 20 compras de prueba correctamente.");
        };
    }
}
