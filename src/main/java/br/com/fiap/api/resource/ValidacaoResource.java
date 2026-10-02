package br.com.fiap.api.resource;

import br.com.fiap.api.bo.ValidacaoBo;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Validacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/validacoes")
public class ValidacaoResource {

    private final ValidacaoBo validacaoBo;

    public ValidacaoResource(ValidacaoBo validacaoBo) {
        this.validacaoBo = validacaoBo;
    }

    @GetMapping
    public List<Validacao> listar() throws SQLException {
        return validacaoBo.listar();
    }

    @GetMapping("/{id}")
    public Validacao buscar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return validacaoBo.buscar(id);
    }

    /** Envio do video para a IA. Retorna 201 com status VALIDADO ou RECUSADO. */
    @PostMapping
    public ResponseEntity<Validacao> enviar(@RequestBody Validacao validacao)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        Validacao criada = validacaoBo.enviar(validacao);
        return ResponseEntity.created(URI.create("/validacoes/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public Validacao atualizar(@PathVariable int id, @RequestBody Validacao validacao)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        return validacaoBo.atualizar(id, validacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        validacaoBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
