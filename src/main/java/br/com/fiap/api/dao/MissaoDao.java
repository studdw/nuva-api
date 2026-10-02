package br.com.fiap.api.dao;

import br.com.fiap.api.config.ConnectionFactory;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.model.Missao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class MissaoDao {

    private static final String SEPARADOR = "|";

    private final ConnectionFactory connectionFactory;
    private final String INSERT_SQL = "insert into tb_nuva_missao (cd_missao, nm_titulo, ds_categoria, ds_cor, nr_recompensa, ds_duracao, ds_dificuldade, ds_resumo, ds_passos, ds_criterio, ds_impacto, st_ativa) values (sq_nuva_missao.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SELECT_SQL = "select * from tb_nuva_missao order by cd_missao";
    private final String SELECT_ATIVAS_SQL = "select * from tb_nuva_missao where st_ativa = 'S' order by cd_missao";
    private final String SELECT_BY_ID_SQL = "select * from tb_nuva_missao where cd_missao = ?";
    private final String UPDATE_SQL = "update tb_nuva_missao set nm_titulo = ?, ds_categoria = ?, ds_cor = ?, nr_recompensa = ?, ds_duracao = ?, ds_dificuldade = ?, ds_resumo = ?, ds_passos = ?, ds_criterio = ?, ds_impacto = ?, st_ativa = ? where cd_missao = ?";
    private final String DELETE_SQL = "delete from tb_nuva_missao where cd_missao = ?";

    public MissaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Missao missao) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_missao"})) {
            preencher(stmt, missao);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) missao.setId(rs.getInt(1));
            }
        }
    }

    public List<Missao> listar(boolean somenteAtivas) throws SQLException {
        List<Missao> lista = new ArrayList<>();
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(somenteAtivas ? SELECT_ATIVAS_SQL : SELECT_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(getMissao(rs));
        }
        return lista;
    }

    public Missao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Missão não encontrada");
                return getMissao(rs);
            }
        }
    }

    public void atualizar(Missao missao) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            preencher(stmt, missao);
            stmt.setInt(12, missao.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Missão não encontrada");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Missão não encontrada");
        }
    }

    private void preencher(PreparedStatement stmt, Missao m) throws SQLException {
        stmt.setString(1, m.getTitulo());
        stmt.setString(2, m.getCategoria());
        stmt.setString(3, m.getCor());
        stmt.setInt(4, m.getRecompensa());
        stmt.setString(5, m.getDuracao());
        stmt.setString(6, m.getDificuldade());
        stmt.setString(7, m.getResumo());
        stmt.setString(8, String.join(SEPARADOR, m.getPassos()));
        stmt.setString(9, m.getCriterio());
        stmt.setString(10, m.getImpacto());
        stmt.setString(11, m.isAtiva() ? "S" : "N");
    }

    private Missao getMissao(ResultSet rs) throws SQLException {
        Missao m = new Missao();
        m.setId(rs.getInt("cd_missao"));
        m.setTitulo(rs.getString("nm_titulo"));
        m.setCategoria(rs.getString("ds_categoria"));
        m.setCor(rs.getString("ds_cor"));
        m.setRecompensa(rs.getInt("nr_recompensa"));
        m.setDuracao(rs.getString("ds_duracao"));
        m.setDificuldade(rs.getString("ds_dificuldade"));
        m.setResumo(rs.getString("ds_resumo"));
        m.setPassos(Arrays.asList(rs.getString("ds_passos").split("\\|")));
        m.setCriterio(rs.getString("ds_criterio"));
        m.setImpacto(rs.getString("ds_impacto"));
        m.setAtiva("S".equals(rs.getString("st_ativa")));
        return m;
    }
}
