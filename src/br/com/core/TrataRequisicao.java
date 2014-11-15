package br.com.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	String requisicao;
	final static String CRLF = "\r\n";
	URL recuperarURL;

	/**
	 * Constructor
	 * 
	 * @param conexao
	 * @param tipoLista
	 */
	public TrataRequisicao(Socket conexao, String tipoLista) {
		this.conexao = conexao;
		this.tipoLista = tipoLista;
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
		chega_cliente_buffer = new BufferedReader(new InputStreamReader(
				chega_cliente));

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

		requisicao = chega_cliente_buffer.readLine();

		// Transformando a string numa StringTokenizer para recuperar a URL
		StringTokenizer tokens = new StringTokenizer(requisicao);
		tokens.nextToken();
		url = tokens.nextToken();
		
		if(!url.startsWith("http")) {
			url = "http://"+url;
		}
		
		try {
			recuperarURL = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Classe que grava em um arrayList todos os sites da white black lists,
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
	 * Método para verificar IP do cliente e porta utilizada pelo mesmo e
	 * imprimir
	 */
	public void verificaCliente() {

		ipCliente = conexao.getRemoteSocketAddress().toString();
		//System.out.println("O ip do cliente e  porta que este utiliza é a : "
		//+ ipCliente);

	}

	public static void adicionaSiteAcessado(String url,
			ArrayList<SitesAcessados> acessos) throws IOException {

		SitesAcessados auxiliar = new SitesAcessados();

		for (int i = 0; i < acessos.size(); i++) {
			auxiliar = acessos.get(i);

			if (url == auxiliar.getSite()) {
				auxiliar.setCont(auxiliar.getCont() + 1);
				return;
			}
		}

		auxiliar.setSite(url);
		auxiliar.setCont(1);

		acessos.add(auxiliar);

	}

	/**
	 * Metodo de teste para impressao dos sites acessados, percorrendo a
	 * arraylist de acessos
	 * 
	 * @param acessos
	 */
	public void imprimeSitesAcessados(ArrayList<SitesAcessados> acessos) {

		SitesAcessados auxiliar = null;

		for (int i = 0; i < acessos.size(); i++) {
			auxiliar = acessos.get(i);

			System.out.println("O site " + auxiliar.getSite() + "foi visitado "
					+ auxiliar.getCont() + " vezes.");
		}

	}

	/**
	 * Metodo que abre conexao com a URL desejada, recupera suas informacoes e
	 * salva num InputStream para passar para o cliente atraves do metodo
	 * sendBytes
	 * 
	 * @param recuperarURL
	 * @param chega_cliente_buffer
	 * @param vai_cliente
	 * @param conexao
	 * @throws Exception
	 */
	public void recuperaURL(URL recuperarURL,BufferedReader chega_cliente_buffer, DataOutputStream vai_cliente,Socket conexao) throws Exception {

		URLConnection urlC = recuperarURL.openConnection();
		
		InputStream in = urlC.getInputStream();
		sendBytes(in, vai_cliente);

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

	public void salvarURLBlock(URL recuperarURL,	BufferedReader chega_cliente_buffer, DataOutputStream vai_cliente,	Socket conexao) throws Exception {

		URLConnection urlC = recuperarURL.openConnection();
		urlC.connect();

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

				adicionaSiteAcessado(url, acessos);
				recuperaURL(recuperarURL, chega_cliente_buffer, vai_cliente,
						conexao);

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
