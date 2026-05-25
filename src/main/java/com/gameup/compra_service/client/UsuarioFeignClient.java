package com.gameup.compra_service.client;

import com.gameup.compra_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-service", url = "${usuario.service.url}")
public interface UsuarioFeignClient {
    @GetMapping("/api/usuarios/{id}")
    UserDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}