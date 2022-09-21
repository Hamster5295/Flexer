package org.hamster5295.flexer;

/**
 * The Flexer main class. Defines an flexible value that can smoothly change into another by lerp.
 * @author Hamster
 * */

public class Flexer implements IUpdatable {

    /**
     * Get the current value.
     * @return current value
     * */
    public float getValue() {
        return value;
    }

    /**
     * Get the target value.
     * @return target value
     * */
    public float getTargetValue() {
        return targetValue;
    }

    /**
     * Get the animate speed
     * @return animate Speed
     * */
    public float getAnimateSpeed() {
        return animateSpeed;
    }

    /**
     * Get the minimum difference when calculating value
     * @return minimum difference
     * */
    public float getMinimumDifference() {
        return minimumDifference;
    }

    /**
     * Get the current state of this Flexer
     * @return the current state: READY, RUNNING,PAUSED, STOPPED
     * */
    public State getState() {
        return state;
    }

    private float value = 0;
    private float targetValue = 0;
    private float animateSpeed = 0.1f;
    private float minimumDifference = 0.01f;
    private boolean stopOnFinished = false;

    private State state;

    protected FlexerTask updateTask = (flexer) -> {
        value = animateSpeed * targetValue + (1 - animateSpeed) * value;
        if (Math.abs(value - targetValue) <= minimumDifference) {
            value = targetValue;
            flexer.finishedTask.run(Flexer.this);

            if (stopOnFinished) {
                stop();
            }
        }
    };

    protected FlexerTask lateUpdateTask = flexer -> {
    };
    protected FlexerTask pausedTask = flexer -> {
    };
    protected FlexerTask resumedTask = flexer -> {
    };
    protected FlexerTask finishedTask = flexer -> {
    };
    protected FlexerTask stoppedTask = flexer -> {
    };

    /**
     * Start the animation
     * */
    public void start() {
        if (state != State.READY) return;

        if (!FlexerUpdater.hasFlexer(this))
            FlexerUpdater.add(this);

        state = State.RUNNING;
    }

    /**
     * Pause the animation
     * */
    public void pause() {
        if (state == State.STOPPED) return;
        state = State.PAUSED;
        pausedTask.run(this);
    }

    /**
     * Resume the paused animation. Won't do anything when state is not PAUSED
     * */
    public void resume() {
        if (state != State.PAUSED) return;
        state = State.RUNNING;
        resumedTask.run(this);
    }

    /**
     * Stop the animation, regardless of the state
     * */
    public void stop() {
        if (state == State.STOPPED) return;
        state = State.STOPPED;
        stoppedTask.run(this);
        FlexerUpdater.remove(this);
    }

    /**
     * Set the target value.
     * */
    public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public void update() {
        updateTask.run(this);
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

        public Builder update(FlexerTask task) {
            flexer.updateTask = task;
            return this;
        }

        public Builder lateUpdate(FlexerTask task) {
            flexer.lateUpdateTask = task;
            return this;
        }

        public Builder onPaused(FlexerTask task) {
            flexer.pausedTask = task;
            return this;
        }

        public Builder onResumed(FlexerTask task) {
            flexer.resumedTask = task;
            return this;
        }

        public Builder onFinished(FlexerTask task) {
            flexer.finishedTask = task;
            return this;
        }

        public Builder onStopped(FlexerTask task) {
            flexer.stoppedTask = task;
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
