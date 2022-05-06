package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private final Data data;
    private int start_index;
    private GPU owner;
    private boolean isProcessed;

    public DataBatch(Data data, int start_index, GPU owner)
    {
        this.data = data;
        this.start_index = start_index;
        this.owner = owner;
        this.isProcessed = false;
    }

    public GPU getOwner()
    {
        return owner;
    }

    public Data getData()
    {
        return data;
    }

    public void setProcessed()
    {
        isProcessed=true;
    }
}
