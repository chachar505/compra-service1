package com.gameup.compra_service.controller;

import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "API para la gestión de compras de juegos")
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    @Operation(summary = "Ejecutar una compra", description = "Registra una nueva compra validando usuario, juego, stock y saldo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación o negocio"),
            @ApiResponse(responseCode = "404", description = "Usuario o juego no encontrado")
    })
    public ResponseEntity<EntityModel<Compra>> ejecutarCompra(@Valid @RequestBody CompraRequestDTO dto) {
        Compra compra = compraService.ejecutarCompra(dto);
        EntityModel<Compra> model = toModel(compra);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las compras")
    public ResponseEntity<CollectionModel<EntityModel<Compra>>> obtenerTodas() {
        List<EntityModel<Compra>> compras = compraService.obtenerTodas().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<Compra>> collection = CollectionModel.of(compras,
                linkTo(methodOn(CompraController.class).obtenerTodas()).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener compra por ID")
    @ApiResponse(responseCode = "404", description = "Compra no encontrada")
    public ResponseEntity<EntityModel<Compra>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toModel(compraService.obtenerPorId(id)));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener compras por ID de usuario")
    public ResponseEntity<CollectionModel<EntityModel<Compra>>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<Compra>> compras = compraService.obtenerPorUsuario(usuarioId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<Compra>> collection = CollectionModel.of(compras,
                linkTo(methodOn(CompraController.class).obtenerPorUsuario(usuarioId)).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    private EntityModel<Compra> toModel(Compra compra) {
        return EntityModel.of(compra,
                linkTo(methodOn(CompraController.class).obtenerPorId(compra.getId())).withSelfRel(),
                linkTo(methodOn(CompraController.class).obtenerTodas()).withRel("todas"),
                linkTo(methodOn(CompraController.class).obtenerPorUsuario(compra.getUsuarioId())).withRel("por-usuario"));
    }
}