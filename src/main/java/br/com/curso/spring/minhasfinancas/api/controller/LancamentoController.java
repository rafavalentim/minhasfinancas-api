package br.com.curso.spring.minhasfinancas.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.spring.minhasfinancas.api.dto.AtualizaStatusDTO;
import br.com.curso.spring.minhasfinancas.api.dto.LancamentoDTO;
import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Lancamento;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.model.enums.StatusLancamento;
import br.com.curso.spring.minhasfinancas.model.enums.TipoLancamento;
import br.com.curso.spring.minhasfinancas.service.LancamentoService;
import br.com.curso.spring.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController   //Possui o ResponseBody por default
@RequestMapping("/minhasfinancas/api/lancamentos")  //Define a raiz da apis.
@RequiredArgsConstructor //cria o construtor automaticamente com os parametros final
public class LancamentoController {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam( value = "mes", required = false) Integer mes,
			@RequestParam( value = "ano", required = false)Integer ano,
			@RequestParam("usuario") Long idUsuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setMes(mes);
		
		Optional<Usuario> usuario =  usuarioService.obterPorId(idUsuario);
		
		if(!usuario.isPresent()) {
			
			return ResponseEntity.badRequest().body("Usuario não encontrado com o id informado.");
			
		}else {
			
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);	
	}
	
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		
		try {
			
			Lancamento lancamento = convertDtoToLancamento(dto);
			
			lancamento = service.salvar(lancamento);		
			
			return new ResponseEntity(lancamento, HttpStatus.CREATED);	
			
		}catch (RegraNegocioException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
		}	
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar (@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		try {
			
			 return service.obterPorId(id).map(entity -> {
				Lancamento lancamento = convertDtoToLancamento(dto);
				lancamento.setId(entity.getId());
				
				service.atualizar(lancamento);
				
				return ResponseEntity.ok(lancamento);
				
			}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
			
			
		}catch (RegraNegocioException e) {
			 return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		try {
			
			return service.obterPorId(id).map(
					
					entity -> {
						
						StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus()); 
						
						if(statusSelecionado == null) {
							return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento. Favor enviar um status válido.");
						}
						
						entity.setStatus(statusSelecionado);
						
						service.atualizar(entity);
						
						return ResponseEntity.ok(entity);
						
					}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
					
			
			
		}catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		
		return service.obterPorId(id).map(entity -> {			
			service.deletar(entity);
			
			return new ResponseEntity(HttpStatus.NO_CONTENT);			
			
		}).orElseGet( () -> 
			new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	public Lancamento convertDtoToLancamento(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setMes(dto.getMes());
		lancamento.setAno(dto.getAno());
		lancamento.setValor(dto.getValor());
		
		//Buscando o lançamento
		Usuario usuario = usuarioService.obterPorId(dto.getIdUsuario()).
				orElseThrow(() -> new RegraNegocioException("Usuário nao encontrado com o id informado"));
		
		lancamento.setUsuario(usuario);
		
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));  //valueOf para capturar enums
		}
		
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		return lancamento;
	
	}
	
	@GetMapping("{id}")
	public ResponseEntity obterLancamentoPorId(@PathVariable("id") Long id) {
		
		return service.obterPorId(id)
				.map(lancamento -> new ResponseEntity(convertLancamentoToDTO(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
		
		
	}
	
	private LancamentoDTO convertLancamentoToDTO(Lancamento lancamento) {
		
		return LancamentoDTO.builder()
							.id(lancamento.getId())
							.descricao(lancamento.getDescricao())
							.mes(lancamento.getMes())
							.ano(lancamento.getAno())
							.valor(lancamento.getValor())
							.tipo(lancamento.getTipo().name())
							.status(lancamento.getStatus().name())
							.idUsuario(lancamento.getUsuario().getId())
							.build();
							
							
		
		
		
	}
	
	

}
