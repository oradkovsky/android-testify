package com.shopify.testify;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.shopify.testify.annotation.TestifyLayout;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

// Consider subclassing ActivityTestRule
@SuppressWarnings({"unused", "WeakerAccess"})
public class ScreenshotTestRule<T extends Activity> extends ActivityTestRule<T> implements TestRule {

    private String testName;
    private Throwable throwable;
    @LayoutRes private int layoutId;

    public ScreenshotTestRule(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        testName = description.getTestClass().getSimpleName() + "_" + description.getMethodName();
        TestifyLayout testifyLayout = description.getAnnotation(TestifyLayout.class);
        if (testifyLayout != null) {
            layoutId = testifyLayout.layoutId();
        }
        return new ScreenshotStatement(super.apply(new BaseInterceptorStatement(base), description));
    }

    public void setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    protected void beforeActivityLaunched() {
        throwable = null;
    }

    private void afterTestStatement() {
        ScreenshotTest screenshotTest = new ScreenshotTest(layoutId);
        try {
            screenshotTest.assertSame();
            int a = 0;
        } catch (Throwable throwable) {
            this.throwable = throwable;
        }
    }

    private class BaseInterceptorStatement extends Statement {

        private final Statement base;

        public BaseInterceptorStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            base.evaluate();
            afterTestStatement();
        }
    }

    private class ScreenshotStatement extends Statement {

        private final Statement base;

        public ScreenshotStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                base.evaluate();
            } catch (Throwable ignored) {
            } finally {
                if (throwable != null) {
                    throw throwable;
                }
            }
        }
    }

    private class ScreenshotTest extends BaseScreenshotTest<ScreenshotTest> {

        public ScreenshotTest(@LayoutRes int layoutId) {
            super(layoutId);
        }

        @Override
        protected String getTestName() {
            return testName;
        }

        @Override
        protected Context getTestContext() {
            return InstrumentationRegistry.getContext();
        }

        @Override
        protected Activity getActivity() {
            return ScreenshotTestRule.this.getActivity();
        }

        @Override
        protected ScreenshotTest getThis() {
            return this;
        }
    }
}