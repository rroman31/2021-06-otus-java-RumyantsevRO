package ru.otus.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMethodsAnnotationProcessingResult {
    private final List<Method> beforeAllMarked;
    private final List<Method> afterAllMarked;
    private final List<Method> testMarked;

    public TestMethodsAnnotationProcessingResult() {
        this.beforeAllMarked = new ArrayList<>();
        this.afterAllMarked = new ArrayList<>();
        this.testMarked = new ArrayList<>();
    }

    public List<Method> getBeforeAllMarked() {
        return Collections.unmodifiableList(beforeAllMarked);
    }

    public List<Method> getAfterAllMarked() {
        return Collections.unmodifiableList(afterAllMarked);
    }

    public List<Method> getTestMarked() {
        return Collections.unmodifiableList(testMarked);
    }

    public void addBeforeAllMarkedMethod(Method method) {
        this.beforeAllMarked.add(method);
    }

    public void addAfterAllMarked(Method method) {
        this.afterAllMarked.add(method);
    }

    public void addTestMarked(Method method) {
        this.testMarked.add(method);
    }
}
