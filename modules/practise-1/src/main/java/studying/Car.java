package studying;

import lombok.ToString;

@ToString
public class Car {
    private final Engine engine;
    private final int vin;
    public Car(int vin, int size) {
        this.vin = vin;
        this.engine = new Engine(size);
    }
}
