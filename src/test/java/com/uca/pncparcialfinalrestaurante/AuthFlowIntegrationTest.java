package com.uca.pncparcialfinalrestaurante;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void flujoCompletoDeRegistroLoginAccesoYRenovacionDeToken() throws Exception {
        String registerBody = """
                {
                  "nombreCompleto": "Cliente de Prueba",
                  "email": "cliente.flujo@test.com",
                  "password": "clave12345"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = """
                {
                  "email": "cliente.flujo@test.com",
                  "password": "clave12345"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        String accessToken = JsonPath.read(loginJson, "$.data.accessToken");
        String refreshToken = JsonPath.read(loginJson, "$.data.refreshToken");

        assertThat(accessToken).isNotBlank();
        assertThat(refreshToken).isNotBlank();

        // Sin token no se puede acceder a un endpoint protegido.
        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isUnauthorized());

        // Con el access token sí se puede acceder.
        mockMvc.perform(get("/api/sucursales")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());

        // El refresh token no sirve como access token para un endpoint protegido.
        mockMvc.perform(get("/api/sucursales")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());

        // Pero sí sirve para renovar el access token.
        String refreshBody = """
                {
                  "refreshToken": "%s"
                }
                """.formatted(refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody))
                .andExpect(status().isOk())
                .andReturn();

        String nuevoAccessToken = JsonPath.read(refreshResult.getResponse().getContentAsString(), "$.data.accessToken");
        assertThat(nuevoAccessToken).isNotBlank();

        mockMvc.perform(get("/api/sucursales")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + nuevoAccessToken))
                .andExpect(status().isOk());
    }
}
