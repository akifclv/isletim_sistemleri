package dispatchershell;

import java.util.Comparator;

/*
  İşlemleri önceliklerine göre karşılaştırmak ve sıralamak için kullanılan bir karşılaştırıcı sınıf
  Not: Öncelikle öncelik, iki işlemi karşılaştırmak için bir parametre olarak kullanılıyor
öncelikleri eşitse varış süreleri bir faktör olarak kullanılıyor
*/
public class ProcessComparator implements Comparator<IProcess>{

	@Override
	public int compare(IProcess a, IProcess b) {
		int priorityLeft = a.getPriority().ordinal();
		int priorityRight = b.getPriority().ordinal();
		int arrivalTimeLeft = a.getArrivalTime();
		int arrivalTimeRight = b.getArrivalTime();
		
		final int LEFT = -1, RIGHT = 1, NONE = 0;

		if (priorityLeft == priorityRight)
		{
			if (arrivalTimeLeft < arrivalTimeRight)
				return LEFT;
			else if (arrivalTimeLeft > arrivalTimeRight)
				return RIGHT;
			else
				return NONE;
		}
		else if (priorityLeft < priorityRight)
			return LEFT;
		
		return RIGHT;
	}

}
