package org.atfl;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.atfl.runtime.ATFLRuntimeTest;

public class ATFLTests extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ATFLRuntimeTest.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
