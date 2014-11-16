package br.com.core;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * Classe que implementa runnable para quando for digitado "p",
 * o programe pare e sejam fechadas todas as conexoes abertas
 * em threads
 * 
 * @author Tarcísio
 *
 */
public class GerenciadorConexoes  implements Runnable{

	boolean exit = false;
	Scanner scan = new Scanner(System.in);
	String opt ;
	LinkedList<Thread> clientes = new LinkedList<Thread>();
	
	
	public void addCliente(Thread cliente){
		clientes.add(cliente);
	}

	public boolean finish(){
		
		return exit;
	}
	
	/**
	 * Neste metodo run, quando for digitado "p",
	 * é enviado um booleando para o runnable da classe
	 * ProxyServer , onde todos as threads sao fechadas
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!exit){
			
			opt = scan.nextLine();
			if(opt.equals("p")){
				
				for(Thread t : clientes){
					
					t.interrupt();
					
				}
				
				exit = true;
				System.out.println("Programa fechado com sucesso!");
			}
		}
	}
	
	

}
