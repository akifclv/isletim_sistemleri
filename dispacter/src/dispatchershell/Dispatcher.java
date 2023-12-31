package dispatchershell;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Dispatcher implements IDispatcher{
	private static IDispatcher instance;
	private static IRealTimeQueue realTimeQueue;
	private static IUserJob userJob;
	private String filePath;
	private Color[] colors;
	private List<IProcess> allProcesses;
	private static List<IProcess> pendingProcesses;
	private int quantum;
	private static int maxWaitingTime;

	public Dispatcher(String filePath, int quantum, int _maxWaitingTime)
	{
		this.quantum = quantum;
		this.filePath = filePath;
		maxWaitingTime = _maxWaitingTime;
		realTimeQueue = new RealTimeQueue();
		userJob = new UserJob(this.quantum);
		pendingProcesses = new ArrayList<IProcess>();
		this.allProcesses = new ArrayList<>();
		this.colors = new Color[]
		{
			Color.BLUE, Color.CYAN, 
			Color.GREEN, Color.PURPLE, 
			Color.RED, Color.WHITE, 
			Color.YELLOW
		};
	}
	
	//Bu sınıftan bir örnek oluşturmak için Singleton modeli kullanılıyor
	public static IDispatcher getInstance(String filePath, int quantum, int maxWaitingTime)
	{
		if (instance == null) {
			instance = new Dispatcher(filePath, quantum, maxWaitingTime);
		}
		return instance;
	}

	@Override
	public IDispatcher readFile() 
	{
		//
		//İşlemler önce önceliklerine, ardından varış saatlerine göre sıralanıyor
		
		PriorityQueue<IProcess> priorityQueue = new PriorityQueue<IProcess>(
					new ProcessComparator()
				);
		
		try(BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
			String line;
			
			int ID = 0;
			while ((line = br.readLine()) != null)
			{
				String[] param = line.split(",");
				
				int arrivalTime = Integer.parseInt(param[0].trim());
				Priority priority = this.convertToPriority(Integer.parseInt(param[1].trim()));
				int burstTime = Integer.parseInt(param[2].trim());
				
				//Each line in the file is parsed and new process object is created
				IProcess newProcess = new Process(
						ID++, arrivalTime, priority, burstTime, this.getRandomColor()
						);
				
				priorityQueue.add(newProcess);
			}
		}
		catch(IOException e) {
			System.out.println("\nFile not found, Please make sure the file path you provided is correct\n"
			e.printStackTrace();
		}

		/*

			Öncelik kuyruğu için tanımlanan karşılaştırıcıyı kullanarak işlemleri sıraladıktan sonra
			listede gezinmeyi kolaylaştırmak için Arraylist'e kopyalanırlar
		*/
		
		while (!priorityQueue.isEmpty())
		{
			this.allProcesses.add(priorityQueue.poll());
		}
		
		return this;
	}
	
	//Assigns processes to the queue they belong and runs the appropriate queue if needed
	@Override
	public void start() throws IOException, InterruptedException 
	{
		//yurutulen son islem
		IProcess lastProcess = null;
		
		while(!this.allProcesses.isEmpty())
		{
			//
			//O anda yürütülmesi gereken süreç aranı
			IProcess currentProcess = this.getCurrentProcess();
			
			if (currentProcess != null)
			{
				//Mevcut süreç daha yüksek önceliğe sahipse son süreç kesintiye uğrar
				if (lastProcess != null && currentProcess.hasHigherPriority(lastProcess))
				{
					this.interrupt(lastProcess);
					lastProcess = null;
				}
			
				//
				//Mevcut süreç gerçek zamanlı ise ilgili kuyruğa atanır ve çalıştırılır.
				if(currentProcess.isRealTime())
				{
					realTimeQueue.add(currentProcess);
					realTimeQueue.run();
					continue;
				}
				else
				{
					//
					//Kullanıcı işlemi ise kullanıcı işi kuyruğuna atanır.
					userJob.distribute(currentProcess);
				}
			}
				
			if (userJob.hasProcess())
			{
				lastProcess = userJob.run();
			}
			else
				Timer.tick();//çalıştırılacak kuyruk yoksa zamanlayıcı işaretlenir
		}
		
		//
		//Kullanıcı iş kuyruğunda hala işlem varsa, işlemlerinin yürütülmesine izin verilir
		while(userJob.hasProcess()) 
		{
			userJob.run();
		}
		
	}
	
	//Süreci kesintiye uğratır
	@Override
	public void interrupt(IProcess process) {
		pendingProcesses.add(lastProcess);
		//
		//Bir süreç kesintiye uğradığında durumu "hazır" olarak ayarlanır
		process.setState(State.READY);	
		Console.printProcessState(process, "is interrupted");
	}
	
	@Override
	public Color getRandomColor()
	{
		final int MIN = 0;
		final int MAX = 6;
		int randomIndex = ThreadLocalRandom.current().nextInt(MIN, MAX + 1);
		return this.colors[randomIndex];
	}
	
	//
	//İletilen bir tamsayı değeri için öncelik sıralaması değerini bulur
	private Priority convertToPriority(int priorityValue)
	{
		return switch(priorityValue) {
			case 0 -> Priority.REALTIME;
			case 1 -> Priority.HIGHESTPRIORITY;
			case 2 -> Priority.MEDIUMPRIORITY;
			case 3 -> Priority.LOWESTPRIORITY;
			default -> null;	
		};
	}

	//İletilen işlemin gelip gelmediğini kontrol eder
	@Override
	public boolean processHasArrived(IProcess process) {
		if (Timer.getCurrentTime() >= process.getArrivalTime()) {
			process.setState(State.READY);
			return true;
		}
		
		return false;
	}
	
	//Şu anda yürütülmesi gereken en yüksek önceliğe sahip süreci bulur
	@Override
	public IProcess getCurrentProcess()
	{
		for (IProcess currentProcess : new ArrayList<IProcess>(this.allProcesses))
		{
			if (this.processHasArrived(currentProcess)) 
			{
				//işlem geldiyse bekleme listesinden çıkarılır ve geri gönderilir
				this.allProcesses.remove(currentProcess);
				return currentProcess;
			}
		}
		
		return null;
	}
	
	//
	//Maksimum bekleme süresini aşan işlem(ler)in olup olmadığını kontrol eder
	public static void checkPendingProcesses()
	{
		for (IProcess process : new ArrayList<IProcess>(pendingProcesses))
		{
			//20 saniye geçmişse işlem sonlandırılır
			if (Dispatcher.shouldBeTerminated(process))
			{
				if(process.isRealTime())
					realTimeQueue.remove(process);
				else 
					userJob.remove(process);
				
				pendingProcesses.remove(process);
				Console.printProcessState(process, "exceeded limit");
			}
		}
	}
	
	//
	//Aktarılan işlemin maksimum bekleme süresini aşıp aşmadığını kontrol eder
	public static boolean shouldBeTerminated(IProcess process)
	{
		return !process.isOver() && process.getWaitingTime() >= maxWaitingTime;
	}
}
