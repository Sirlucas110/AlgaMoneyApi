package com.example.algamoney.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Component
public class S3 {
	
	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	@Autowired
	private AlgamoneyApiProperty property;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	public String salvarTemporariamente(MultipartFile arquivo) {
		try {
			// Definir permissões públicas de leitura
			AccessControlList acl = new AccessControlList();
			acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

			// Definir metadados do arquivo
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(arquivo.getContentType());
			objectMetadata.setContentLength(arquivo.getSize());

			// Gerar um nome único para o arquivo
			String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());

			// Criar e configurar a requisição de upload
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(),
					nomeUnico,
					arquivo.getInputStream(),
					objectMetadata);
					

			// Adicionar tag de expiração
			putObjectRequest.setTagging(new ObjectTagging(
					Arrays.asList(new Tag("expirar", "true"))));

			// Enviar o arquivo ao S3
			amazonS3.putObject(putObjectRequest);

			logger.info("✅ Arquivo '{}' enviado com sucesso para o S3.", arquivo.getOriginalFilename());

			return nomeUnico;

		} catch (IOException e) {
			logger.error("❌ Erro ao enviar arquivo para o S3: {}", e.getMessage(), e);
			throw new RuntimeException("Problemas ao tentar enviar o arquivo ao S3", e);
		}
	}

	public String configurarUrl(String objeto) {
		return "https://" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
	}
	
	public void salvar(String objeto) {
		try {
			// Remover tags para que o objeto não expire automaticamente
			SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
					property.getS3().getBucket(), 
					objeto, 
					new ObjectTagging(Collections.emptyList()));
			
			amazonS3.setObjectTagging(setObjectTaggingRequest);
			logger.info("✅ Arquivo '{}' salvo permanentemente no S3.", objeto);

		} catch (AmazonServiceException e) {
			logger.error("❌ Erro ao salvar objeto no S3: {}", e.getMessage(), e);
		}
	}

	public void remover(String objeto) {
		try {
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), objeto);
			
			amazonS3.deleteObject(deleteObjectRequest);
			logger.info("✅ Objeto '{}' removido com sucesso do S3.", objeto);
			
		} catch (AmazonServiceException e) {
			logger.error("❌ Erro ao remover objeto do S3: {}", e.getMessage(), e);
		} catch (SdkClientException e) {
			logger.error("❌ Erro de conexão com a AWS ao tentar remover '{}': {}", objeto, e.getMessage(), e);
		}
	}

	public void substituir(String objetoAntigo, String objetoNovo) {
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		
		salvar(objetoNovo);
	}

	private String gerarNomeUnico(String originalFileName) {
		return UUID.randomUUID().toString() + "_" + originalFileName;
	}
}
