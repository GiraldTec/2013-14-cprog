package ejercicio5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import ejercicio4.Message;
import ejercicio4.Publisher;


public class LogPublisher extends Publisher {
	
	private String dirName;
	
	public LogPublisher(String d){
		super();
		dirName = d;
	}

	public void publish(Message m) {
		try {
			File file = new File(dirName);
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.newLine();
			bw.write(m.getData());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}