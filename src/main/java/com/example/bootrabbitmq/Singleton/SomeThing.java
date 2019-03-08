package com.example.bootrabbitmq.Singleton;

/**
 * 枚举实现单例
 */
public enum SomeThing {

    INSTANCE;

    private Resource instance;

    SomeThing() {
        instance = new Resource();
    }
    public Resource getInstance() {
        return instance;
    }
}
