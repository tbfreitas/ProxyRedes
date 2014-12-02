package br.com.core;

public class SitesAcessados implements Comparable<SitesAcessados> {

	private String site;
	private int cont;
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public int getCont() {
		return cont;
	}
	public void setCont(int cont) {
		this.cont = cont;
	}
	@Override
	public int compareTo(SitesAcessados site) {
		if(this.cont > site.cont){
			return -1;
		}
		else if(this.cont < site.cont){
			return 1;
		}
		return 0;
	}

	
}
