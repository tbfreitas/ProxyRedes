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
		
		/**
		 *  Impressão para testes corretos dos parâmetros
		 * */
		
		System.out.println("a porta é a :" +porta);
		System.out.println("O ip é o :" +ip);
		System.out.println("O tipo de lista é a :" +tipoLista);
		
		//Abrindo conexão de servidor na porta passada , com o tamanho de 50
		//requests na fila e o IP passado como parâmetro, que é o do localhost
		ServerSocket conexao = new ServerSocket(porta, 50, ip);
		
		while(true){
			
				Socket conex = conexao.accept();
				
				 Runnable requisicao = new TrataRequisicao(conex, tipoLista);
				 
				 Thread	thread = new Thread(requisicao);
				 
				 thread.start();
		}

	}

}
