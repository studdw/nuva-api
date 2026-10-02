package br.com.fiap.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

/** Converte as excecoes da aplicacao em respostas HTTP padronizadas. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> naoEncontrada(EntidadeNaoEncontradaException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regraNegocio(RegraNegocioException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> requisicaoInvalida(Exception e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida: verifique o corpo JSON e os parâmetros.", req);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResposta> erroBanco(SQLException e, HttpServletRequest req) {
        log.error("Erro de banco de dados", e);
        if (e.getErrorCode() == 1) { // ORA-00001: unique constraint
            return montar(HttpStatus.CONFLICT, "Registro duplicado: já existe um cadastro com esses dados.", req);
        }
        if (e.getErrorCode() == 2291) { // ORA-02291: FK inexistente
            return montar(HttpStatus.BAD_REQUEST, "Registro relacionado não existe.", req);
        }
        if (e.getErrorCode() == 2292) { // ORA-02292: registro filho existente
            return montar(HttpStatus.CONFLICT, "Não é possível remover: existem registros vinculados.", req);
        }
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados.", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> erroGenerico(Exception e, HttpServletRequest req) {
        log.error("Erro inesperado", e);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado.", req);
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String mensagem, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(new ErroResposta(status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI()));
    }
}
