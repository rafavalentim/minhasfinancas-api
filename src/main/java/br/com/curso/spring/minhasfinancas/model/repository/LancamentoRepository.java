package br.com.curso.spring.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.curso.spring.minhasfinancas.model.entity.Lancamento;
import br.com.curso.spring.minhasfinancas.model.enums.StatusLancamento;
import br.com.curso.spring.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
	@Query(value = "select sum(l.valor) "
				+ " from Lancamento l join l.usuario u "
				+ " where u.id = :idUsuario and l.tipo = :tipo "
				+ " and l.status = :status "
				+ " group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
				@Param("idUsuario") Long id, 
				@Param("tipo") TipoLancamento tipo,
				@Param("status") StatusLancamento status);

}
