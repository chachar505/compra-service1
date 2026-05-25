package com.gameup.compra_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private BigDecimal billetera;
    private boolean cuentaBloqueada;
}