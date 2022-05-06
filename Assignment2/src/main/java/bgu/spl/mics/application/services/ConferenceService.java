package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.sql.SQLOutput;
import java.util.LinkedList;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private final ConfrenceInformation info;
    private LinkedList<Model> models;

    public ConferenceService(String name, ConfrenceInformation info) {
        super("ConferenceService");
        // TODO Implement this
        this.info = info;
        models = new LinkedList<>();
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent c) -> {
            if(c.getModel().isGood()) {
                addModel(c.getModel());
                info.addModel(c.getModel());
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast c) -> {
            if(c.getTime() == info.getDate()){
                sendBroadcast(new PublishConfrenceBroadcast(models));
                terminate();
            }
        });

        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast c) -> {
            terminate();
        });
    }
    public void addModel(Model model){
        models.add(model);
    }
}