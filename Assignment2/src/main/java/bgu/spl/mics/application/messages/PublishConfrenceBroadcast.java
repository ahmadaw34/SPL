package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class PublishConfrenceBroadcast implements Broadcast {
    private LinkedList<Model> models;

    public PublishConfrenceBroadcast(LinkedList<Model> models) {
        this.models = models;
    }

    public LinkedList<Model> getModels() {
        return models;
    }
}
