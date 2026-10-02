package br.com.fiap.api.bo;

import br.com.fiap.api.dao.ConversaoDao;
import br.com.fiap.api.dao.UsuarioDao;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Conversao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Regras: minimo de 100 SoulCoins e taxa fixa 100 SC = R$ 1,00. */
@Service
public class ConversaoBo {

    public static final int MINIMO_COINS = 100;
    private static final BigDecimal TAXA = BigDecimal.valueOf(100);

    private final ConversaoDao conversaoDao;
    private final UsuarioDao usuarioDao;

    public ConversaoBo(ConversaoDao conversaoDao, UsuarioDao usuarioDao) {
        this.conversaoDao = conversaoDao;
        this.usuarioDao = usuarioDao;
    }

    public static BigDecimal paraReais(int coins) {
        return BigDecimal.valueOf(coins).divide(TAXA, 2, RoundingMode.HALF_UP);
    }

    public Conversao converter(Conversao c) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        if (c.getCoins() < MINIMO_COINS)
            throw new RegraNegocioException("A conversão mínima é de " + MINIMO_COINS + " SoulCoins.");
        usuarioDao.buscar(c.getUsuarioId());
        if (!usuarioDao.debitar(c.getUsuarioId(), c.getCoins()))
            throw new RegraNegocioException("Saldo insuficiente para essa conversão.");
        c.setValorDesconto(paraReais(c.getCoins()));
        c.setData(LocalDate.now());
        conversaoDao.cadastrar(c);
        return c;
    }

    public List<Conversao> listar() throws SQLException {
        return conversaoDao.listar();
    }

    public Conversao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return conversaoDao.buscar(id);
    }

    /** Cancelamento da conversao: remove o registro e estorna os SoulCoins. */
    public void cancelar(int id) throws SQLException, EntidadeNaoEncontradaException {
        Conversao c = conversaoDao.buscar(id);
        conversaoDao.remover(id);
        usuarioDao.estornar(c.getUsuarioId(), c.getCoins());
    }
}
