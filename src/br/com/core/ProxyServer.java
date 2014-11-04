package br.com.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Classe que inicia o servidor Proxy, recendo os parametros
 * necess�rio de porta, endere�o de IP e tipo da lista passada.
 * 
 * @author Tarc�sio
 *
 * */
public class ProxyServer {

	public static void main(String[] args) throws IOException {
		
		int porta = new Integer(args[0]);
		InetAddress ip = InetAddress.getByName(args[1]);
		String tipoLista = args[2];
		
		/**
		 *  Impress�o para testes corretos dos par�metros
		 * */
		
		System.out.println("a porta � a :" +porta);
		System.out.println("O ip � o :" +ip);
		System.out.println("O tipo de lista � a :" +tipoLista);
		
		//Abrindo conex�o de servidor na porta passada , com o tamanho de 50
		//requests na fila e o IP passado como par�metro, que � o do localhost
		ServerSocket conexao = new ServerSocket(porta, 50, ip);
		
		while(true){
			
				Socket conex = conexao.accept();
				
				 Runnable requisicao = new TrataRequisicao(conex, tipoLista);
				 
				 Thread	thread = new Thread(requisicao);
				 
				 thread.start();
		}

	}

}
