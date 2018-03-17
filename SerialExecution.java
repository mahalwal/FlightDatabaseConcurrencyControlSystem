//Manish Mahalwal 2016054
//Raj Kamal yadav 2016076

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DatabaseSerial
{
	ArrayList<Flight> Pl;
	ArrayList<Passenger> PL;
	RockLockS lock = new RockLockS();
	public DatabaseSerial(){
		Pl=new ArrayList<>();
		
		PL=new ArrayList<>();
		Pl.add(new Flight("1"));
		Pl.add(new Flight("2"));
		Pl.add(new Flight("3"));
		Pl.add(new Flight("4"));
		Pl.add(new Flight("5"));

	}
	public void add(Flight t,Passenger a){
		int y=Integer.parseInt(t.id)-1;
		this.Pl.get(y).Plist.add(a);
		  
		
	}
	public void add_f(Flight flight, Passenger a) {
		
		a.Book_flight_list.add(flight);
		
		int psa=search(a.id);
		if(psa<0){
			PL.add(a);
		}
	}
	public void delete(Flight t,Passenger s){
		int yes=0;
		for(int i=0;i<t.Plist.size();i++)
		{
			if(t.Plist.get(i).id.equals(s.id)){
				t.Plist.remove(i);
				yes=1;				
			}
		}
		if(yes==0){
			//System.out.println("No Passenger with the given name exist on flight");
		}
	}
	public void delete_f(Flight t,Passenger s){
	
		for(int i=0;i<t.Plist.size();i++)
		{
			if(s.Book_flight_list.get(i).id.equals(t.id)){
				s.Book_flight_list.remove(i);					
			}
		}
		
	}
	public int search_on_flight(Flight t,String s){
		for(int i=0;i<t.Plist.size();i++)
		{
			if(t.Plist.get(i).id.equals(s)){
				return i;
				
				
			}
		}
		return -1;
	}
	public int search(String s){
		for(int i=0;i<PL.size();i++)
		{
			if(PL.get(i).id.equals(s)){
				return i;
				
				
			}
		}
		return -1;
	}
	public void replace(Passenger h,Flight a,Flight b){
		for(int l=0;l<h.Book_flight_list.size();l++){
			if(h.Book_flight_list.get(l).id.equals(a.id)){
				h.Book_flight_list.remove(l);
			}
		}
		h.Book_flight_list.add(b);
	}
	public  void f_lock() throws InterruptedException{	

		    	for(int y=0;y<this.Pl.size();y++){
		    		
		    		//System.out.println("FLight Id  : "+ this.Pl.get(y).id);
		    		//System.out.println("Reserved Passngers : ");
		    		for(int h=0;h<this.Pl.get(y).Plist.size();h++){
		    			//System.out.println(this.Pl.get(y).Plist.get(h).id);
		    		}
		    	}
		
	}	
	
}

class Flight  {
	String id;
	ArrayList<Passenger> Plist;
	Lock lock = new ReentrantLock();
	public Flight(String t){
		id=t;
		Plist=new ArrayList<>();
	}

	public  void c_lock(DatabaseSerial dB, Passenger t2) throws InterruptedException{	

		    	dB.delete(this, t2);

		
	}
	public void h_lock(DatabaseSerial db,Passenger e) throws InterruptedException {

		    	
		    	db.delete(this, e);

	}
	public void i_lock(DatabaseSerial dB, Passenger e) throws InterruptedException {


		    	
		    	dB.add(this, e);

	
	}
}
class Passenger implements Serializable {
	String id;
	ArrayList<Flight> Book_flight_list;
	Lock lock=new ReentrantLock();
	public Passenger(String t){
		id=t;
		Book_flight_list=new ArrayList<>();
		
	}



