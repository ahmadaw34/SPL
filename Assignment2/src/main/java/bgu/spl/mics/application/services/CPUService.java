package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;

/**
 * CPU service is responsible for handling the ....
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;

    public CPUService(String name, CPU cp) {
        super("CPUService");
        // TODO Implement this
        cpu = cp;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast c) -> {

            if (cpu.getDataToProcess() == null) {
                try {
                    DataBatch d2 =Cluster.getInstance().getUnprocessedData();
                    while(d2 == null) {
                        if(!Cluster.getInstance().getWorking()){
                            terminate();
                            return;
                        }
                        d2 = Cluster.getInstance().getUnprocessedData();
                    }
                    cpu.setDataToProcess(d2);
                } catch (Exception e) {
                    return;
                }
            }

            if (cpu.getDataToProcess() != null) {
                cpu.calculateTicks(cpu.getDataToProcess());

                if (cpu.getNumOfTicks() >= cpu.getWaitTime()) {
                    Cluster.getInstance().addProcessedData(cpu.getDataToProcess());
                    Cluster.getInstance().increaseNumOfDataProcessed();
                    cpu.reset();
                }
                Cluster.getInstance().increaseCpuTime();
                cpu.increaseTicks();
            }

        });

        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast c) -> {
            terminate();
        });
    }
}