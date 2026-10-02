package br.com.fiap.api.bo;

import br.com.fiap.api.dao.MissaoDao;
import br.com.fiap.api.dao.UsuarioDao;
import br.com.fiap.api.dao.ValidacaoDao;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.exception.RegraNegocioException;
import br.com.fiap.api.model.Missao;
import br.com.fiap.api.model.StatusValidacao;
import br.com.fiap.api.model.Validacao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValidacaoBo {

    public static final int MAX_TENTATIVAS = 3;

    private final ValidacaoDao validacaoDao;
    private final UsuarioDao usuarioDao;
    private final MissaoDao missaoDao;

    public ValidacaoBo(ValidacaoDao validacaoDao, UsuarioDao usuarioDao, MissaoDao missaoDao) {
        this.validacaoDao = validacaoDao;
        this.usuarioDao = usuarioDao;
        this.missaoDao = missaoDao;
    }

    public Validacao enviar(Validacao v) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validarArquivo(v.getNomeArquivo());
        usuarioDao.buscar(v.getUsuarioId());
        Missao missao = missaoDao.buscar(v.getMissaoId());

        if (!missao.isAtiva()) throw new RegraNegocioException("Esta missão está desativada.");
        if (validacaoDao.validadaHoje(v.getUsuarioId(), v.getMissaoId()))
            throw new RegraNegocioException("Você já concluiu esta missão hoje.");
        int tentativas = validacaoDao.contarTentativasHoje(v.getUsuarioId(), v.getMissaoId());
        if (tentativas >= MAX_TENTATIVAS)
            throw new RegraNegocioException("Limite de " + MAX_TENTATIVAS + " tentativas atingido para esta missão hoje.");

        // simulacao da IA
        if (v.isRostoVisivel()) {
            v.setStatus(StatusValidacao.VALIDADO);
            v.setMensagem("Missão validada! +" + missao.getRecompensa() + " SoulCoins creditados.");
        } else {
            v.setStatus(StatusValidacao.RECUSADO);
            v.setMensagem("Vídeo recusado: rosto não identificado. Tentativas restantes: " + (MAX_TENTATIVAS - tentativas - 1) + ".");
        }
        v.setDataEnvio(LocalDateTime.now());
        validacaoDao.cadastrar(v);

        if (v.getStatus() == StatusValidacao.VALIDADO) usuarioDao.creditar(v.getUsuarioId(), missao.getRecompensa());
        return v;
    }

    public List<Validacao> listar() throws SQLException {
        return validacaoDao.listar();
    }

    public Validacao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return validacaoDao.buscar(id);
    }

    /** Revisao manual do operador (ex.: reprovar uma validacao). */
    public Validacao atualizar(int id, Validacao v) throws SQLException, RegraNegocioException, EntidadeNaoEncontradaException {
        validarArquivo(v.getNomeArquivo());
        if (v.getStatus() == null) throw new RegraNegocioException("Informe o status: VALIDADO ou RECUSADO.");
        Validacao atual = validacaoDao.buscar(id);
        if (atual.getStatus() != v.getStatus()) {
            int recompensa = missaoDao.buscar(atual.getMissaoId()).getRecompensa();
            if (v.getStatus() == StatusValidacao.VALIDADO) usuarioDao.creditar(atual.getUsuarioId(), recompensa);
            else usuarioDao.creditar(atual.getUsuarioId(), -recompensa);
        }
        atual.setNomeArquivo(v.getNomeArquivo());
        atual.setStatus(v.getStatus());
        validacaoDao.atualizar(atual);
        return atual;
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        validacaoDao.remover(id);
    }

    private void validarArquivo(String nome) throws RegraNegocioException {
        Validador.obrigatorio(nome, "nomeArquivo");
        Validador.tamanhoMaximo(nome, 200, "nomeArquivo");
        String minusculo = nome.toLowerCase();
        if (!minusculo.endsWith(".mp4") && !minusculo.endsWith(".mov"))
            throw new RegraNegocioException("Formato inválido. Envie um vídeo MP4 ou MOV.");
    }
}
