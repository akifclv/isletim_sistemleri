package dispatchershell;

import java.io.IOException;

public class Process implements IProcess {
	private int Id;
	private int arrivalTime;
	private int burstTime;
	private int elapsedTime;
	private int lastExecutionTime;
	private Color color; 
	private Priority priority;
	private State state;
	
	public Process(int Id, int arrivalTime, Priority priority, int burstTime, Color color) {
		this.Id = Id;
		this.arrivalTime = arrivalTime;
		this.priority = priority;
		this.burstTime = burstTime;
		this.color = color;
		this.elapsedTime = 0;
		this.state = State.NEW;
	}
	
	//proses quantum suresi boyunca calisir
	@Override
	public State execute(int quantum) throws IOException, InterruptedException {
		while (quantum > 0 && !this.isOver()) 
		{
			Console.printProcessState(this, "is running");
			//timeri her saniye artırır
			Timer.tick();
			
			this.setState(State.RUNNING);
			this.elapsedTime++;
			quantum--;
		}

		//prosesi bekleme durumuna update ediyor
		this.setState(State.WAITING);
		
		//eger proses sonlarirsa
		if(this.isOver()) {
			Console.printProcessState(this, "has ended");
			this.setState(State.TERMINATED);
		}
		
		//Prosesin bekleme süresinin takibi için son yürütme süresi kaydedilir
		this.lastExecutionTime = Timer.getCurrentTime();
		
		return this.getState();
	}
	
	//proseslerin oncelik siralamasini karsilastirir
	@Override
	public boolean hasHigherPriority(IProcess other) {
		if (other == null) return true;
		return this.getPriority().ordinal() < other.getPriority().ordinal();
	}

	//sürecin bekleme süresini hesaplar
	@Override
	public int getWaitingTime() {
		return Timer.getCurrentTime() - this.lastExecutionTime;
	}
	
	//işlemin bitip bitmediğini kontrol eder
	@Override
	public boolean isOver() {
		return this.getBurstTime() == this.getElapsedTime();
	}
	
	//Sürecin gerçek zamanlı olup olmadığını söyler
	@Override
	public boolean isRealTime() {
		return this.priority == Priority.REALTIME;
	}
	@Override
	public int getId() {
		return Id;
	}
	@Override
	public State getState() {
		return state;
	}
	@Override
	public void setState(State state) {
		this.state = state;
	}
	@Override
	public Color getColor() {
		return color;
	}
	@Override
	public int getArrivalTime() {
		return arrivalTime;
	}
	@Override
	public int getBurstTime() {
		return burstTime;
	}
	@Override
	public int getElapsedTime() {
		return elapsedTime;
	}
	@Override
	public void setPriority(Priority priority) {
		this.priority=priority;
	}
	@Override
	public Priority getPriority() {
		return this.priority;
	}

	//reduces the priority of the process by 1
	@Override
	public void reducePriority() {
		
		switch(this.getPriority()) 
		{
			case HIGHESTPRIORITY:	this.setPriority(Priority.MEDIUMPRIORITY); 	break;
			case MEDIUMPRIORITY:	this.setPriority(Priority.LOWESTPRIORITY); 	break;
			case LOWESTPRIORITY:	break;
			default:
				break;
		}
	}
}