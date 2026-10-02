package br.com.fiap.api.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Health check usado pelo Render e para "acordar" a API. */
@RestController
public class StatusResource {

    @GetMapping("/")
    public Map<String, String> status() {
        return Map.of("api", "NUVA - Guardiões da Luz", "status", "online");
    }
}
