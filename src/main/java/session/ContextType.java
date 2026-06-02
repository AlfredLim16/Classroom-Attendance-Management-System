package session;

public enum ContextType {
    CLASSROOM(1, "Classroom"),
    SCHOOL_EVENT(2, "School Event");

    private final int contextId;
    private final String contextName;

    ContextType(int contextId, String contextName){
        this.contextId = contextId;
        this.contextName = contextName;
    }

    public int getContextId(){
        return contextId;
    }

    public String getContextName(){
        return contextName;
    }

    public static ContextType fromId(int id){
        for(ContextType c : values()){
            if(c.contextId == id){
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown contextId: " + id);
    }
}
