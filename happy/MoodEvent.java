package happy;
import java.util.EventObject;

// should be immutable
public class MoodEvent extends EventObject {

    private Mood _mood;

    public MoodEvent( Object source, Mood mood ) {
        super( source );
        _mood = mood;
    }

    public Mood mood() {
        return _mood;
    }

}
