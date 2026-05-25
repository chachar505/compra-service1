package com.gameup.compra_service.controller;

import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<Compra> ejecutarCompra(@Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.ejecutarCompra(dto));
    }

    @GetMapping
    public ResponseEntity<List<Compra>> obtenerTodas() {
        return ResponseEntity.ok(compraService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Compra>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(compraService.obtenerPorUsuario(usuarioId));
    }
}