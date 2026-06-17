package com.gameup.compra_service.controller;

import com.gameup.compra_service.assembler.CompraModelAssembler;
import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Métodos del microservicio de compras")
public class CompraController {

    private final CompraService compraService;
    private final CompraModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Ejecutar una compra", description = "Registra una compra de un juego por parte de un usuario")
    public ResponseEntity<EntityModel<Compra>> ejecutarCompra(@Valid @RequestBody CompraRequestDTO dto) {
        Compra compra = compraService.ejecutarCompra(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(compra));
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar todas las compras", description = "Retorna todas las compras registradas")
    public CollectionModel<EntityModel<Compra>> obtenerTodas() {
        List<EntityModel<Compra>> compras = compraService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(compras,
                linkTo(methodOn(CompraController.class).obtenerTodas()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener compra por ID")
    public EntityModel<Compra> obtenerPorId(
            @Parameter(description = "ID de la compra", required = true)
            @PathVariable Long id) {
        return assembler.toModel(compraService.obtenerPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar compras de un usuario", description = "Retorna todas las compras de un usuario por su ID")
    public CollectionModel<EntityModel<Compra>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        List<EntityModel<Compra>> compras = compraService.obtenerPorUsuario(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(compras,
                linkTo(methodOn(CompraController.class).obtenerPorUsuario(usuarioId)).withSelfRel());
    }
}