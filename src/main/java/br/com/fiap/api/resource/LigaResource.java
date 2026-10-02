package br.com.fiap.api.resource;

import br.com.fiap.api.bo.LigaBo;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Liga;
import br.com.fiap.api.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/ligas")
public class LigaResource {

    private final LigaBo ligaBo;

    public LigaResource(LigaBo ligaBo) {
        this.ligaBo = ligaBo;
    }

    @GetMapping
    public List<Liga> listar() throws SQLException {
        return ligaBo.listar();
    }

    @GetMapping("/{id}")
    public Liga buscar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ligaBo.buscar(id);
    }

    @GetMapping("/{id}/ranking")
    public List<Usuario> ranking(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return ligaBo.ranking(id);
    }

    @PostMapping
    public ResponseEntity<Liga> cadastrar(@RequestBody Liga liga) throws SQLException, RegraNegocioException {
        Liga criada = ligaBo.cadastrar(liga);
        return ResponseEntity.created(URI.create("/ligas/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public Liga atualizar(@PathVariable int id, @RequestBody Liga liga)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        return ligaBo.atualizar(id, liga);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id)
            throws SQLException, EntidadeNaoEncontradaException, RegraNegocioException {
        ligaBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
