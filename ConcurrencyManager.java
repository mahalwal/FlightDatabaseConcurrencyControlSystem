//Manish Mahalwal 2016054

import java.io.*; 
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class Flight  {
	String id;
	RockLock lock = new RockLock();
	ArrayList<Passenger> Plist;
	public Flight(String t){
		id=t;
		Plist=new ArrayList<>();
	}
	public  void a_lock(Database dB, Passenger t2) throws InterruptedException{
		
		t2.lock.x_lock();
		dB.add(this, t2);
		t2.lock.x_unlock();
		
	}
	public void c_lock(Database dB, Passenger t2) throws InterruptedException{	
		t2.lock.x_lock();	    	
		dB.delete(this, t2);
	    t2.lock.x_unlock();
	    
	}
	public void h_lock(Database db,Passenger e) throws InterruptedException {

		    	e.lock.x_lock();
		    	db.delete(this, e);
		    	e.lock.x_unlock();
	}
	public void i_lock(Database dB, Passenger e) throws InterruptedException {

			e.lock.x_lock();
		    dB.add(this, e);
		    e.lock.x_unlock();
	}
}

class Passenger implements Serializable {
	String id;
	ArrayList<Flight> Book_flight_list;
	RockLock lock = new RockLock();
	public Passenger(String t){
		id=t;
		Book_flight_list=new ArrayList<>();
	}
	

	public void b_lock(Database db,Flight flight) throws InterruptedException{	
		flight.lock.x_lock();
		db.add_f(flight, this);
		flight.lock.x_unlock();
	
	}

	public void d_lock(Database db, Flight flight) throws InterruptedException {
		flight.lock.x_lock();
		db.delete_f(flight, this); 
		flight.lock.x_unlock();

	}
	public void e_lock(Database db,int e) throws InterruptedException {
		db.PL.get(e).lock.s_lock();
		for(int i=0;i<db.PL.get(e).Book_flight_list.size();i++){
			////System.out.println(db.PL.get(e).Book_flight_list.get(i).id+ " id");
		  	}
		   
		db.PL.get(e).lock.s_unlock();
		
	}
	public void g_lock(Database db,Flight a,Flight b) throws InterruptedException {

		if(a.id.compareTo(b.id)<=0)
		{
			a.lock.x_lock();
			b.lock.x_lock();
			db.replace(this, a, b);
		    b.lock.x_unlock();
		    a.lock.x_unlock();			
		}
		else
		{
			b.lock.x_lock();
			a.lock.x_lock();
			db.replace(this, a, b);
		    a.lock.x_unlock();
		    b.lock.x_unlock();	
		}
	}
}


class RockLock {
	
	int s_status=0;
	int x_status=0;
	
	//1 is already locked
	//0 object can obtain lock
	public void s_lock(){
		if(x_status==1){
			while(x_status==1){
				////System.out.println("x_lock already acquired");
			}
			//System.out.println("s_lock acquired");
			s_status = 1;
		}
		else
			s_status = 1;
	}
	public void s_unlock(){
		if(s_status==1){
			s_status=0;
		}
	}
	
	public void x_lock(){
		if(s_status==1 || x_status==1){
			while(s_status==1 && x_status==1){
				//System.out.println("x_lock already acquired");
			}
			//System.out.println("x_lock acquired");
			x_status = 1;
		}
		else
			x_status = 1;
	}
	public void x_unlock(){
		if(x_status==1){
			x_status = 0;
		}
	}
	
}


