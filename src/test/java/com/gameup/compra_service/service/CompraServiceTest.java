package com.gameup.compra_service.service;

import com.gameup.compra_service.client.JuegoFeignClient;
import com.gameup.compra_service.client.UsuarioFeignClient;
import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.dto.JuegoDTO;
import com.gameup.compra_service.dto.UserDTO;
import com.gameup.compra_service.exception.BusinessException;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del CompraService")
class CompraServiceTest {

    @Mock private CompraRepository compraRepository;
    @Mock private UsuarioFeignClient usuarioFeignClient;
    @Mock private JuegoFeignClient juegoFeignClient;

    @InjectMocks
    private CompraService compraService;

    private UserDTO usuarioMock;
    private JuegoDTO juegoMock;
    private CompraRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new UserDTO();
        usuarioMock.setId(1L);
        usuarioMock.setBilletera(new BigDecimal("200.00"));
        usuarioMock.setCuentaBloqueada(false);

        juegoMock = new JuegoDTO();
        juegoMock.setIdJuego(10L);
        juegoMock.setPrecio(new BigDecimal("59.99"));
        juegoMock.setActivo(true);
        juegoMock.setStock(5);

        requestMock = new CompraRequestDTO();
        requestMock.setUsuarioId(1L);
        requestMock.setJuegoId(10L);
    }

    @Test
    @DisplayName("Compra exitosa con saldo y stock suficiente")
    void ejecutarCompra_exitosa() {
        Compra compraMock = new Compra();
        compraMock.setId(1L);
        compraMock.setUsuarioId(1L);
        compraMock.setJuegoId(10L);
        compraMock.setPrecioPagado(new BigDecimal("59.99"));

        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioMock);
        when(juegoFeignClient.obtenerJuegoPorId(10L)).thenReturn(juegoMock);
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);

        Compra resultado = compraService.ejecutarCompra(requestMock);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPrecioPagado()).isEqualByComparingTo("59.99");
        verify(compraRepository).save(any(Compra.class));
    }

    @Test
    @DisplayName("Compra falla si cuenta bloqueada")
    void ejecutarCompra_cuentaBloqueada_lanzaExcepcion() {
        usuarioMock.setCuentaBloqueada(true);
        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioMock);
        when(juegoFeignClient.obtenerJuegoPorId(10L)).thenReturn(juegoMock);

        assertThatThrownBy(() -> compraService.ejecutarCompra(requestMock))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqueada");
    }

    @Test
    @DisplayName("Compra falla si saldo insuficiente")
    void ejecutarCompra_saldoInsuficiente_lanzaExcepcion() {
        usuarioMock.setBilletera(new BigDecimal("10.00"));
        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioMock);
        when(juegoFeignClient.obtenerJuegoPorId(10L)).thenReturn(juegoMock);

        assertThatThrownBy(() -> compraService.ejecutarCompra(requestMock))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Saldo");
    }

    @Test
    @DisplayName("Obtener todas las compras")
    void obtenerTodas_retornaLista() {
        when(compraRepository.findAll()).thenReturn(List.of(new Compra()));
        assertThat(compraService.obtenerTodas()).hasSize(1);
    }
}