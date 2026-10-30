package com.dyad.hrms.utils;

import com.dyad.hrms.models.Employee;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {

    private static final int SUFFIX_BOUND = 1_000_000;

    private TestDataFactory() {
    }

    /** The demo site is shared, so names carry a random suffix to stay distinguishable. */
    public static Employee uniqueEmployee() {
        return new Employee("Auto" + randomSuffix(), "Tester" + randomSuffix());
    }

    public static String uniqueLastName() {
        return "Updated" + randomSuffix();
    }

    public static String uniqueOtherId() {
        return "OID" + randomSuffix();
    }

    private static int randomSuffix() {
        return ThreadLocalRandom.current().nextInt(SUFFIX_BOUND);
    }
}
