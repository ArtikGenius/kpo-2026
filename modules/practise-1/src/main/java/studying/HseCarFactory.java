package studying;

import java.util.ArrayList;
import java.util.List;

public class HseCarFactory {
    private int carNumber = 0;
    private final List<Customer> customers = new ArrayList<>();
    private final List<Car> cars = new ArrayList<>();
    public void SaleCar(){
        customers.stream()
                .filter(customer -> customer.getCar() == null && !cars.isEmpty())
                .forEach(customer -> customer.setCar(cars.removeFirst())
        );
        cars.clear();
    }
    public void addCar(int engineSize){
        cars.add(new Car(carNumber++, engineSize));
    }
    public void addCustomer(Customer customer){
        customers.add(customer);
    }
    public void printCars(){
        System.out.println(cars);
    }
    public void printCustomers(){
        System.out.println(customers);
    }
}
