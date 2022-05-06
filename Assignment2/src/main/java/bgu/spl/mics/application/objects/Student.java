package bgu.spl.mics.application.objects;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;

    public Student(String name, String department, String status)
    {
        this.name = name;
        this.department = department;
        publications = 0;
        papersRead = 0;
        if(status.equals("MSc"))
            this.status=Degree.MSc;
        else this.status=Degree.PhD;
    }

    public String getName()
    {
        return name;
    }

    public  String getDepartment()
    {
        return department;
    }

    public Degree getStatus()
    {
        return status;
    }

    public int getPublications()
    {
        return  publications;
    }

    public int getPapersRead()
    {
        return papersRead;
    }

    public void setStatus(Degree status)
    {
        this.status=status;
    }

    public void addPublications()
    {
        publications++;
    }

    public void addpapersRead()
    {
        papersRead++;
    }
}
