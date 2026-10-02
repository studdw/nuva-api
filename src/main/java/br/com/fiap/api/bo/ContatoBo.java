package br.com.fiap.api.bo;

import br.com.fiap.api.dao.ContatoDao;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Contato;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ContatoBo {

    private static final Set<String> ASSUNTOS = Set.of(
            "Dúvida sobre missões", "SoulCoins e conversão", "Ligas", "Parcerias", "Outro");

    private final ContatoDao contatoDao;

    public ContatoBo(ContatoDao contatoDao) {
        this.contatoDao = contatoDao;
    }

    public Contato cadastrar(Contato c) throws SQLException, RegraNegocioException {
        validar(c);
        contatoDao.cadastrar(c);
        c.setDataEnvio(LocalDateTime.now());
        return c;
    }

    public List<Contato> listar() throws SQLException {
        return contatoDao.listar();
    }

    public Contato buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return contatoDao.buscar(id);
    }

    public Contato atualizar(int id, Contato c) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validar(c);
        c.setId(id);
        contatoDao.atualizar(c);
        return contatoDao.buscar(id);
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        contatoDao.remover(id);
    }

    private void validar(Contato c) throws RegraNegocioException {
        Validador.tamanhoMinimo(c.getNome(), 3, "nome");
        Validador.tamanhoMaximo(c.getNome(), 100, "nome");
        Validador.email(c.getEmail());
        if (!ASSUNTOS.contains(c.getAssunto())) throw new RegraNegocioException("Assunto inválido. Use: " + ASSUNTOS);
        Validador.tamanhoMinimo(c.getMensagem(), 10, "mensagem");
        Validador.tamanhoMaximo(c.getMensagem(), 1000, "mensagem");
    }
}
