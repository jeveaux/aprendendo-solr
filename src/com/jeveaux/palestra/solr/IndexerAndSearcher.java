package com.jeveaux.palestra.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.jeveaux.palestra.solr.entity.Evento;

public class IndexerAndSearcher {

	private SolrServer solrServer;
	
	public IndexerAndSearcher() {
		try {
			this.solrServer = new CommonsHttpSolrServer("http://localhost:8983/solr");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param field
	 * @param text
	 */
	public void search(String field, String text) {
	    // http://localhost:8983/solr/select/q=*:*&sort=id+asc&version=2.2
	    ModifiableSolrParams params = new ModifiableSolrParams();
	    params.set("q", field + ":" + text);

		try {
			QueryResponse queryResponse = solrServer.query(params);
			
			SolrDocumentList documents = queryResponse.getResults();
			System.out.println("\n--\n");
			for(SolrDocument document : documents) {
				System.out.println("Document[" + document.getFieldValue("id") + "]");
				System.out.println("\tDocument Fields[" + document.getFieldNames() + "]");
				System.out.println("\tNome: " + document.getFieldValue("nome"));
				System.out.println("\tDescrição: " + document.getFieldValue("descricao"));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	    
	}
	
	/**
	 * 
	 * @param id
	 * @param nome
	 * @param descricao
	 */
	public void addDocument(String id, String nome, String descricao) {
		SolrInputDocument document = new SolrInputDocument();
	    document.addField("id", id);
	    document.addField("nome", nome);
	    document.addField("descricao", descricao);
	    
	    try {
			solrServer.add(document);
			solrServer.commit();
			System.out.println("[Document] Evento cadastrado: " + document.toString());
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param evento
	 */
	public void addBean(String id, String nome, String descricao) {
		Evento evento = new Evento();
		evento.setId(id);
		evento.setNome(nome);
		evento.setDescricao(descricao);
		
		try {
			solrServer.addBean(evento);
			solrServer.commit();
			System.out.println("[Bean] Evento cadastrado: " + evento.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void findAllDocuments() {
		SolrQuery query = new SolrQuery();
	    query.setQuery("*:*");
	    query.addSortField("id", SolrQuery.ORDER.asc);
	    
		try {
			QueryResponse queryResponse = solrServer.query(query);
			
			// Recupera todos os documentos
			SolrDocumentList documents = queryResponse.getResults();
			for(SolrDocument document : documents) {
				System.out.println("Document[" + document.getFieldValue("id") + "]");
				System.out.println("\tDocument Fields[" + document.getFieldNames() + "]");
				System.out.println("\tNome: " + document.getFieldValue("nome"));
				System.out.println("\tDescrição: " + document.getFieldValue("descricao"));
			}
			
			System.out.println("\n--\n");
			
			// Recupera todos os documentos do tipo Evento
			List<Evento> eventos = queryResponse.getBeans(Evento.class);
			for(Evento evento : eventos) {
				System.out.println("Evento[" + evento.getId() + "]");
				System.out.println("\tNome: " + evento.getNome());
				System.out.println("\tDescrição: " + evento.getDescricao());
			}
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		IndexerAndSearcher indexerAndSearcher = new IndexerAndSearcher();
		
		//indexerAndSearcher.addDocument("1", "Café com Tapioca", "Evento de Java do CEJUG em Fortaleza");
		//indexerAndSearcher.addBean("2", "EJES", "Encontro de Java do Espírito Santo");
		
		indexerAndSearcher.findAllDocuments();
		
		indexerAndSearcher.search("*", "*");
	}
	
}
