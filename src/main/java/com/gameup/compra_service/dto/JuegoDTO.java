package com.gameup.compra_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class JuegoDTO {
    private Long idJuego;
    private String nombreJuego;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;
}