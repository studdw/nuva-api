package br.com.fiap.api.resource;

import br.com.fiap.api.bo.UsuarioBo;
import br.com.fiap.api.dto.Carteira;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Usuario;
import br.com.fiap.api.model.Validacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    private final UsuarioBo usuarioBo;

    public UsuarioResource(UsuarioBo usuarioBo) {
        this.usuarioBo = usuarioBo;
    }

    @GetMapping
    public List<Usuario> listar() throws SQLException {
        return usuarioBo.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return usuarioBo.buscar(id);
    }

    @GetMapping("/{id}/carteira")
    public Carteira carteira(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return usuarioBo.carteira(id);
    }

    @GetMapping("/{id}/validacoes")
    public List<Validacao> validacoes(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return usuarioBo.validacoes(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        Usuario criado = usuarioBo.cadastrar(usuario);
        return ResponseEntity.created(URI.create("/usuarios/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable int id, @RequestBody Usuario usuario)
            throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        return usuarioBo.atualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        usuarioBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
