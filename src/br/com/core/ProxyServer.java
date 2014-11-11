package br.com.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Classe que inicia o servidor Proxy, recendo os parametros
 * necessário de porta, endereço de IP e tipo da lista passada.
 * 
 * @author Tarcísio
 *
 * */
public class ProxyServer {

	public static void main(String[] args) throws IOException {
		
		int porta = new Integer(args[0]);
		InetAddress ip = InetAddress.getByName(args[1]);
		String tipoLista = args[2];
		String modoBloqueio;
		
		if(tipoLista.equals("b")){
			modoBloqueio = "BlackList.";
		}else{
			modoBloqueio = "WhiteList.";
		}
		
		/**
		 *  Impressão para de parâmetros passados ao servidor
		 * */
		System.out.println("INFORMAÇÕES DO SERVIDOR :");
		System.out.println("");
		System.out.println("A porta usada pelo servidor é a " +porta);
		System.out.println("O IP do servidor é o " +ip);
		System.out.println("O tipo de lista é a " +modoBloqueio);
		System.out.println("");
		
		/**
		 * Abertura do servidor Socket que recebe os parametros passados
		 */
		ServerSocket conexao = new ServerSocket(porta, 50, ip);
		
		while(true){
			
				Socket conex = conexao.accept();
				
				 Runnable requisicao = new TrataRequisicao(conex, tipoLista);
				 
				 Thread	thread = new Thread(requisicao);
				 
				 thread.start();
		}

	}

}
