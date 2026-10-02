package br.com.fiap.api.dao;

import br.com.fiap.api.config.ConnectionFactory;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.model.StatusValidacao;
import br.com.fiap.api.model.Validacao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ValidacaoDao {

    private final ConnectionFactory connectionFactory;
    private final String INSERT_SQL = "insert into tb_nuva_validacao (cd_validacao, cd_usuario, cd_missao, nm_arquivo, st_validacao) values (sq_nuva_validacao.nextval, ?, ?, ?, ?)";
    private final String SELECT_SQL = "select * from tb_nuva_validacao order by dt_envio desc";
    private final String SELECT_BY_ID_SQL = "select * from tb_nuva_validacao where cd_validacao = ?";
    private final String SELECT_BY_USUARIO_SQL = "select * from tb_nuva_validacao where cd_usuario = ? order by dt_envio desc";
    private final String UPDATE_SQL = "update tb_nuva_validacao set nm_arquivo = ?, st_validacao = ? where cd_validacao = ?";
    private final String DELETE_SQL = "delete from tb_nuva_validacao where cd_validacao = ?";
    private final String COUNT_HOJE_SQL = "select count(*) from tb_nuva_validacao where cd_usuario = ? and cd_missao = ? and trunc(dt_envio) = trunc(sysdate)";
    private final String COUNT_HOJE_STATUS_SQL = COUNT_HOJE_SQL + " and st_validacao = ?";

    public ValidacaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Validacao v) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_validacao"})) {
            stmt.setInt(1, v.getUsuarioId());
            stmt.setInt(2, v.getMissaoId());
            stmt.setString(3, v.getNomeArquivo());
            stmt.setString(4, v.getStatus().name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) v.setId(rs.getInt(1));
            }
        }
    }

    public List<Validacao> listar() throws SQLException {
        return consultarLista(SELECT_SQL, null);
    }

    public List<Validacao> listarPorUsuario(int usuarioId) throws SQLException {
        return consultarLista(SELECT_BY_USUARIO_SQL, usuarioId);
    }

    public Validacao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Validação não encontrada");
                return getValidacao(rs);
            }
        }
    }

    public void atualizar(Validacao v) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, v.getNomeArquivo());
            stmt.setString(2, v.getStatus().name());
            stmt.setInt(3, v.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Validação não encontrada");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Validação não encontrada");
        }
    }

    /** Quantidade de envios do usuario para a missao no dia (regra das 3 tentativas). */
    public int contarTentativasHoje(int usuarioId, int missaoId) throws SQLException {
        return contar(COUNT_HOJE_SQL, usuarioId, missaoId, null);
    }

    /** Verifica se a missao ja foi validada hoje (regra: 1 validacao por dia). */
    public boolean validadaHoje(int usuarioId, int missaoId) throws SQLException {
        return contar(COUNT_HOJE_STATUS_SQL, usuarioId, missaoId, StatusValidacao.VALIDADO) > 0;
    }

    private int contar(String sql, int usuarioId, int missaoId, StatusValidacao status) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, missaoId);
            if (status != null) stmt.setString(3, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private List<Validacao> consultarLista(String sql, Integer parametro) throws SQLException {
        List<Validacao> lista = new ArrayList<>();
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(getValidacao(rs));
            }
        }
        return lista;
    }

    private Validacao getValidacao(ResultSet rs) throws SQLException {
        Validacao v = new Validacao();
        v.setId(rs.getInt("cd_validacao"));
        v.setUsuarioId(rs.getInt("cd_usuario"));
        v.setMissaoId(rs.getInt("cd_missao"));
        v.setNomeArquivo(rs.getString("nm_arquivo"));
        v.setStatus(StatusValidacao.valueOf(rs.getString("st_validacao")));
        v.setDataEnvio(rs.getTimestamp("dt_envio").toLocalDateTime());
        return v;
    }
}
