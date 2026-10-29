package logika;

public class Document extends Item {
    private static final long serialVersionUID = 1L;

    private final String readableContent;
    private final boolean evidence;

    public Document (String id, String name, String description, String readableContent, boolean evidence) {
        super (id, name, description, false);
        this.readableContent = readableContent;
        this.evidence = evidence;
    }

    public String getReadableContent(){
        return readableContent;
    }

    @Override
    public boolean isEvidence() {
        return evidence;
    }
}
