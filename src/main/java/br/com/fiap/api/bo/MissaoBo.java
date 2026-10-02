package br.com.fiap.api.bo;

import br.com.fiap.api.dao.MissaoDao;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Missao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Service
public class MissaoBo {

    private static final Set<String> DIFICULDADES = Set.of("Fácil", "Média", "Desafiadora");
    private static final Set<String> CORES = Set.of("iris", "orchid", "periwinkle", "pale-iris", "deep-iris", "cyan");

    private final MissaoDao missaoDao;

    public MissaoBo(MissaoDao missaoDao) {
        this.missaoDao = missaoDao;
    }

    public Missao cadastrar(Missao missao) throws SQLException, RegraNegocioException {
        validar(missao);
        missao.setAtiva(true);
        missaoDao.cadastrar(missao);
        return missao;
    }

    public List<Missao> listar(boolean somenteAtivas) throws SQLException {
        return missaoDao.listar(somenteAtivas);
    }

    public Missao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return missaoDao.buscar(id);
    }

    public Missao atualizar(int id, Missao missao) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validar(missao);
        Missao atual = missaoDao.buscar(id);
        // regra: missao desativada nao pode ser reativada com o mesmo identificador
        if (!atual.isAtiva() && missao.isAtiva())
            throw new RegraNegocioException("Missões desativadas não podem ser reativadas. Cadastre uma nova missão.");
        missao.setId(id);
        missaoDao.atualizar(missao);
        return missao;
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        missaoDao.remover(id);
    }

    private void validar(Missao m) throws RegraNegocioException {
        Validador.tamanhoMinimo(m.getTitulo(), 5, "titulo");
        Validador.tamanhoMaximo(m.getTitulo(), 120, "titulo");
        Validador.obrigatorio(m.getCategoria(), "categoria");
        Validador.obrigatorio(m.getDuracao(), "duracao");
        Validador.tamanhoMinimo(m.getResumo(), 10, "resumo");
        Validador.tamanhoMaximo(m.getResumo(), 300, "resumo");
        // regra: toda missao precisa de um criterio objetivo de validacao
        Validador.tamanhoMinimo(m.getCriterio(), 10, "criterio");
        Validador.intervalo(m.getRecompensa(), 10, 1000, "recompensa");
        if (!DIFICULDADES.contains(m.getDificuldade()))
            throw new RegraNegocioException("Dificuldade deve ser: Fácil, Média ou Desafiadora.");
        if (m.getCor() == null || m.getCor().isBlank()) m.setCor("iris");
        if (!CORES.contains(m.getCor())) throw new RegraNegocioException("Cor inválida. Use: " + CORES);
        if (m.getPassos() == null || m.getPassos().isEmpty() || m.getPassos().stream().anyMatch(p -> p == null || p.isBlank()))
            throw new RegraNegocioException("Informe ao menos um passo, sem itens vazios.");
        if (m.getPassos().stream().anyMatch(p -> p.contains("|")))
            throw new RegraNegocioException("Os passos não podem conter o caractere '|'.");
        if (String.join("|", m.getPassos()).length() > 1000)
            throw new RegraNegocioException("Os passos somados devem ter no máximo 1000 caracteres.");
    }
}