class Database 
{
	ArrayList<Flight> FL;
	ArrayList<Passenger> PL;
	//Lock lock = new ReentrantLock();
	public Database(){
		FL=new ArrayList<>();

		PL=new ArrayList<>();
		for(int i=1; i<=5; i++)
			FL.add(new Flight(Integer.toString(i)));

	}
	public void add(Flight t,Passenger a){
		int y=Integer.parseInt(t.id)-1;
		
		this.FL.get(y).Plist.add(a);
	
		  
		
	}
	public void add_f(Flight flight, Passenger a) {
		if(!a.Book_flight_list.contains(flight))
		{
		a.Book_flight_list.add(flight);
		}
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
//		//System.out.println("asdkfjksdl");
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
	
		
		for(int i=0; i<this.PL.size(); i++)
		{
			this.PL.get(i).lock.s_lock();
		}
		for(int i=0; i<this.FL.size(); i++)
		{
			this.FL.get(i).lock.s_lock();
		}
		

		    	for(int y=0;y<this.FL.size();y++){
		    		
		    		//System.out.println("FLight Id  : "+ this.FL.get(y).id);
		    		//System.out.println("Reserved Passngers : ");
		    		for(int h=0;h<this.FL.get(y).Plist.size();h++){
		    			//System.out.println(this.FL.get(y).Plist.get(h).id);
		    		}
		    	}
		for(int i=0; i<this.PL.size(); i++)
		{
			this.PL.get(i).lock.s_unlock();
		}
		for(int i=0; i<this.FL.size(); i++)
		{
			this.FL.get(i).lock.s_unlock();
		}
	}
}



class Transactions implements Runnable {
	Database DB=new Database();
	int trans_id;
	String flight1, flight2, passenger;
	int timestamp=0;

	public Transactions(Database c,int id) throws ClassNotFoundException, IOException{
		this.trans_id = id;
		DB=c;

	}
	public Transactions(Database c,String f,String p, int id) {
		this.trans_id = id;
		this.flight1 = f;
		this.passenger = p;
		DB=c;

	}
	
	public Transactions(Database c,String f1,String f2,String p, int id) {
		this.trans_id = id;
		this.flight1 = f1;
		this.flight2 = f2;
		this.passenger = p;
		
		DB=c;

	}
	
	
	public Transactions(Database c,String p, int id) {
		this.trans_id = id;
		this.passenger = p;
		DB=c;

	}
	
	public void reserve(String f,String p){
		int f_id=Integer.parseInt(f)-1;
		int wh=DB.search(p);
//		//System.out.println(wh+"manis ka doubt");
		if(f_id<5&& f_id>=0 ){
	
			if(wh>=0){

			try{
			DB.PL.get(wh).b_lock(DB,DB.FL.get(f_id));
			DB.FL.get(f_id).a_lock(DB,DB.PL.get(wh));
			}
			catch(InterruptedException t){
				
			}
			finally{
				
			}
			}
			else{

				Passenger t_n=new Passenger(p);
				try{
				t_n.b_lock(DB,DB.FL.get(f_id));
				DB.FL.get(f_id).a_lock(DB,t_n);
				}
				catch(InterruptedException t){
					
				}
				finally{
					
				}
			}				
		}		
		else{
			//System.out.println("Invalid Flight ID: reserve");
		}
		
	}
	public void cancel(String f,String p) {
		int f_id=Integer.parseInt(f)-1;
		int wh=DB.search(p);		
		if(f_id<5&& f_id>=0){		
			if(wh>=0){
//			Lock a=new ReentrantLock();
//			Lock b=new ReentrantLock();
			try{
			DB.PL.get(wh).d_lock(DB,DB.FL.get(f_id));
			DB.FL.get(f_id).c_lock(DB,DB.PL.get(wh));
			}
			catch(InterruptedException t){				
			}
			finally{
				
			}
			}
			else{
				//System.out.println("Passenger not found");
			}
						
		}		
		else{
			//System.out.println("Invalid Flight ID: cancel");
		}
		
	}
	public void my_flights(String p){
		//returns the set of flights on which passenger i has a reservation		
		int wh=DB.search(p);

		if(wh>=0){
			//Lock a=new ReentrantLock();
			try{
				
//				//System.out.println(" found it");
				DB.PL.get(wh).e_lock(DB,wh);
				
				}
				catch(InterruptedException t){				
				}
				finally{
					
				}
							
			}		
		else{
			//System.out.println("Passenger not found");
		}
			
	}
	public void total_reservations(){
		//returns the sum total of all reservations on all flights.
			try{
				DB.f_lock();				
				}
				catch(InterruptedException t){				
				}
				finally{
					
				}
	
	}
	public void transfer(String f1,String f2,String p){
		//transfer passenger i from flight F1 to F2. This transaction should have no impact if the passenger is not found in F1 or there is no room in F2.
		int f_id1=Integer.parseInt(f1)-1;
		int f_id2=Integer.parseInt(f2)-1;
		int wh=DB.search(p);		
		if(f_id1<5&& f_id1>=0&&f_id2<5&& f_id2>=0){		
			if(wh>=0){

			try{
			DB.PL.get(wh).g_lock(DB,DB.FL.get(f_id1),DB.FL.get(f_id2));
			
			
			DB.FL.get(f_id1).h_lock(DB,DB.PL.get(wh));
			DB.FL.get(f_id2).i_lock(DB,DB.PL.get(wh));
			}
			catch(InterruptedException t){				
			}
			finally{
				
			}		
			}
			else{
				//System.out.println("Passenger not Found");
			}
		}		
		else{
			//System.out.println("Invalid Flight ID: transfer");
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
			//System.out.println();
		}
		else if(this.trans_id==5)
		{
			this.transfer(this.flight1, this.flight2, passenger);
		}
		
	}
}

