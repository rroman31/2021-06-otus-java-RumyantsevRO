package ru.otus.homework;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class CustomerService {
    private final TreeMap<Customer, String> storage = new TreeMap<>();

    //todo: 3. надо реализовать методы этого класса
    //важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны

    public Map.Entry<Customer, String> getSmallest() {
        //Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        return createCopy(storage.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return createCopy(storage.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        storage.put(customer, data);
    }


    private Entry createCopy(Map.Entry<Customer, String> entry) {
        return Objects.isNull(entry) ? null : new Entry(entry);
    }

    private class Entry implements Map.Entry<Customer, String> {
        private final Customer key;
        private String value;

        public Entry(Customer key, String value) {
            this.key = key;
            this.value = value;
        }

        public Entry(Map.Entry<Customer, String> entry) {
            this.key = new Customer(entry.getKey());
            this.value = entry.getValue();
        }

        @Override
        public Customer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            this.value = value;
            return this.value;
        }
    }
}
