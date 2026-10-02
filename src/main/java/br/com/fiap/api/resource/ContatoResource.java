package br.com.fiap.api.resource;

import br.com.fiap.api.bo.ContatoBo;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Contato;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/contatos")
public class ContatoResource {

    private final ContatoBo contatoBo;

    public ContatoResource(ContatoBo contatoBo) {
        this.contatoBo = contatoBo;
    }

    @GetMapping
    public List<Contato> listar() throws SQLException {
        return contatoBo.listar();
    }

    @GetMapping("/{id}")
    public Contato buscar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return contatoBo.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Contato> cadastrar(@RequestBody Contato contato) throws SQLException, RegraNegocioException {
        Contato criado = contatoBo.cadastrar(contato);
        return ResponseEntity.created(URI.create("/contatos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Contato atualizar(@PathVariable int id, @RequestBody Contato contato)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        return contatoBo.atualizar(id, contato);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        contatoBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
