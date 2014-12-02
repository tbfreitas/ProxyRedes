package br.com.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
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
	DataOutputStream vai_cliente;
	BufferedReader chega_cliente_buffer;
	String requisicao;
	final static String CRLF = "\r\n";
	long tempoInicial;
	long tempoFinal;
	URL recuperarURL;
	InetAddress ip;

	/**
	 * Constructor
	 * 
	 * @param conexao
	 * @param tipoLista
	 * @param ip 
	 */
	public TrataRequisicao(Socket conexao, String tipoLista, InetAddress ip) {
		this.conexao = conexao;
		this.tipoLista = tipoLista;
		this.ip = ip;
	}

	/**
	 * Classe para pegar no Socket conexao e salvar em Streams a chegada e saida
	 * de dados
	 * 
	 * @param conexao
	 * @throws IOException
	 */

	public void estabeleConexao(Socket conexao) throws IOException {

		InputStream chega_cliente;
		chega_cliente = conexao.getInputStream();

		vai_cliente = new DataOutputStream(conexao.getOutputStream());
		chega_cliente_buffer = new BufferedReader(new InputStreamReader(chega_cliente));

	}

	/**
	 * Classe que pega a String requisicao apontada para o Buffer de entrada de
	 * dados da Stream. Pega a url corretada atraves da tokenizacao da String
	 * requisicao e posteriormente passa para uma classe URL esta string
	 * 
	 * @param chega_cliente_buffer
	 * @throws IOException
	 */
	public void pegaRequisicao(BufferedReader chega_cliente_buffer)
			throws IOException {

		try{
			requisicao = chega_cliente_buffer.readLine();

			StringTokenizer tokens = new StringTokenizer(requisicao);
			tokens.nextToken();
			url = tokens.nextToken();
			
		}catch(Exception e){
			e.toString();
			System.out.println(e);
		}	

		try {
			
			if(url.isEmpty()){
				System.out.println("TÁ VAZIA ESSA URL");
			}
			
			if(!url.startsWith("http")) {
	
				url = "http://"+url;
			}
		
			recuperarURL = new URL(url);
			
		} catch (Exception e) {
		
			System.out.println(e);
		}
	}
	

	/**
	 * Classe que grava em um arrayList todos os sites da white ou  black lists,
	 * gravados no arquivos .txt
	 * 
	 * @param tipoLista
	 * @throws IOException
	 */
	public void gravandoListas(String tipoLista) throws IOException {

		if (tipoLista == "w") {

			BufferedReader in = new BufferedReader(new FileReader("whitelist.txt"));
			String linha = in.readLine();

			while (linha != null) {
				listas.add(linha);
				linha = in.readLine();
			}

		} else {

			BufferedReader in = new BufferedReader(new FileReader("blacklist.txt"));
			String linha = in.readLine();

			while (linha != null) {
				listas.add(linha);
				linha = in.readLine();
			}
		}
	}

	/**
	 * Classe que percorre a ArrayLists de Sites. Se o parâmetro for whitelist,
	 * percorre e se achar , retorna true. Se não achar na whitelist, retorna
	 * false. A lógica da blacklist é inversa. *
	 * 
	 * @param tipoLista
	 * @return boolean
	 * @throws FileNotFoundException
	 */
	public boolean verificaPermissaoPraPagina(String tipoLista, String url,
			ArrayList<String> listas) throws FileNotFoundException {

		if (tipoLista.equals("w")) {

			for (String site : listas) {
				if (site.equals(url)) {
					return true;
				}
			}
			return false;

		} else {

			for (String site : listas) {
				if (site.equals(url)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Método para verificar IP do cliente e porta utilizada pelo mesmo e jogar numa String
	 * imprimir junto com a URL requisitada
	 */
	public void verificaCliente() {

		 ipCliente = conexao.getRemoteSocketAddress().toString();
	
	}

	/**
	 * Metodo que abre conexao com a URL desejada, recupera suas informacoes e
	 * salva num InputStream para passar para o cliente atraves do metodo
	 * sendBytes.
	 * 
	 * E salvo tambem no arquivo IPS, o ip do cliente que fez a requisicao
	 * 
	 * @param recuperarURL
	 * @param chega_cliente_buffer
	 * @param vai_cliente
	 * @param conexao
	 * @throws Exception
	 */
	synchronized public void recuperaURL(URL recuperarURL,BufferedReader chega_cliente_buffer, DataOutputStream vai_cliente,Socket conexao) throws Exception {

		String i[] = ipCliente.split(":");
		String salva = i[0];

		Writer arquivo2 = new BufferedWriter(new FileWriter("IPS.txt", true));
		arquivo2.append(salva+"\r\n");
		arquivo2.close();

		arquivo2 = new BufferedWriter(new FileWriter("URLSacessados.txt", true));
		arquivo2.append(url+"\r\n");
		arquivo2.close();

		URLConnection urlC = recuperarURL.openConnection();
		
		try{
				
			tempoInicial = System.currentTimeMillis();   
			
			InputStream in = urlC.getInputStream();
			sendBytes(in, vai_cliente);
			
			 tempoFinal = System.currentTimeMillis(); 
			 tempoFinal = tempoFinal - tempoInicial;
			
		}catch(Exception e){
			e.toString();
			System.out.println(e);
		}

		arquivo2 = new BufferedWriter(new FileWriter("TempoPagina.txt", true));
		arquivo2.append(tempoFinal+ " ms : " +url +"\r\n");
		arquivo2.close();		
		
		vai_cliente.close();
		chega_cliente_buffer.close();
		conexao.close();
	}

	/**
	 * Metodo para envio de informacoes para o cliente atraves do OutPutStream
	 * 
	 * @param teste
	 * @param os
	 * @throws Exception
	 */
	private static void sendBytes(InputStream in, OutputStream vai_cliente)
			throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream.
		while ((bytes = in.read(buffer)) != -1) {
			vai_cliente.write(buffer, 0, bytes);
		}
	}

	/**
	 * 
	 * Metodo chamado quando a URL eh bloqueada. Primeiro , a conexao eh aberta,
	 * em seguida, eh criado um arquivo txt para salvar o conteudo html da pagina bloqueada.
	 * Logo apos, eh salvo no arquivo URLSbarradas a ULR que foi barrada jntamente com o IP 
	 * que fez a requisicao.
	 * No final , eh voltado ao cliente a mensagem de URL bloqueada
	 * 
	 * @param recuperarURL
	 * @param chega_cliente_buffer
	 * @param vai_cliente
	 * @param conexao
	 * @throws Exception
	 */
	synchronized public void salvarURLBlock(URL recuperarURL,	BufferedReader chega_cliente_buffer, DataOutputStream vai_cliente,	Socket conexao) throws Exception {

		URLConnection urlC = recuperarURL.openConnection();
		BufferedReader paginaBloq = new BufferedReader(new InputStreamReader(urlC.getInputStream()));

		String teste = url;
		String s[] = teste.split("\\."); 

		teste = (s[1] +".txt");

		OutputStream os = new FileOutputStream(teste);
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		String linhaSite;
		while((linhaSite = paginaBloq.readLine()) != null ){
			bw.write(linhaSite+"\r\n");
		}

		bw.close();

		Writer arquivo = new BufferedWriter(new FileWriter("URLSbarradas.txt", true));
		arquivo.append(" A url " +url +"foi acessada pelo IP: " +ipCliente +".\r\n");
		arquivo.close();		

		String i[] = ipCliente.split(":");
		String salva = i[0];

		Writer arquivo2 = new BufferedWriter(new FileWriter("IPS.txt", true));
		arquivo2.append(salva+"\r\n");
		arquivo2.close();		

		String block = "Página bloqueada pelo administrador da rede. Favor entrar em contato com a administração.";
		vai_cliente.writeBytes(block);		

		vai_cliente.close();
		chega_cliente_buffer.close();
		conexao.close();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {

			estabeleConexao(conexao);
			pegaRequisicao(chega_cliente_buffer);
			gravandoListas(tipoLista);
			verificaCliente();

			if (verificaPermissaoPraPagina(tipoLista, url, listas) == true) {
				recuperaURL(recuperarURL, chega_cliente_buffer, vai_cliente,conexao);
				
				System.out.println("IP CLIENTE E PORTA :");
				System.out.println(ipCliente);
				System.out.println("URL DE DESTINO :");
				System.out.println(url);
				System.out.println("IP DE DESTINO");
				System.out.println(ip);
				System.out.println("TEMPO PARA RECUPERAR A PÁGINA:");
				System.out.println(tempoFinal+ " ms .");
				System.out.println();

			} else {
				
				salvarURLBlock(recuperarURL, chega_cliente_buffer, vai_cliente,	conexao);
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
