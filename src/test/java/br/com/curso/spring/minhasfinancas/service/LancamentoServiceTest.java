package br.com.curso.spring.minhasfinancas.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Lancamento;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.model.enums.StatusLancamento;
import br.com.curso.spring.minhasfinancas.model.repository.LancamentoRepository;
import br.com.curso.spring.minhasfinancas.model.repository.LancamentoRepositoryTest;
import br.com.curso.spring.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl serviceImpl;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criaLancamento();
		Mockito.doNothing().when(serviceImpl).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criaLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//Execução
		Lancamento lancamento = serviceImpl.salvar(lancamentoASalvar);
		
		//Verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
		
		
	}
	
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverUmErroDeValidacao() {
		
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criaLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(serviceImpl).validar(lancamentoASalvar);
		
		//Execução e verificação
		Assertions.catchThrowableOfType(() -> serviceImpl.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criaLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE); 
		
		
		Mockito.doNothing().when(serviceImpl).validar(lancamentoSalvo);
		
		
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//Execução
		serviceImpl.atualizar(lancamentoSalvo);
		
		//Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoNaoSalvoPreviamente() {
		
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criaLancamento();
		lancamentoASalvar.setId(null);
		lancamentoASalvar.setUsuario(new Usuario().builder().nome("Rafael").
				email("rafael@valentim.com").
				id(1l).
				senha("123").
				build());
		
		//Execução e verificação
		Assertions.catchThrowableOfType(() -> serviceImpl.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	
	@Test
	public void deveDeletarUmLancamento() {
		
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
		lancamento.setId(1l);
		
		//execução
		serviceImpl.deletar(lancamento);
		
		//verificação
		Mockito.verify(repository).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoNaoSalvoPreviamente() {
		
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
		lancamento.setId(null);
		
		//execução e verificação
		Assertions.catchThrowableOfType(() -> serviceImpl.deletar(lancamento), NullPointerException.class);
		//verificação
				Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	
	@Test
	public void deveFiltrarLancamentos() {
		
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
		lancamento.setId(1l);
				
		List<Lancamento> lista = new ArrayList<Lancamento>();
		lista.add(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execução
		List<Lancamento> listaResultado = serviceImpl.buscar(lancamento);
		
		//verificação
		Assertions
		.assertThat(listaResultado)
		.isNotEmpty()
		.hasSize(1)
		.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		
		Mockito.doReturn(lancamento).when(serviceImpl).atualizar(lancamento);
		
		//execução
		serviceImpl.atualizarStatus(lancamento, novoStatus);
		
		//verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		
		Mockito.verify(serviceImpl).atualizar(lancamento);
	}
	
	
	@Test
	public void deveObterUmLancamentoPorId() {
		
		//cenário
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execução
		Optional<Lancamento> resultado = serviceImpl.obterPorId(id);
		
		//verificação
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void naoDeveRetornarUmLancamentoPorId() {
		
		//cenário
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execução
		Optional<Lancamento> resultado = serviceImpl.obterPorId(id);
		
		//verificação
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	
	@Test
	public void deveLancarErrosAoValidarLancamentos() {
		
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable(() -> serviceImpl.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		
		lancamento.setDescricao("Proventos");
		erro = Assertions.catchThrowable(() -> serviceImpl.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido .");

		lancamento.setMes(9);
		erro = Assertions.catchThrowable(() -> serviceImpl.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido .");

		lancamento.setAno(2020);
		erro = Assertions.catchThrowable(() -> serviceImpl.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário válido .");

		Usuario u = Usuario.builder().id(1l).email("usuario@usuario.com").nome("Usuario").senha("123").build();
		lancamento.setUsuario(u);
		
		erro = Assertions.catchThrowable(() -> serviceImpl.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido .");

		lancamento.setValor(BigDecimal.valueOf(250.00));
		erro = Assertions.catchThrowable(() -> serviceImpl.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento válido .");

		lancamento.setStatus(StatusLancamento.EFETIVADO);
		
	}
	

}
