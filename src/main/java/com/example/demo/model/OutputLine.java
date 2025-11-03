package com.example.demo.model;

import java.util.Objects;

public class OutputLine {

    private final String name, transport, topSpeed;

    public OutputLine(String name, String transport, String topSpeed) {
        this.name = name;
        this.transport = transport;
        this.topSpeed = topSpeed;
    }

    public String getName() {
        return name;
    }

    public String getTransport() {
        return transport;
    }

    public String getTopSpeed() {
        return topSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputLine that = (OutputLine) o;
        return Objects.equals(name, that.name) && Objects.equals(transport, that.transport) && Objects.equals(topSpeed, that.topSpeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, transport, topSpeed);
    }
}
