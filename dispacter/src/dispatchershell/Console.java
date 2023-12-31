package dispatchershell;
import java.io.IOException;
import java.awt.SystemColor;

//Mesajı konsola kaydetmek için kullanılan yardımcı program sınıfı
public class Console {
	public static void log(String text)
	{
		try {
			new ProcessBuilder("cmd", "/c", "echo " + text)
							.inheritIO()
							.start()
							.waitFor();
		} catch (InterruptedException|IOException e) {
			throw new RuntimeException(e);
	    }
	}
	
	public static void printProcessState(IProcess process, String state)
	{
		System.out.print(process.getColor().colorCode);
		
		Console.log(Timer.getCurrentTime() + ".0000 sec\tprocess "
			+ state + "\t(id : " + String.format("%04d",process.getId()) + " priority : " + 
			process.getPriority().ordinal() + " remaining time: " + 
			(process.getBurstTime() - process.getElapsedTime()) + ")");
		
		System.out.print(Color.RESET.colorCode);
	}
}