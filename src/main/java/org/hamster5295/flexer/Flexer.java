package org.hamster5295.flexer;

public class Flexer implements IUpdatable {

    public float getValue() {
        return value;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public float getAnimateSpeed() {
        return animateSpeed;
    }

    public float getMinimumDifference() {
        return minimumDifference;
    }

    public State getState() {
        return state;
    }

    protected IFlexerEvent getEvent() {
        return event;
    }

    private float value = 0;
    private float targetValue = 0;
    private float animateSpeed = 0.1f;
    private float minimumDifference = 0.01f;
    private boolean stopOnFinished = false;

    private State state;

    private Runnable updateTask = () -> {
        value = animateSpeed * targetValue + (1 - animateSpeed) * value;
        if (Math.abs(value - targetValue) <= minimumDifference) {
            value = targetValue;
            Flexer.this.event.onFinished(Flexer.this);

            if (stopOnFinished) {
                stop();
            }
        }
    };

    private IFlexerEvent event;

    public void start() {
        if (state != State.READY) return;

        if (!FlexerUpdater.hasFlexer(this))
            FlexerUpdater.add(this);

        state = State.RUNNING;
    }

    public void pause() {
        if (state == State.STOPPED) return;
        state = State.PAUSED;
        event.onPaused(this);
    }

    public void resume() {
        if (state != State.PAUSED) return;
        state = State.RUNNING;
        event.onResumed(this);
    }

    public void stop() {
        if (state == State.STOPPED) return;
        state = State.STOPPED;
        event.onStopped(this);
    }

    public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public void update() {
        updateTask.run();
    }

    public static class Builder {
        private final Flexer flexer;

        public Builder() {
            flexer = new Flexer();
        }

        public Builder value(float val) {
            flexer.value = val;
            return this;
        }

        public Builder targetValue(float targetVal) {
            flexer.targetValue = targetVal;
            return this;
        }

        public Builder animateSpeed(float speed) {
            flexer.animateSpeed = speed;
            return this;
        }

        public Builder minimumDifference(float diff) {
            flexer.minimumDifference = diff;
            return this;
        }

        public Builder stopOnFinished() {
            flexer.stopOnFinished = true;
            return this;
        }

        public Builder onUpdate(Runnable task) {
            flexer.updateTask = task;
            return this;
        }

        public Builder event(IFlexerEvent event) {
            flexer.event = event;
            return this;
        }

        public Flexer build() {
            flexer.state = State.READY;
            return flexer;
        }
    }

    public enum State {
        READY, RUNNING, PAUSED, STOPPED
    }
}
