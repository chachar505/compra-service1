package com.gameup.compra_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameup.compra_service.dto.CompraRequestDTO;
import com.gameup.compra_service.model.Compra;
import com.gameup.compra_service.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompraRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Compra compraExistente;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        compraExistente = new Compra();
        compraExistente.setUsuarioId(1L);
        compraExistente.setJuegoId(2L);
        compraExistente.setPrecioPagado(BigDecimal.valueOf(29.99));
        compraExistente.setFechaCompra(LocalDateTime.now());
        compraExistente = repository.save(compraExistente);
    }

    @Test
    void obtenerTodas_deberiaRetornarLista() throws Exception {
        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.compraList", hasSize(1)))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void obtenerPorId_deberiaRetornarCompra() throws Exception {
        mockMvc.perform(get("/api/compras/{id}", compraExistente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.juegoId").value(2))
                .andExpect(jsonPath("$.precioPagado").value(29.99))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void obtenerPorId_deberiaRetornar404() throws Exception {
        mockMvc.perform(get("/api/compras/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorUsuario_deberiaRetornarCompras() throws Exception {
        mockMvc.perform(get("/api/compras/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.compraList", hasSize(1)));
    }

    @Test
    void crearCompra_conDtoInvalido_deberiaRetornar400() throws Exception {
        CompraRequestDTO dto = new CompraRequestDTO();
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
