package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private final Student student;
    private final LinkedList<Model> models;

    public StudentService(String name, Student student, LinkedList<Model> models) {
        super("StudentService");
        this.student = student;
        this.models = models;
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConfrenceBroadcast.class, (PublishConfrenceBroadcast c) -> {
            LinkedList<Model> goodModels = c.getModels();
            for (Model model : goodModels) {
                if (model.getStudent().equals(student)) {
                    student.addPublications();
                } else {
                    student.addpapersRead();
                }
            }
        });
        Thread t =new Thread(()->{
            String answer = "";
            for (int i = 0; i < models.size(); i++) {
                Future<String> f1 = sendEvent(new TrainModelEvent(models.get(i)));
                if (f1 != null) {
                    while (answer == null || answer.equals("")) {
                        answer = f1.get(100, TimeUnit.MILLISECONDS);
                        if (!Cluster.getInstance().getWorking()) {
                            terminate();
                            return;
                        }
                    }
                    if (answer.equals("Trained")) {
                        Future<String> f2 = sendEvent(new TestModelEvent(models.get(i)));
                        String answer2 = f2.get();
                        if (answer2 == null) {
                            terminate();
                            return;
                        }
                        if (answer2.equals("GOOD")) {
                            sendEvent(new PublishResultsEvent(models.get(i)));
                        }
                    }
                }
                if (!Cluster.getInstance().getWorking()) {
                    terminate();
                    return;
                }
            }
        });
        t.start();
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast c) -> {
            terminate();
        });
    }
}
