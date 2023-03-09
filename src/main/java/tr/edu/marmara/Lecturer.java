package tr.edu.marmara;

import com.google.gson.JsonElement;

public class Lecturer extends Staff {
    public Lecturer(Settings s) {
        super(s);
    }

    public Lecturer(JsonElement element, Settings s) throws Exception {
        super(element, s);
    }
}
