/** Hacia dónde cruza un auto el puente. Ya está hecho. */
public enum Sentido {
    NORTE, SUR;

    /** @return el sentido contrario */
    public Sentido otro() {
        return this == NORTE ? SUR : NORTE;
    }
}
