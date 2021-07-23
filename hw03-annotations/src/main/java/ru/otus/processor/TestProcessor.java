package ru.otus.processor;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;
import ru.otus.model.TestMethodResult;
import ru.otus.model.TestMethodsAnnotationProcessingResult;
import ru.otus.model.TestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestProcessor {
    private final String PASSED = "PASSED";
    private final String FAILED = "FAILED";

    public void run(Class... classes) {
        for (Class clazz : classes) {
            TestMethodsAnnotationProcessingResult testMethodsAnnotationProcessingResult = createTestPlan(clazz);
            TestResult testResult = RunTestCycle(testMethodsAnnotationProcessingResult, clazz);
            printResult(testResult, clazz);
        }
    }

    private TestMethodsAnnotationProcessingResult createTestPlan(Class clazz) {
        TestMethodsAnnotationProcessingResult testMethodsAnnotationProcessingResult = new TestMethodsAnnotationProcessingResult();
        for (Method method : clazz.getMethods()) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length > 1) {
                throw new TestException("Method marked more than one annotation");
            }
            processingMethodAnnotations(annotations, testMethodsAnnotationProcessingResult, method);
        }
        return testMethodsAnnotationProcessingResult;
    }

    private void processingMethodAnnotations(Annotation[] annotations, TestMethodsAnnotationProcessingResult testMethodsAnnotationProcessingResult,
                                             Method method) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof After) {
                testMethodsAnnotationProcessingResult.addAfterAllMarked(method);
            }
            if (annotation instanceof Before) {
                testMethodsAnnotationProcessingResult.addBeforeAllMarkedMethod(method);
            }
            if (annotation instanceof Test) {
                testMethodsAnnotationProcessingResult.addTestMarked(method);
            }
        }
    }

    private TestResult RunTestCycle(TestMethodsAnnotationProcessingResult testMethodsAnnotationProcessingResult, Class clazz) {
        TestResult testResult = new TestResult();
        for (Method testMethod : testMethodsAnnotationProcessingResult.getTestMarked()) {
            Object object = instantiateObjectAndThrowExceptionIfNeed(clazz);

            for (Method method : testMethodsAnnotationProcessingResult.getBeforeAllMarked()) {
                invokeAndThrowExceptionIfNeed(method, object);
            }

            try {
                testMethod.invoke(object);
                testResult.incrementPassedCount();
                testResult.addMethodResult(new TestMethodResult(testMethod, true, null));
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    testResult.incrementFailCount();
                    testResult.addMethodResult(new TestMethodResult(testMethod, false,
                            ((InvocationTargetException) e).getTargetException()));
                } else {
                    testResult.incrementFailCount();
                    testResult.addMethodResult(new TestMethodResult(testMethod, false, e));
                }
            }

            for (Method method : testMethodsAnnotationProcessingResult.getAfterAllMarked()) {
                invokeAndThrowExceptionIfNeed(method, object);
            }
        }
        return testResult;
    }

    private void invokeAndThrowExceptionIfNeed(Method method, Object object) {
        try {
            method.invoke(object);
        } catch (Exception e) {
            throw new TestException("Method invoke error", e);
        }
    }

    private <T> Object instantiateObjectAndThrowExceptionIfNeed(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new TestException("Object creation error", e);
        }
    }

    private void printResult(TestResult testResult, Class clazz) {
        String headerTemplate = " %n Class name: %s; Tests count: %d; Test passed: %d; Tests failed: %d  %n";
        System.out.printf((headerTemplate) + "%n", clazz.getName(), testResult.getTestMethodResults().size(),
                testResult.getPassedCount(), testResult.getFailCount());

        String template = "Method name: %s ; result: %s";
        String errorTemplate = "Method error: %s";

        for (TestMethodResult testMethodResult : testResult.getTestMethodResults()) {
            System.out.printf((template) + "%n", testMethodResult.getMethod().getName(), testMethodResult.getPassed() ? PASSED : FAILED);
            if (!testMethodResult.getPassed()) {
                System.out.printf((errorTemplate) + "%n", testMethodResult.getException().getMessage());
            }
        }
    }
}
