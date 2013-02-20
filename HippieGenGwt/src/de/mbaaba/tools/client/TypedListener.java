package de.mbaaba.tools.client;

import java.io.Serializable;

public interface TypedListener<T extends Serializable> {
	void notifyMe(T aResult);

	void notifyFail(Throwable aCaught);
}
