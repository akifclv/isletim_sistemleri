package dispatchershell;

//program calisması boyunca burayı kullanir
public class Timer {
	private static final int ONESECOND = 0;
	private static int currentTime = 0;
	
	public static void tick()
	{
		//1 saniye bekler
		try
        {
            Thread.sleep(ONESECOND);
            currentTime++;
            /*

                Gecen her saniye süreçlerin bekleme listesi kontrol ediliyor
                Bu, maksimum bekleme süresini aşan herhangi bir işlemi sonlandırmak için yapılır.
            */

            Dispatcher.checkPendingProcesses();
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
	}
	
	public static int getCurrentTime() {
		return currentTime;
	}
}
