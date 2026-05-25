package com.gameup.compra_service.client;

import com.gameup.compra_service.dto.JuegoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "juego-service", url = "${juego.service.url}")
public interface JuegoFeignClient {
    @GetMapping("/api/juegos/{id}")
    JuegoDTO obtenerJuegoPorId(@PathVariable("id") Long id);
}