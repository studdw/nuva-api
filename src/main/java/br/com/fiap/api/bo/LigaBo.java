package br.com.fiap.api.bo;

import br.com.fiap.api.dao.LigaDao;
import br.com.fiap.api.dao.UsuarioDao;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Liga;
import br.com.fiap.api.model.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class LigaBo {

    /** Regra: ligas suportam no maximo 200 participantes. */
    public static final int LIMITE_PARTICIPANTES = 200;

    private final LigaDao ligaDao;
    private final UsuarioDao usuarioDao;

    public LigaBo(LigaDao ligaDao, UsuarioDao usuarioDao) {
        this.ligaDao = ligaDao;
        this.usuarioDao = usuarioDao;
    }

    public Liga cadastrar(Liga liga) throws SQLException, RegraNegocioException {
        validar(liga);
        ligaDao.cadastrar(liga);
        return liga;
    }

    public List<Liga> listar() throws SQLException {
        return ligaDao.listar();
    }

    public Liga buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return ligaDao.buscar(id);
    }

    /** Ranking semanal: participantes ordenados por pontos da semana. */
    public List<Usuario> ranking(int id) throws SQLException, EntidadeNaoEncontradaException {
        ligaDao.buscar(id);
        return usuarioDao.listarPorLiga(id);
    }

    public Liga atualizar(int id, Liga liga) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validar(liga);
        Liga atual = ligaDao.buscar(id);
        if (liga.getMaxParticipantes() < atual.getParticipantes())
            throw new RegraNegocioException("O limite não pode ser menor que o número atual de participantes (" + atual.getParticipantes() + ").");
        liga.setId(id);
        ligaDao.atualizar(liga);
        return ligaDao.buscar(id);
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException, RegraNegocioException {
        if (ligaDao.buscar(id).getParticipantes() > 0)
            throw new RegraNegocioException("Não é possível remover uma liga com participantes.");
        ligaDao.remover(id);
    }

    private void validar(Liga liga) throws RegraNegocioException {
        Validador.tamanhoMinimo(liga.getNome(), 3, "nome");
        Validador.tamanhoMaximo(liga.getNome(), 80, "nome");
        Validador.obrigatorio(liga.getRegiao(), "regiao");
        Validador.tamanhoMaximo(liga.getRegiao(), 80, "regiao");
        if (liga.getMaxParticipantes() == 0) liga.setMaxParticipantes(LIMITE_PARTICIPANTES);
        Validador.intervalo(liga.getMaxParticipantes(), 2, LIMITE_PARTICIPANTES, "maxParticipantes");
    }
}
