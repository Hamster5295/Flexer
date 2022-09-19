package org.hamster5295.flexer;

public interface IFlexerEvent {
    void onLateUpdate(Flexer f);

    void onPaused(Flexer f);

    void onResumed(Flexer f);

    void onFinished(Flexer f);

    void onStopped(Flexer f);
}
