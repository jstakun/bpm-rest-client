package com.redhat.waw.jstakun;

public class Sale {
	
	private String article;
	private long amount;
	private int quantity;
	private String customer;
	
	public Sale(String article, long amount, int quantity) {
		this.article = article;
		this.amount = amount;
		this.quantity = quantity;
	}
	
	public Sale(String article, long amount, int quantity, String customer) {
		this.article = article;
		this.amount = amount;
		this.quantity = quantity;
		this.customer = customer;
	}
	
	public String getArticle() {
		return article;
	}
	
	public void setArticle(String article) {
		this.article = article;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}
}
