package br.com.curso.spring.minhasfinancas.service;

import java.util.List;

import br.com.curso.spring.minhasfinancas.model.entity.Lancamento;
import br.com.curso.spring.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamentoFiltro, StatusLancamento status);
	
	void validar(Lancamento lancamento);
}
