package com.alien.utils.core.assertion.assertionpoll;


public class AssertionFactory {

    private static final int WAIT = 1000;

    public void performAssertion(AssertTask assertTask) {
        WaitUntilAsserter waitUntilAsserter = new WaitUntilAsserter(assertTask);
        waitUntilAsserter.assertTaskResult();
    }

    public void performAssertionOnce(AssertTask assertTask) {
        WaitUntilAsserter waitUntilAsserter = new WaitUntilAsserter(assertTask, WAIT);
        waitUntilAsserter.assertTaskResult();
    }
    
    public Boolean waitUntilTaskSuccess(AssertTask assertTask) {
    	WaitUntilAsserter waitUntilAsserter = new WaitUntilAsserter(assertTask);
        return waitUntilAsserter.taskResult();
    }
}