class Reader{
    static BufferedReader reader;
    static StringTokenizer tokenizer;
    /** call this method to initialize reader for InputStream */
    static void init(InputStream input) {
        reader = new BufferedReader(
                new InputStreamReader(input) );
        tokenizer = new StringTokenizer("");
    }
    /** get next word */
    static String next() throws IOException {
        while ( ! tokenizer.hasMoreTokens() ) {
            //TODO add check for eof if necessary
            tokenizer = new StringTokenizer(
                    reader.readLine() );
        }
        return tokenizer.nextToken();
    }

    static int nextInt() throws IOException {
        return Integer.parseInt( next() );
    } 

    static double nextDouble() throws IOException {
        return Double.parseDouble( next() );
    }

    static float nextFloat() throws IOException {
        return Float.parseFloat( next() );
    }
}

class ConcurrencyManager {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		Reader.init(System.in);
//		Database DB=new Database();	
		Random ran = new Random(); 
		ArrayList<Transactions> ts=new ArrayList<>();
		Database DB=new Database();	
		int x = 50;
		for(int i=0;i<x;i++){
			int y = ran.nextInt(5);
			//System.out.println("Transaction type: " + (1+y));
			if(y==0)
			{

				int fight = 1 + ran.nextInt(5);
				int pass= 1+ ran.nextInt(3);
				//System.out.println("To reserve passenger "+pass+" in the flight "+fight);
				
				Transactions R=new Transactions(DB,Integer.toString(fight),Integer.toString(pass), 1);
				ts.add(R);
				
				
			}
			else if (y==1){
				int fight=1+ran.nextInt(5);
				int pass=1+ran.nextInt(5);
				//System.out.println("To cancel passenger "+pass+" from the flight "+fight);

				Transactions R=new Transactions(DB,Integer.toString(fight),Integer.toString(pass), 2);
				ts.add(R);
			}
			else if(y==2){
				int pass=1+ran.nextInt(5);
				//System.out.println("Show all the flights of the passenger " + pass);
				Transactions R=new Transactions(DB,Integer.toString(pass), 3);
				ts.add(R);
			}
			else if(y==3){
				Transactions R=new Transactions(DB,4); 
				ts.add(R);
				
				
			}
			else if(y==4){
				int fight1 = 1 + ran.nextInt(5);
				int fight2 = 1 + ran.nextInt(5);
				int pass = 1 + ran.nextInt(5);
				//System.out.println("To transfer passenger "+pass+" from the flight "+fight1+" to the flight "+fight2);

				Transactions R=new Transactions(DB,Integer.toString(fight1),Integer.toString(fight2),Integer.toString(pass), 5);
				ts.add(R);

			}
		}

		long startTime = System.currentTimeMillis();
		ExecutorService exec = Executors.newFixedThreadPool(6);
		
		for(int i=0;i<ts.size();i++){
			long startTime2 = System.currentTimeMillis();
			exec.submit(ts.get(i));
			long endTime2   = System.currentTimeMillis();
			long totalTime2 = endTime2 - startTime2;
			Thread.sleep(totalTime2);
			
		}
		if(!exec.isTerminated()) {
			exec.shutdown();
//			exec.awaitTermination(10L,TimeUnit.SECONDS);
			}
		long endTime   = System.currentTimeMillis();
//		endTime /= 1000000;
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
	}
}
