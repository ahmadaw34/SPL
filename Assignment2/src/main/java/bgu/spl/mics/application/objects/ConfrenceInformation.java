package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> models;

    public ConfrenceInformation(String name,int date)
    {
        this.name=name;
        this.date=date;
        this.models = new LinkedList<>();
    }

    public void addModel(Model model){
        this.models.add(model);
    }

    public LinkedList<Model> getModels(){
        return models;
    }

    public String getName()
    {
        return name;
    }

    public int getDate()
    {
        return date;
    }

}
