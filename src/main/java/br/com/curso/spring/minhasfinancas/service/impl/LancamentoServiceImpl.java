package br.com.curso.spring.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Lancamento;
import br.com.curso.spring.minhasfinancas.model.enums.StatusLancamento;
import br.com.curso.spring.minhasfinancas.model.repository.LancamentoRepository;
import br.com.curso.spring.minhasfinancas.service.LancamentoService;


@Service
public class LancamentoServiceImpl implements LancamentoService{
	
	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repositor) {
		this.repository = repository;
		
	}
	

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		
		validar(lancamento);
		Objects.requireNonNull(lancamento.getId()); //O lancamento precisa ter um ID.
		return repository.save(lancamento); 
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		
		Objects.requireNonNull(lancamento.getId()); //O lancamento precisa ter um ID.
		repository.delete(lancamento);
		
	}


	@Override
	@Transactional
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		
		lancamento.setStatus(status);
		atualizar(lancamento);
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {

		Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching().
				withIgnoreCase().
				withStringMatcher(StringMatcher.CONTAINING)); //filtra pelos atributos populados do objeto
		
		
		
		return repository.findAll(example);
	}


	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			
			throw new RegraNegocioException("Informe uma descrição válida.");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			
			throw new RegraNegocioException("Informe um mês válido .");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			
			throw new RegraNegocioException("Informe um ano válido .");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId()== null) {
			
			throw new RegraNegocioException("Informe um usuário válido .");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			
			throw new RegraNegocioException("Informe um valor válido .");
		}
		if(lancamento.getTipo() == null) {
			
			throw new RegraNegocioException("Informe um tipo de lançamento válido .");
		}
		
		
	}

}