	public void d_lock(DatabaseSerial db, Flight flight) throws InterruptedException {

		    	db.delete_f(flight, this); 

	}
	public void e_lock(DatabaseSerial db,int e) throws InterruptedException {

		    	//System.out.println("printing it "+e+"  "+db.PL.get(e).Book_flight_list.size());
		    	
		    	for(int i=0;i<db.PL.get(e).Book_flight_list.size();i++){
		    		//System.out.println(db.PL.get(e).Book_flight_list.get(i).id);
		    	}

	}
	public void g_lock(DatabaseSerial db,Flight a,Flight b) throws InterruptedException {

		 db.replace(this, a, b);

	}
	
	
}
class RockLockS {
	
	int s_status=0;	
	//1 is already locked
	//0 object can obtain lock
	public void lock(){
		if(s_status==1){
			while(s_status==1){
				//System.out.println("x_lock already acquired");
			}
			//System.out.println("s_lock acquired");
			s_status = 1;
		}
		else
			s_status = 1;
	}
	public void unlock(){
		if(s_status==1){
			s_status=0;
		}
	}

	
}
class TransactionsSerial implements Runnable {
	DatabaseSerial DB;
	int trans_id;
	String flight1, flight2, passenger;
	int timestamp=0;
	
	public TransactionsSerial(DatabaseSerial c,int id) throws ClassNotFoundException, IOException{
		this.trans_id = id;
		DB=c;

	}
	public TransactionsSerial(DatabaseSerial c,String f,String p, int id) {
		this.trans_id = id;
		this.flight1 = f;
		this.passenger = p;
		DB=c;
	}
	
	public TransactionsSerial(DatabaseSerial c,String f1,String f2,String p, int id) {
		this.trans_id = id;
		this.flight1 = f1;
		this.flight2 = f2;
		this.passenger = p;
		
		DB=c;

	}
	
	
	public TransactionsSerial(DatabaseSerial c,String p, int id) {
		this.trans_id = id;
		this.passenger = p;
		DB=c;

	}
	
