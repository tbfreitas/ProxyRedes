package br.com.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.net.*;


public class TrataRequisicao implements Runnable {
	
	Socket conexao;
	String tipoLista;
	public static ArrayList<String> listas = new ArrayList<>();
	String ipCliente;
	String url;
	ArrayList<SitesAcessados> acessos = new ArrayList<SitesAcessados>();
	DataOutputStream vai_cliente;
	BufferedReader chega_cliente_buffer;
		
	public TrataRequisicao(Socket conexao, String tipoLista) {
		this.conexao = conexao;
		this.tipoLista = tipoLista;
	}
	
	/**
	 * Classe que grava em um arrayList todos os sites da white 
	 * black lists, gravados no arquivos .txt
	 * 
	 * @param tipoLista
	 * @throws IOException
	 */
	
	public void estabeleConexao(Socket conexao) throws IOException{
			
		System.out.println("CHEGUEI.");
		InputStream chega_cliente;
		chega_cliente = conexao.getInputStream();

		vai_cliente = new DataOutputStream(conexao.getOutputStream());
		chega_cliente_buffer = new BufferedReader(new InputStreamReader(chega_cliente));

	}
	
	public void pegaRequisicao(BufferedReader chega_cliente_buffer) throws IOException{
			  
		//Colocando numa string a leitura da linha do BUfferedReader
		String requestLine = chega_cliente_buffer.readLine();
        
        // Transformando a string numa StringTokenizer para recuperar a URL
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        url = tokens.nextToken();
        
        System.out.println(url);
                
	}
	
	public  void gravandoListas(String tipoLista) throws IOException{
		
		if(tipoLista == "w"){
			
			BufferedReader in = new BufferedReader(new FileReader("whitelist.txt"));
		    String linha = in.readLine();
		   
		    while(linha != null){
			    listas.add(linha);
			    linha = in.readLine();
		    }
		
		}else{
			
			BufferedReader in = new BufferedReader(new FileReader("blacklist.txt"));
		    String linha = in.readLine();
		   
		    while(linha != null){
			    listas.add(linha);
			    linha = in.readLine();
		    }
		}
	}
	
	/**
	 * Classe que percorre a ArrayLists de Sites. Se o parâmetro for whitelist,
	 * percorre e se achar , retorna true. Se não achar na whitelist, retorna false.
	 * A lógica da blacklist é inversa.	 * 
	 * 
	 * @param tipoLista
	 * @return boolean 
	 * @throws FileNotFoundException
	 */
	public static boolean verificaPermissaoPraPagina(String tipoLista, String url) throws FileNotFoundException{
	
		if(tipoLista == "w"){
			
			for(String site : listas){			
				if(site == url){					
					return true;
				}
			}
			return false;
		
		}else{
			
			for(String site : listas){			
				if(site == url){					
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Método para verificar IP do cliente
	 */
	public  void verificaCliente(){
		
		 ipCliente = conexao.getRemoteSocketAddress().toString();
	}
	
	
	public static void adicionaSiteAcessado(String url, ArrayList<SitesAcessados> acessos){
		
		SitesAcessados auxiliar = null;
		
		for(int i=0;i<acessos.size();i++){ 
			auxiliar = acessos.get(i);
			
			if(url == auxiliar.getSite()){
				auxiliar.setCont(auxiliar.getCont() +1);
				break;
			}
		} 
		
		auxiliar.setSite(url);
		auxiliar.setCont(1);
		
		acessos.add(auxiliar);
	}
	
	public void imprimeSitesAcessados(ArrayList<SitesAcessados> acessos){
		
		SitesAcessados auxiliar = null;
		
		for(int i=0;i<acessos.size();i++){ 
			auxiliar = acessos.get(i);
		
			System.out.println("O site " +auxiliar.getSite() + "foi visitado " +auxiliar.getCont() +" vezes.");
		}
		
	}
	
	public static void recuperaURL(){
		//recuperar a URL requisita do cliente e salva no arquivo .txt 
		//todos os sites requisitados . Também grava o tempo necessário para recuperar a página
	}
	
	public static void gravaURL(){
		//grava em um arquivo txt, os sites mais acessados
	}
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("CHEGUEI.");		
			estabeleConexao(conexao);
			pegaRequisicao(chega_cliente_buffer);
			System.out.println("O IP do cliente é : " +ipCliente +".");
			
			if(verificaPermissaoPraPagina(tipoLista, url)){
				
				adicionaSiteAcessado(url, acessos);
				imprimeSitesAcessados(acessos);
				recuperaURL();
				gravaURL();
			
			}else{				
				System.out.println("Página bloqueada !");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
