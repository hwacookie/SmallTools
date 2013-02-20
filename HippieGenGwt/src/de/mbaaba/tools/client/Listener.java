package de.mbaaba.tools.client;

public interface Listener {
	void notifyMe();

	void notifyFail(Throwable aCaught);
}
