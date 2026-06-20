package com.gameup.compra_service;

import com.gameup.compra_service.client.JuegoFeignClient;
import com.gameup.compra_service.client.UsuarioFeignClient;
import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.dto.JuegoDTO;
import com.gameup.compra_service.dto.UserDTO;
import com.gameup.compra_service.exception.BusinessException;
import com.gameup.compra_service.exception.ResourceNotFoundException;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.repository.CompraRepository;
import com.gameup.compra_service.service.CompraService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @Mock
    private JuegoFeignClient juegoFeignClient;

    @InjectMocks
    private CompraService compraService;

    @Test
    void ejecutarCompra_deberiaCrearCompra() {
        CompraRequestDTO dto = new CompraRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);

        UserDTO usuario = new UserDTO();
        usuario.setId(1L);
        usuario.setBilletera(BigDecimal.valueOf(100));
        usuario.setCuentaBloqueada(false);

        JuegoDTO juego = new JuegoDTO();
        juego.setIdJuego(1L);
        juego.setPrecio(BigDecimal.valueOf(29.99));
        juego.setStock(10);
        juego.setActivo(true);

        Compra compra = new Compra();
        compra.setId(1L);
        compra.setUsuarioId(1L);
        compra.setJuegoId(1L);
        compra.setPrecioPagado(BigDecimal.valueOf(29.99));

        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        when(juegoFeignClient.obtenerJuegoPorId(1L)).thenReturn(juego);
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);

        Compra resultado = compraService.ejecutarCompra(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPrecioPagado()).isEqualByComparingTo(BigDecimal.valueOf(29.99));

        verify(usuarioFeignClient).obtenerUsuarioPorId(1L);
        verify(juegoFeignClient).obtenerJuegoPorId(1L);
        verify(compraRepository).save(any(Compra.class));
    }

    @Test
    void ejecutarCompra_cuentaBloqueada_deberiaLanzarExcepcion() {
        CompraRequestDTO dto = new CompraRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);

        UserDTO usuario = new UserDTO();
        usuario.setCuentaBloqueada(true);

        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);

        assertThatThrownBy(() -> compraService.ejecutarCompra(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqueada");
    }

    @Test
    void ejecutarCompra_sinStock_deberiaLanzarExcepcion() {
        CompraRequestDTO dto = new CompraRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);

        UserDTO usuario = new UserDTO();
        usuario.setCuentaBloqueada(false);
        usuario.setBilletera(BigDecimal.valueOf(100));

        JuegoDTO juego = new JuegoDTO();
        juego.setActivo(true);
        juego.setStock(0);
        juego.setPrecio(BigDecimal.valueOf(29.99));

        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        when(juegoFeignClient.obtenerJuegoPorId(1L)).thenReturn(juego);

        assertThatThrownBy(() -> compraService.ejecutarCompra(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("stock");
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(compraRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> compraService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }
}
