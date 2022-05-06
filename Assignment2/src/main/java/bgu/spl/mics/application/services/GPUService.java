package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;


/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent}
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;

    public GPUService(String name,GPU gpu) {
        super("GPUService");
        // TODO Implement this
        this.gpu=gpu;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeEvent(TrainModelEvent.class,(TrainModelEvent ev)->{
            ev.getModel().setTraining();
            gpu.setModel(ev.getModel());

            while(gpu.getTrainedData() < gpu.getTotal_data()) {
                gpu.train();
                if(!Cluster.getInstance().getWorking()){
                    complete(ev,"TerminatedProcess");
                    terminate();
                    return;
                }
            }
            gpu.increaseTicks();
            Cluster.getInstance().increaseGpuTime(gpu.getNumOfTicks());
            ev.getModel().setTrained();
            complete(ev, "Trained");

        });

        subscribeEvent(TestModelEvent.class,(TestModelEvent ev)->{
            gpu.setModel(ev.getModel());
            gpu.test(Math.random());
            ev.getModel().setTested();
            complete(ev,gpu.getResult_of_test());
        });

        subscribeBroadcast(TerminateBroadcast.class, c -> {
            terminate();
        });
    }
}
