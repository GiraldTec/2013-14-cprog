package ejercicio4;

public class Message implements Comparable<Message>{
	

	private int id;
	private String data;
	
	public Message(int i,String d){
		id=i;
		data=d;
	}
	
	public int getId(){
		return id;
	}
	
	public String getData(){
		return data;
	}
	
	public int compareTo(Message o) {
		if(id<=o.getId()) return -1;
		if(id>=o.getId()) return 1;
		return 0;
	}

}
