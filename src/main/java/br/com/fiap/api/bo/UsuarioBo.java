package br.com.fiap.api.bo;

import br.com.fiap.api.dao.ConversaoDao;
import br.com.fiap.api.dao.LigaDao;
import br.com.fiap.api.dao.UsuarioDao;
import br.com.fiap.api.dao.ValidacaoDao;
import br.com.fiap.api.dto.Carteira;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Liga;
import br.com.fiap.api.model.Usuario;
import br.com.fiap.api.model.Validacao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Service
public class UsuarioBo {

    private final UsuarioDao usuarioDao;
    private final LigaDao ligaDao;
    private final ConversaoDao conversaoDao;
    private final ValidacaoDao validacaoDao;

    public UsuarioBo(UsuarioDao usuarioDao, LigaDao ligaDao, ConversaoDao conversaoDao, ValidacaoDao validacaoDao) {
        this.usuarioDao = usuarioDao;
        this.ligaDao = ligaDao;
        this.conversaoDao = conversaoDao;
        this.validacaoDao = validacaoDao;
    }

    public Usuario cadastrar(Usuario usuario) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validar(usuario);
        verificarVagaNaLiga(usuario.getLigaId());
        // saldo inicial sempre zero: SoulCoins so entram por missoes validadas
        usuario.setSaldo(0);
        usuario.setPontosSemana(0);
        usuarioDao.cadastrar(usuario);
        return usuarioDao.buscar(usuario.getId());
    }

    public List<Usuario> listar() throws SQLException {
        return usuarioDao.listar();
    }

    public Usuario buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return usuarioDao.buscar(id);
    }

    public Usuario atualizar(int id, Usuario usuario) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validar(usuario);
        Usuario atual = usuarioDao.buscar(id);
        // regra: um usuario participa de apenas uma liga; ao trocar, a nova liga precisa ter vaga
        if (!Objects.equals(atual.getLigaId(), usuario.getLigaId())) verificarVagaNaLiga(usuario.getLigaId());
        usuario.setId(id);
        usuarioDao.atualizar(usuario);
        return usuarioDao.buscar(id);
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        usuarioDao.remover(id);
    }

    public Carteira carteira(int id) throws SQLException, EntidadeNaoEncontradaException {
        Usuario usuario = usuarioDao.buscar(id);
        return new Carteira(id, usuario.getSaldo(), ConversaoBo.paraReais(usuario.getSaldo()),
                conversaoDao.listarPorUsuario(id));
    }

    public List<Validacao> validacoes(int id) throws SQLException, EntidadeNaoEncontradaException {
        usuarioDao.buscar(id);
        return validacaoDao.listarPorUsuario(id);
    }

    private void validar(Usuario usuario) throws RegraNegocioException {
        Validador.tamanhoMinimo(usuario.getNome(), 3, "nome");
        Validador.tamanhoMaximo(usuario.getNome(), 100, "nome");
        Validador.email(usuario.getEmail());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
    }

    private void verificarVagaNaLiga(Integer ligaId) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        if (ligaId == null) return;
        Liga liga = ligaDao.buscar(ligaId);
        if (liga.getParticipantes() >= liga.getMaxParticipantes())
            throw new RegraNegocioException("A " + liga.getNome() + " está lotada (" + liga.getMaxParticipantes() + " participantes).");
    }
}
