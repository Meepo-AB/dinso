package se.meepo.dinso.service;

public record DemoProfile(
    String id,
    CustomerId customerId,
    PortalType portal,
    DemoRole role,
    String name,
    String description) {}
