package com.example.instagram.auth.domain;

import java.util.concurrent.TimeUnit;

public enum Expiration {

    ACCESS(TimeUnit.HOURS.toMillis(1))
    ,REFRESH(TimeUnit.DAYS.toMillis(1));

    private final long ttl;

    Expiration(long ttl){
        this.ttl = ttl;
    }

    public long getTtl(){
        return ttl;
    }
}
