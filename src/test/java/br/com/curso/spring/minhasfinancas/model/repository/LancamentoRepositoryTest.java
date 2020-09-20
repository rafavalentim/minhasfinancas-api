package br.com.curso.spring.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.curso.spring.minhasfinancas.model.entity.Lancamento;
import br.com.curso.spring.minhasfinancas.model.enums.StatusLancamento;
import br.com.curso.spring.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class) //Spring 2.2.x
@ActiveProfiles("test") //Declaração explícita do profile de testes.
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) //Não muda as configurações do H2 definida no propertie de test
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		Lancamento lancamento =criaLancamento();
		
		lancamento = repository.save(lancamento);
		
		assertThat(lancamento.getId()).isNotNull();
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoInexistente).isNull();
		
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento.setAno(2021);
		lancamento.setDescricao("Teste Atualizar Lançamento");
		lancamento.setStatus(StatusLancamento.CANCELADO);
				
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2021);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar Lançamento");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		
		
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
		
	}
	
	
	private Lancamento criarEPersistirUmLancamento() {
		
		Lancamento lancamento =criaLancamento();
		
		entityManager.persist(lancamento);
		
		return lancamento;
		
	}
	
	
	public static Lancamento criaLancamento() {
		
		return  Lancamento.builder()
				.ano(2020)
				.mes(9)
				.descricao("Descrição teste")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}
	
	

}
