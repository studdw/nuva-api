package br.com.fiap.api.resource;

import br.com.fiap.api.bo.ConversaoBo;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Conversao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

/** Conversoes sao registros financeiros: nao ha PUT, apenas criacao e cancelamento com estorno. */
@RestController
@RequestMapping("/conversoes")
public class ConversaoResource {

    private final ConversaoBo conversaoBo;

    public ConversaoResource(ConversaoBo conversaoBo) {
        this.conversaoBo = conversaoBo;
    }

    @GetMapping
    public List<Conversao> listar() throws SQLException {
        return conversaoBo.listar();
    }

    @GetMapping("/{id}")
    public Conversao buscar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return conversaoBo.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Conversao> converter(@RequestBody Conversao conversao)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        Conversao criada = conversaoBo.converter(conversao);
        return ResponseEntity.created(URI.create("/conversoes/" + criada.getId())).body(criada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        conversaoBo.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
