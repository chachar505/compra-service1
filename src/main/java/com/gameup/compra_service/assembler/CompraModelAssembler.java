package com.gameup.compra_service.assembler;

import com.gameup.compra_service.controller.CompraController;
import com.gameup.compra_service.model.Compra;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CompraModelAssembler implements RepresentationModelAssembler<Compra, EntityModel<Compra>> {

    @Override
    public EntityModel<Compra> toModel(Compra compra) {
        return EntityModel.of(compra,
                linkTo(methodOn(CompraController.class).obtenerPorId(compra.getId())).withSelfRel(),
                linkTo(methodOn(CompraController.class).obtenerTodas()).withRel("compras"),
                linkTo(methodOn(CompraController.class).obtenerPorUsuario(compra.getUsuarioId())).withRel("compras-usuario")
        );
    }
}