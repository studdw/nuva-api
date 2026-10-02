package br.com.fiap.api.resource;

import br.com.fiap.api.bo.MissaoBo;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Missao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/missoes")
public class MissaoResource {

    private final MissaoBo missaoBo;

    public MissaoResource(MissaoBo missaoBo) {
        this.missaoBo = missaoBo;
    }

    /** GET /missoes?todas=true inclui as desativadas (painel do operador). */
    @GetMapping
    public List<Missao> listar(@RequestParam(defaultValue = "false") boolean todas) throws SQLException {
        return missaoBo.listar(!todas);
    }

    @GetMapping("/{id}")
    public Missao buscar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return missaoBo.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Missao> cadastrar(@RequestBody Missao missao) throws SQLException, RegraNegocioException {
        Missao criada = missaoBo.cadastrar(missao);
        return ResponseEntity.created(URI.create("/missoes/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public Missao atualizar(@PathVariable int id, @RequestBody Missao missao)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        return missaoBo.atualizar(id, missao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        missaoBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
