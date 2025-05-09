package com.example.algamoney.api.repository.listener;

import org.springframework.util.StringUtils;

import com.example.algamoney.api.AlgamoneyApiNovoApplication;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.storage.S3;

import jakarta.persistence.PostLoad;

public class LancamentoAnexoListener {
	
	
	@PostLoad
	public void postLoad(Lancamento lancamento) {
		
		if (StringUtils.hasText(lancamento.getAnexo())) {
			S3 s3 = AlgamoneyApiNovoApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
		
	}

}
