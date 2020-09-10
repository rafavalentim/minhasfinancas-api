package br.com.curso.spring.minhasfinancas.exception;

public class ErroAutenticacaoException extends RuntimeException{
	
	public ErroAutenticacaoException(String msg) {
		super(msg);
	}

}
