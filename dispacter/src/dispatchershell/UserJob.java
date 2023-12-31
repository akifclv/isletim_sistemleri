package dispatchershell;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

// cok beslemeli 3 levelli kuyruk kullanici proseslerini yonetiyor
public class UserJob implements IUserJob{
	
	private Queue<IProcess>[] processQueue;
	private final int HIGHESTPRIORITY = 0;
	private final int MEDIUMPRIORITY = 1;
	private final int LOWESTPRIORITY = 2;
	private final int SIZE = 3;
	private int quantum;
	
	
	public UserJob(int quantum) {
		
		this.quantum = quantum;
		processQueue = new LinkedList[SIZE];
				
		//coklu kuru beslemeli kuyruk
		processQueue[HIGHESTPRIORITY] = new LinkedList<IProcess>();
		processQueue[MEDIUMPRIORITY] = new LinkedList<IProcess>();
		processQueue[LOWESTPRIORITY] = new LinkedList<IProcess>();
	}
	
	@Override
	public IProcess run() throws IOException, InterruptedException {
		IProcess currentProcess = null;
		
		for(int i = 0; i < SIZE; i++)
		{
			//yuksek oncelikli kuyrugu cagiriyor
			if(!processQueue[i].isEmpty()) {
				//The current process is pulled out from the queue
				currentProcess = this.processQueue[i].poll();
				
				//proses calisiyor
				State state = currentProcess.execute(this.quantum);
				
				if(state != State.TERMINATED) {
					//proses calistiktan sonra sonraki kuyruga geciyor
					
					if(i + 1 < SIZE) {
						currentProcess.reducePriority();
						this.processQueue[i + 1].add(currentProcess);
					}
					else
					{
						processQueue[i].add(currentProcess);  //Last queue
					}
				}
				return currentProcess;
			}	
		}
		
		return currentProcess;
	}
	
	//Kullanıcı işinin herhangi bir kuyrukta bir işlemi olup olmadığını kontrol eder.
	@Override	
	public boolean hasProcess() {	
		
		for(int priority = 0; priority < SIZE; priority++)
			if(!processQueue[priority].isEmpty()) return true;
	
		return false;
	}
	
	//prosesi kuyruktan kaldiriyor
	@Override
	public void remove(IProcess process) {
		int priorityValue = process.getPriority().ordinal();
		int level = priorityValue - 1;
		this.processQueue[level].remove(process);
	}
	
	//prosesi sonraki kuyruga atar
	@Override
	public void distribute(IProcess process) {
		
		switch(process.getPriority()) {
		
		case HIGHESTPRIORITY:{
			processQueue[HIGHESTPRIORITY].add(process);
			break;
		}
		case MEDIUMPRIORITY:{
			processQueue[MEDIUMPRIORITY].add(process);
			break;
		}
		case LOWESTPRIORITY:{
			processQueue[LOWESTPRIORITY].add(process);
			break;
		}
		default:
			break;
		
		}
	}
	
}
