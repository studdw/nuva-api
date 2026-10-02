package br.com.fiap.api.bo;

import br.com.fiap.api.exception.RegraNegocioException;

import java.util.regex.Pattern;

/** Validacoes reutilizadas pelas classes BO. */
final class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validador() {
    }

    static void obrigatorio(String valor, String campo) throws RegraNegocioException {
        if (valor == null || valor.isBlank()) throw new RegraNegocioException("O campo '" + campo + "' é obrigatório.");
    }

    static void tamanhoMinimo(String valor, int minimo, String campo) throws RegraNegocioException {
        obrigatorio(valor, campo);
        if (valor.trim().length() < minimo)
            throw new RegraNegocioException("O campo '" + campo + "' deve ter ao menos " + minimo + " caracteres.");
    }

    static void tamanhoMaximo(String valor, int maximo, String campo) throws RegraNegocioException {
        if (valor != null && valor.length() > maximo)
            throw new RegraNegocioException("O campo '" + campo + "' deve ter no máximo " + maximo + " caracteres.");
    }

    static void email(String valor) throws RegraNegocioException {
        obrigatorio(valor, "email");
        if (!EMAIL.matcher(valor.trim()).matches()) throw new RegraNegocioException("E-mail inválido.");
    }

    static void intervalo(int valor, int min, int max, String campo) throws RegraNegocioException {
        if (valor < min || valor > max)
            throw new RegraNegocioException("O campo '" + campo + "' deve estar entre " + min + " e " + max + ".");
    }
}
