package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;

import java.util.Timer;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private final long duration;
	private long tickTime;
	private Timer timer;

	public TimeService(long tickTime, long duration) {
		super("TimeService");
		// TODO Implement this
		this.duration = duration;
		this.tickTime = tickTime;
		timer = new Timer();
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		long num = 1;
		while (num < duration){
			if(num % 1000 == 0) {
				System.out.println(num + "/" + duration);
			}
			sendBroadcast(new TickBroadcast((int) num));
			try{
				Thread.sleep(tickTime);
			} catch (Exception e) {}
			num++;
			Cluster.getInstance().getTime().incrementAndGet();
		}

		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast c) -> {
			this.terminate();
		});
		sendBroadcast(new TerminateBroadcast());
		Cluster.getInstance().setNotWorking();
	}
}