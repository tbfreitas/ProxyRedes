package br.com.core;

import java.util.LinkedList;
import java.util.Scanner;

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
			}
		}
	}
	
	

}
