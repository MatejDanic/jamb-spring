package matej.models.payload.request;

public class PreferenceRequest {

	private Integer volume;

    protected PreferenceRequest() { }

    public Integer getVolume() {
        return volume;
    }
    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}