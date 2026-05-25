package com.gameup.compra_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompraRequestDTO {
    @NotNull(message = "El id de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id de juego es obligatorio")
    private Long juegoId;
}