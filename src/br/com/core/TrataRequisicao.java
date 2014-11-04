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
	public static boolean verificaPermissaoPraPagina(String tipoLista) throws FileNotFoundException{
	
		if(tipoLista == "w"){
			
			for(String site : listas){			
				if(site == "4"){					
					return true;
				}
			}
			return false;
		
		}else{
			
			for(String site : listas){			
				if(site == "4"){					
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
				InputStream chega_cliente;
					chega_cliente = conexao.getInputStream();

				DataOutputStream vai_cliente = new DataOutputStream(conexao.getOutputStream());
			
				BufferedReader chega_cliente_buffer = new BufferedReader(new InputStreamReader(chega_cliente));

		        // Get the request line of the HTTP request message.
//		        while(chega_clienete=)
				
				String requestLine = chega_cliente_buffer.readLine();

		        
		        // Extract the filename from the request line.
		        StringTokenizer tokens = new StringTokenizer(requestLine);
		        tokens.nextToken();  // skip over the method, which should be "GET"
		        String url = tokens.nextToken();
		        System.out.println(url);
		        
		        URL pega_url = new URL(url);
			
		       penStram chega_url =  pega_url.openStream();
			
			if(verificaPermissaoPraPagina(tipoLista)){
				recuperaURL();
				gravaURL();
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
