package br.com.core;

public class ClientesAcessaram implements Comparable<ClientesAcessaram>  {
	
	private String ip;
	private int requisicao;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getRequisicao() {
		return requisicao;
	}
	public void setRequisicao(int requisicao) {
		this.requisicao = requisicao;
	}
	@Override
	public int compareTo(ClientesAcessaram clientes) {
		if(this.requisicao > clientes.requisicao){
			return -1;
		}
		else if(this.requisicao < clientes.requisicao){
			return 1;
		}
		return 0;
	}
	
}
	
	

