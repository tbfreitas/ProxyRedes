package br.com.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.net.*;


public class TrataRequisicao implements Runnable {

	Socket conexao;
	String tipoLista;
	ArrayList<String> listas = new ArrayList<>();
	String ipCliente;
	String url;
	ArrayList<SitesAcessados> acessos = new ArrayList<SitesAcessados>();
	DataOutputStream vai_cliente;
	BufferedReader chega_cliente_buffer;
	String requestLine;
	final static String CRLF = "\r\n";
	URL recuperarURL;

	public TrataRequisicao(Socket conexao, String tipoLista) {
		this.conexao = conexao;
		this.tipoLista = tipoLista;
	}

	public void estabeleConexao(Socket conexao) throws IOException{

		InputStream chega_cliente;
		chega_cliente = conexao.getInputStream();

		vai_cliente = new DataOutputStream(conexao.getOutputStream());
		chega_cliente_buffer = new BufferedReader(new InputStreamReader(chega_cliente));

	}

	public void pegaRequisicao(BufferedReader chega_cliente_buffer) throws IOException{

		//Colocando numa string a leitura da linha do BUfferedReader
		requestLine = chega_cliente_buffer.readLine();

		// Transformando a string numa StringTokenizer para recuperar a URL
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();  // skip over the method, which should be "GET"
		url = tokens.nextToken();
		
		 recuperarURL = new URL(url);
		
		System.out.println(url);

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
	public static boolean verificaPermissaoPraPagina(String tipoLista, String url,ArrayList<String> listas) throws FileNotFoundException{

		if(tipoLista.equals("w")){

			for(String site : listas){			
				if(site.equals(url)){					
					return true;
				}
			}
			return false;

		}else{

			for(String site : listas){			
				if(site.equals(url)){					
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

		SitesAcessados auxiliar = new SitesAcessados();

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

	public static void recuperaURL(URL recuperarURL,String url,String requestLine, BufferedReader chega_cliente_buffer, DataOutputStream vai_cliente, Socket conexao) throws Exception{
				
		URLConnection urlC = recuperarURL.openConnection();
		
		BufferedReader in = new BufferedReader( new InputStreamReader(urlC.getInputStream()));
		
		// Open the requested file.
		FileInputStream fis = null ;
		boolean fileExists = true ;
		
		try {
			fis = (FileInputStream) recuperarURL.openConnection().getInputStream();
			fis.
		} catch (FileNotFoundException e) {
			fileExists = false ;
		}
		
		if(fileExists == true){
			System.out.println("Teste");
		}

		// Debug info for private use
		System.out.println("Incoming!!!");
		System.out.println(requestLine);

		String headerLine = null;
		while ((headerLine = chega_cliente_buffer.readLine()).length() != 0) {
			System.out.println(headerLine +"teste");
		}

		
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-Type: " + contentType(url) + CRLF;
		} else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "<HTML>" + 
					"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
					"<BODY>Not Found</BODY></HTML>";
		}
		
		// Send the status line.
		vai_cliente.writeBytes(statusLine);

		// Send the content type line.
		vai_cliente.writeBytes(contentTypeLine);

		// Send a blank line to indicate the end of the header lines.
		vai_cliente.writeBytes(CRLF);

		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, vai_cliente);
			fis.close();
		} else {
			vai_cliente.writeBytes(entityBody) ;
		}

		// Close streams and socket.
		vai_cliente.close();
		chega_cliente_buffer.close();
		conexao.close();
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName) {
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if(fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
			return "audio/x-pn-realaudio";
		}
		return "application/octet-stream" ;
	}


public static void gravaURL(){
	//grava em um arquivo txt, os sites mais acessados
}

@Override
public void run() {
	// TODO Auto-generated method stub
	try {

		estabeleConexao(conexao);
		pegaRequisicao(chega_cliente_buffer);
		gravandoListas(tipoLista);
		verificaCliente();
		System.out.println("O IP do cliente é : " +ipCliente +".");

		if(verificaPermissaoPraPagina(tipoLista, url,listas) == true){

			adicionaSiteAcessado(url, acessos);
			recuperaURL(recuperarURL,url,requestLine, chega_cliente_buffer,vai_cliente,conexao);
			gravaURL();
			imprimeSitesAcessados(acessos);

		}else{				
			System.out.println("Página bloqueada !");
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}


}