	public void reserve(String f,String p){
		int f_id=Integer.parseInt(f)-1;
		int wh=DB.search(p);		
		if(f_id<5&& f_id>=0 ){
	
			if(wh>=0){
				DB.lock.lock();
				DB.add_f(DB.Pl.get(f_id), DB.PL.get(wh)); 
				DB.add(DB.Pl.get(f_id), DB.PL.get(wh));
				DB.lock.unlock();

			}
			else{
				////System.out.println("therajskldfdsh");

				DB.lock.lock();
				Passenger t_n=new Passenger(p);

					DB.add_f(DB.Pl.get(f_id), t_n); 
					DB.add(DB.Pl.get(f_id),t_n);
					DB.lock.unlock();
			}	
		}		
		else{
			//System.out.println("Invalid Flight ID here");
		}
		
	}
	public void cancel(String f,String p) {
		int f_id=Integer.parseInt(f)-1;
		int wh=DB.search(p);		
		if(f_id<5&& f_id>=0 && wh>=0){			

				DB.lock.lock();
				DB.delete_f(DB.Pl.get(f_id), DB.PL.get(wh));
				DB.delete(DB.Pl.get(f_id), DB.PL.get(wh));
				DB.lock.unlock();
			
		}		
		else{
			//System.out.println("Invalid Flight ID");
		}
		
	}
	public void my_flights(String p){
		//returns the set of flights on which passenger i has a reservation		
		int wh=DB.search(p);
		
		if(wh>=0){
			//Lock a=new ReentrantLock();
			DB.lock.lock();
	    	for(int i=0;i<DB.PL.get(wh).Book_flight_list.size();i++){
	    		//System.out.println(DB.PL.get(wh).Book_flight_list.get(i).id);
	    	}
	    	DB.lock.unlock();

		
			}		
			
	}
	public void total_reservations(){
		//returns the sum total of all reservations on all flights.
		DB.lock.lock();
	    	for(int y=0;y<DB.Pl.size();y++){
	    		
	    		//System.out.println("FLight Id  : "+ DB.Pl.get(y).id);
	    		//System.out.println("Reserved Passngers : ");
	    		for(int h=0;h<DB.Pl.get(y).Plist.size();h++){
	    			//System.out.println(DB.Pl.get(y).Plist.get(h).id);
	    		}
	    	}
	    DB.lock.unlock();

	}
	public void transfer(String f1,String f2,String p){
		//transfer passenger i from flight F1 to F2. This transaction should have no impact if the passenger is not found in F1 or there is no room in F2.
		int f_id1=Integer.parseInt(f1)-1;
		int f_id2=Integer.parseInt(f2)-1;
		int wh=DB.search(p);		
		if(f_id1<5&& f_id1>=0&&f_id2<5&& f_id2>=0 && wh>=0){			
			DB.lock.lock();
		    	DB.replace(DB.PL.get(wh),DB.Pl.get(f_id1),DB.Pl.get(f_id2));
		    	DB.delete(DB.Pl.get(f_id1), DB.PL.get(wh));
		    	DB.add(DB.Pl.get(f_id2), DB.PL.get(wh));
		    	
		    DB.lock.unlock();
				
		}		
		else{
			//System.out.println("Invalid Flight ID");
		}
	
		
		
	}
	@Override
	public void run() {
		if(this.trans_id==1)
		{
			this.reserve(this.flight1, this.passenger);
			
		}
		else if(this.trans_id==2)
		{
			this.cancel(this.flight1, this.passenger);
		}
		else if(this.trans_id==3)
		{
			this.my_flights(this.passenger);
		}
		else if(this.trans_id==4)
		{
			this.total_reservations();
		}
		else if(this.trans_id==5)
		{
			this.transfer(this.flight1, this.flight2, passenger);
		}	
	}
}
public class SerialExecution {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		//Database DB=new Database();	
		Random ran = new Random(); 
		ArrayList<TransactionsSerial> ts=new ArrayList<>();
		DatabaseSerial DB=new DatabaseSerial();	
		int x = 50;
		for(int i=0;i<x;i++){
			int y = ran.nextInt(5);
			//System.out.println("Transaction type: " + (1+y));
			if(y==0)
			{

				int fight = 1 + ran.nextInt(5);
				int pass= 1+ ran.nextInt(3);
				//System.out.println("To reserve passenger "+pass+" in the flight "+fight);
				
				TransactionsSerial R=new TransactionsSerial(DB,Integer.toString(fight),Integer.toString(pass), 1);
				ts.add(R);
				
				
			}
			else if (y==1){
				int fight=1+ran.nextInt(5);
				int pass=1+ran.nextInt(5);
				//System.out.println("To cancel passenger "+pass+" from the flight "+fight);

				TransactionsSerial R=new TransactionsSerial(DB,Integer.toString(fight),Integer.toString(pass), 2);
				ts.add(R);
			}
			else if(y==2){
				int pass=1+ran.nextInt(5);
				//System.out.println("Show all the flights of the passenger " + pass);
				TransactionsSerial R=new TransactionsSerial(DB,Integer.toString(pass), 3);
				ts.add(R);
			}
			else if(y==3){
				TransactionsSerial R=new TransactionsSerial(DB,4); 
				ts.add(R);
				
				
			}
			else if(y==4){
				int fight1 = 1 + ran.nextInt(5);
				int fight2 = 1 + ran.nextInt(5);
				int pass = 1 + ran.nextInt(5);
				//System.out.println("To transfer passenger "+pass+" from the flight "+fight1+" to the flight "+fight2);

				TransactionsSerial R=new TransactionsSerial(DB,Integer.toString(fight1),Integer.toString(fight2),Integer.toString(pass), 5);
				ts.add(R);

			}
		}

		long startTime = System.currentTimeMillis();
		ExecutorService exec = Executors.newFixedThreadPool(1);
		
		for(int i=0;i<ts.size();i++){
			long startTime2 = System.currentTimeMillis();
			exec.submit(ts.get(i));
			long endTime2   = System.currentTimeMillis();
			long totalTime2 = endTime2 - startTime2;
			Thread.sleep(1);
		}
		if(!exec.isTerminated()) {
			exec.shutdown();
			exec.awaitTermination(10L,TimeUnit.SECONDS);}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
	}
}
