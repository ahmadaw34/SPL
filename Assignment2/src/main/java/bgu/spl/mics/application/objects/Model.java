package bgu.spl.mics.application.objects;

import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import org.junit.Test;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status { PreTrained , Training , Trained , Tested }
    public enum Result { None , Good , Bad }
    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String name,Data data,Student student)
    {
        this.name=name;
        this.data=data;
        this.student=student;
        result= Result.None;
        status=Status.PreTrained;
    }

    public String getName()
    {
        return name;
    }

    public Data getData()
    {
        return data;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setTrained(){status=Status.Trained;}
    public void setTraining(){status=Status.Training;}
    public void setTested(){status=Status.Tested;}

    public void setResult(Result r)
    {
        result=r;
    }

    public Result getResult() {
        return result;
    }

    public boolean isGood() {
        return result.equals(Result.Good);
    }

    public boolean isTrainedOrTested(){
        if(status.equals(Status.Trained) || status.equals(Status.Tested))
            return true;
        return false;
    }

    public String getStatus() {
        if(status.equals(Status.PreTrained))return "PreTrained";
        else if(status.equals(Status.Training))return "Training";
        else if(status.equals(Status.Tested))return "Tested";
        return "Trained";
    }

    public String getResults()
    {
        if(result.equals(Result.Good))return "Good";
        else if(result.equals(Result.Bad))return "Bad";
        return "None";
    }

}
