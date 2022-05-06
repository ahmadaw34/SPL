package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private final Type type;
    private final int size;
    private int processed;

    public Data(String type, int size) {
        if(type.equals("Images")) this.type = Type.Images;
        else if(type.equals("Text")) this.type = Type.Text;
        else this.type = Type.Tabular;
        this.size = size;
        this.processed = 0;
    }

    public int getSize()
    {
        return size;
    }

    public String getType() {
        if(type.equals(Type.Images))
            return "Images";
        else if(type.equals(Type.Text))return "Text";
        return "Tabular";
    }

    public void increaseProcessed(){
        this.processed += 1000;
    }
}
