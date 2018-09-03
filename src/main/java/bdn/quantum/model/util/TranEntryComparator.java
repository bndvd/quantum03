package bdn.quantum.model.util;

import java.util.Comparator;

import org.springframework.stereotype.Service;

import bdn.quantum.model.TranEntity;

@Service("tranEntityComparator")
public class TranEntryComparator implements Comparator<TranEntity> {

	@Override
	public int compare(TranEntity t1, TranEntity t2) {
		if (t1 != null && t2!= null) {
			if (t1.getTranDate().before(t2.getTranDate())) {
				return -1;
			}
			if (t1.getTranDate().after(t2.getTranDate())) {
				return 1;
			}
		}

		return 0;
	}

}
