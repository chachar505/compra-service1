package com.gameup.compra_service.service;

import com.gameup.compra_service.client.JuegoFeignClient;
import com.gameup.compra_service.client.UsuarioFeignClient;
import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.dto.JuegoDTO;
import com.gameup.compra_service.dto.UserDTO;
import com.gameup.compra_service.exception.BusinessException;
import com.gameup.compra_service.exception.ResourceNotFoundException;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.repository.CompraRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioFeignClient usuarioFeignClient;
    private final JuegoFeignClient juegoFeignClient;

    public Compra ejecutarCompra(CompraRequestDTO dto) {
        log.info("Iniciando compra para usuario id: {}, juego id: {}", dto.getUsuarioId(), dto.getJuegoId());

        UserDTO usuario;
        try {
            usuario = usuarioFeignClient.obtenerUsuarioPorId(dto.getUsuarioId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Usuario con id " + dto.getUsuarioId() + " no encontrado");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con usuario-service: " + e.getMessage());
        }

        JuegoDTO juego;
        try {
            juego = juegoFeignClient.obtenerJuegoPorId(dto.getJuegoId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Juego con id " + dto.getJuegoId() + " no encontrado");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con juego-service: " + e.getMessage());
        }

        if (usuario.isCuentaBloqueada()) {
            throw new BusinessException("La cuenta del usuario está bloqueada");
        }
        if (!juego.getActivo()) {
            throw new BusinessException("El juego no está disponible para la venta");
        }
        if (juego.getStock() <= 0) {
            throw new BusinessException("No hay stock disponible para este juego");
        }
        if (usuario.getBilletera().compareTo(juego.getPrecio()) < 0) {
            throw new BusinessException("Saldo insuficiente en la billetera");
        }

        Compra compra = new Compra();
        compra.setUsuarioId(dto.getUsuarioId());
        compra.setJuegoId(dto.getJuegoId());
        compra.setPrecioPagado(juego.getPrecio());

        Compra guardada = compraRepository.save(compra);
        log.info("Compra registrada correctamente con id: {}", guardada.getId());

        return guardada;
    }

    public List<Compra> obtenerTodas() {
        return compraRepository.findAll();
    }

    public Compra obtenerPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra con id " + id + " no encontrada"));
    }

    public List<Compra> obtenerPorUsuario(Long usuarioId) {
        return compraRepository.findByUsuarioId(usuarioId);
    }
}