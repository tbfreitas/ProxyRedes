package br.com.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


/**
 * Classe que inicia o servidor Proxy, recendo os parametros
 * necessário de porta, endereço de IP e tipo da lista passada.
 * 
 * @author Tarcísio
 *
 * */

public class ProxyServer {

	static ArrayList<ClientesAcessaram> acessos = new ArrayList<ClientesAcessaram>();
	static ArrayList<SitesAcessados> sites = new ArrayList<SitesAcessados>();

	static boolean entrou = false;

	static void lerIPS() throws IOException{
		ClientesAcessaram auxiliar;

		BufferedReader in = new BufferedReader(new FileReader("IPS.txt"));
		String linha;	

		while((linha =in.readLine())!= null){

			for(ClientesAcessaram s : acessos){
				auxiliar = s;

				if(linha.equals(s.getIp())){

					s.setRequisicao(s.getRequisicao() +1);
					entrou = true;

				}
			}

			if(!entrou){
				auxiliar= new ClientesAcessaram();
				
				auxiliar.setIp(linha);
				auxiliar.setRequisicao(1);
				acessos.add(auxiliar);
				entrou =false;
			}
		}

		Collections.sort(acessos);

		for(ClientesAcessaram s : acessos){
			System.out.println("O IP : " +s.getIp() + " acessou " +s.getRequisicao() +" vezes o Proxy."  );
		}
	}

	static void lerURLSacessadas() throws IOException{
		SitesAcessados auxiliar ;

		BufferedReader in = new BufferedReader(new FileReader("URLSacessados.txt"));
		String linha;	

		entrou = false;

		while((linha =in.readLine())!= null){

			for(SitesAcessados s : sites){
				auxiliar = s;

				if(linha.equals(s.getSite())){			

					s.setCont(s.getCont() +1);
					entrou = true;
				}			

			}

			if(!entrou){

				auxiliar= new SitesAcessados();
				
				auxiliar.setSite(linha);
				auxiliar.setCont(1);

				sites.add(auxiliar);
				entrou =false;
			}

		}

		Collections.sort(sites);

		for(SitesAcessados m : sites){
			System.out.println(m.getCont()+"  --   "+m.getSite());
		}
	}
	
	static void lerURLSbarradas() throws Exception{
		
		BufferedReader in = new BufferedReader(new FileReader("URLSbarradas.txt"));
		String linha;	

		while((linha = in.readLine()) != null){
			System.out.println(linha);
		}
		
	}


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
		 *  Impressão para de parâmetros passados ao servidor
		 * */
		System.out.println("INFORMAÇÕES DO SERVIDOR :");
		System.out.println("");
		System.out.println("A porta usada pelo servidor é a " +porta);
		System.out.println("O IP do servidor é o " +ip);
		System.out.println("O tipo de lista é a " +modoBloqueio);
		System.out.println("");

		ServerSocket conexao = new ServerSocket(porta, 50, ip);


		while(!gerenciador.finish()){

			Socket conex = conexao.accept();

			Runnable requisicao = new TrataRequisicao(conex, tipoLista, ip);

			Thread	thread = new Thread(requisicao);

			thread.start();

			gerenciador.addCliente(thread);			

		}

	}
}




