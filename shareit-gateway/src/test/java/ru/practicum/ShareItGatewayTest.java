package ru.practicum;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ShareItGatewayTest
    extends TestCase {
    public ShareItGatewayTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ShareItGatewayTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }
}
