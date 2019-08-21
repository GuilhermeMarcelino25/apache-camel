package br.com.caelum.camel.pojo;

import java.util.Calendar;

public class Negociacao {
	
	public Negociacao(Double preco, Integer quantidade, Calendar data) {
		super();
		this.preco = preco;
		this.quantidade = quantidade;
		this.data = data;
	}
	public Negociacao() {
		super();
	}
	
	
	Double preco;
	Integer quantidade;
	Calendar data;
	
	public Double getPreco() {
		return preco;
	}
	public void setPreco(Double preco) {
		this.preco = preco;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public Calendar getData() {
		return data;
	}
	public void setData(Calendar data) {
		this.data = data;
	}
}
