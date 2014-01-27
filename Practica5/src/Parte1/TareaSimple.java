package Parte1;


public class TareaSimple implements Runnable
{
  private int MAX_COUNT;
  private int c;
  
  public TareaSimple(int maxCount) 
  { 
    this.MAX_COUNT = maxCount;
    this.c = 0;
  }
  
  @Override
  public void run()
  {
    while(c < MAX_COUNT) {
      c++;
    }
  }
  
}
