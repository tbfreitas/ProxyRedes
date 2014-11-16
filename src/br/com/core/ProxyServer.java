package br.com.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Classe que inicia o servidor Proxy, recendo os parametros
 * necess�rio de porta, endere�o de IP e tipo da lista passada.
 * 
 * @author Tarc�sio
 *
 *
 * */
public class ProxyServer {
	
	public static void main(String[] args) throws IOException {
		
		int porta = new Integer(args[0]);
		InetAddress ip = InetAddress.getByName(args[1]);
		String tipoLista = args[2];
		String modoBloqueio;
			
		GerenciadorConexoes gerenciador = new GerenciadorConexoes();
		Thread gc = new Thread( (Runnable) (gerenciador));
		gc.start();
		
		if(tipoLista.equals("b")){
			modoBloqueio = "BlackList.";
		}else{
			modoBloqueio = "WhiteList.";
		}
		
		/**
		 *  Impress�o para de par�metros passados ao servidor
		 * */
		System.out.println("INFORMA��ES DO SERVIDOR :");
		System.out.println("");
		System.out.println("A porta usada pelo servidor � a " +porta);
		System.out.println("O IP do servidor � o " +ip);
		System.out.println("O tipo de lista � a " +modoBloqueio);
		System.out.println("");
		/**
		 * Abertura do servidor Socket que recebe os parametros passados
		 */
		ServerSocket conexao = new ServerSocket(porta, 50, ip);
		
		
		while(!gerenciador.finish()){
								
				Socket conex = conexao.accept();
							 		 
				 Runnable requisicao = new TrataRequisicao(conex, tipoLista);
				 
				 Thread	thread = new Thread(requisicao);
				 
				 thread.start();
				 
				gerenciador.addCliente(thread);			
				
		}
		
	}
}


	

