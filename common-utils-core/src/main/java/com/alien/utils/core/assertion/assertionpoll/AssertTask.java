package com.alien.utils.core.assertion.assertionpoll;

public interface AssertTask {

    boolean execute();

    String getTaskName();

    String getTaskFailureMessage();
}
